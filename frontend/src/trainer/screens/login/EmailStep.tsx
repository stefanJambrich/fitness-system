import { Alert, Button, Paper, Stack, Text, TextInput, Title } from '@mantine/core'
import { isEmail, useForm } from '@mantine/form'
import { useState } from 'react'
import { trainerAuthApi } from '../../../api/client'
import { strings } from '../../../common/strings'
import { errorMessage } from '../../../common/utils/errorMessage'
import { REQUEST_ERRORS } from './loginErrors'

type Props = {
  initialEmail: string
  sessionExpired: boolean
  onCodeSent: (email: string) => void
}

const EMAIL_MAX = 254
const DISPLAY_NAME_MAX = 64

const isValidDisplayName = (name: string): boolean => {
  const trimmed = name.trim()
  return trimmed.length >= 1 && trimmed.length <= DISPLAY_NAME_MAX && !/\p{Cc}/u.test(trimmed)
}

export const EmailStep = ({ initialEmail, sessionExpired, onCodeSent }: Props) => {
  const [registering, setRegistering] = useState(false)
  const [error, setError] = useState<string>()
  const [submitting, setSubmitting] = useState(false)
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: { email: initialEmail, displayName: '' },
    validate: {
      email: (value) => isEmail(strings.trainerEmailInvalid)(value.trim()),
      displayName: (value) => (!registering || isValidDisplayName(value) ? null : strings.trainerDisplayNameInvalid),
    },
  })

  const submit = async (values: { email: string, displayName: string }) => {
    const email = values.email.trim()
    setSubmitting(true)
    setError(undefined)
    try {
      if (registering) {
        await trainerAuthApi.registerTrainer({
          registerTrainerRequest: { email, displayName: values.displayName.trim() },
        })
      } else {
        await trainerAuthApi.requestLoginCode({ requestLoginCodeRequest: { email } })
      }
      onCodeSent(email)
    } catch (e) {
      setError(await errorMessage(e, REQUEST_ERRORS))
      setSubmitting(false)
    }
  }

  const toggleRegistering = () => {
    setRegistering(!registering)
    setError(undefined)
    form.clearErrors()
  }

  return (
    <Paper withBorder p="lg">
      <form onSubmit={form.onSubmit(submit)} noValidate>
        <Stack>
          <Title order={2} size="h3">{registering ? strings.trainerRegisterTitle : strings.trainerLoginTitle}</Title>
          {sessionExpired && <Alert color="yellow">{strings.trainerSessionExpired}</Alert>}
          <Text c="dimmed">{strings.trainerLoginIntro}</Text>
          <TextInput
            type="email"
            label={strings.trainerEmailLabel}
            maxLength={EMAIL_MAX}
            autoComplete="email"
            autoFocus
            key={form.key('email')}
            {...form.getInputProps('email')}
          />
          {registering && (
            <TextInput
              label={strings.trainerDisplayNameLabel}
              maxLength={DISPLAY_NAME_MAX}
              autoComplete="name"
              key={form.key('displayName')}
              {...form.getInputProps('displayName')}
            />
          )}
          {error && <Alert color="red">{error}</Alert>}
          <Button type="submit" size="md" fullWidth loading={submitting}>{strings.trainerSendCode}</Button>
          <Button variant="subtle" fullWidth onClick={toggleRegistering} disabled={submitting}>
            {registering ? strings.trainerHaveAccount : strings.trainerNoAccount}
          </Button>
        </Stack>
      </form>
    </Paper>
  )
}