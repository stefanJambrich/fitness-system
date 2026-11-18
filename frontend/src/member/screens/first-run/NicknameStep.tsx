import { Alert, Button, Group, Paper, Stack, TextInput, Title } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useEffect, useState } from 'react'
import { apiErrorCode } from '../../../api/errors'
import type { MemberIdentity } from '../../../api/generated'
import { isPersistent } from '../../../crypto/keyStore'
import { strings } from '../../../common/strings'
import { createIdentity } from '../../identity'

type Props = {
  onBack: () => void
  onCreated: (profile: MemberIdentity) => void
}

const NICKNAME_MAX = 32

const isValidNickname = (nickname: string): boolean => {
  const trimmed = nickname.trim()
  // eslint-disable-next-line no-control-regex
  return trimmed.length >= 1 && trimmed.length <= NICKNAME_MAX && !/[#\u0000-\u001f\u007f-\u009f]/.test(trimmed)
}

export const NicknameStep = ({ onBack, onCreated }: Props) => {
  const [error, setError] = useState<string>()
  const [submitting, setSubmitting] = useState(false)
  const [persistent, setPersistent] = useState(true)
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: { nickname: '' },
    validate: {
      nickname: (value) => (isValidNickname(value) ? null : strings.nicknameInvalid),
    },
  })

  useEffect(() => {
    isPersistent().then(setPersistent)
  }, [])

  const submit = async ({ nickname }: { nickname: string }) => {
    setSubmitting(true)
    setError(undefined)
    try {
      onCreated(await createIdentity(nickname.trim()))
    } catch (e) {
      const code = await apiErrorCode(e)
      if (code === 'VALIDATION') {
        form.setFieldError('nickname', strings.nicknameInvalid)
      } else {
        setError(code === 'RATE_LIMITED' ? strings.errorRateLimited : strings.errorGeneric)
      }
      setSubmitting(false)
    }
  }

  return (
    <Paper withBorder p="lg">
      <form onSubmit={form.onSubmit(submit)} noValidate>
        <Stack>
          <Title order={2} size="h3">{strings.nicknameTitle}</Title>
          {!persistent && (
            <Alert color="yellow" title={strings.notPersistentTitle}>{strings.notPersistentWarning}</Alert>
          )}
          <TextInput
            label={strings.nicknameLabel}
            description={strings.nicknameHint}
            maxLength={NICKNAME_MAX}
            autoComplete="nickname"
            autoFocus
            key={form.key('nickname')}
            {...form.getInputProps('nickname')}
          />
          {error && <Alert color="red">{error}</Alert>}
          <Group justify="space-between">
            <Button variant="subtle" onClick={onBack} disabled={submitting}>{strings.back}</Button>
            <Button type="submit" loading={submitting}>{strings.nicknameSubmit}</Button>
          </Group>
        </Stack>
      </form>
    </Paper>
  )
}