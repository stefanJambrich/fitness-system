import { Alert, Button, Checkbox, Group, Input, Paper, PinInput, Stack, Text, Title } from '@mantine/core'
import { useState } from 'react'
import type { FormEvent } from 'react'
import { trainerAuthApi } from '../../../api/client'
import type { TrainerProfile } from '../../../api/generated'
import { strings } from '../../../common/strings'
import { errorMessage } from '../../../common/utils/errorMessage'
import { CODE_ERRORS } from './loginErrors'

type Props = {
  email: string
  onBack: () => void
  onLoggedIn: (trainer: TrainerProfile) => void
}

const CODE_LENGTH = 6

export const CodeStep = ({ email, onBack, onLoggedIn }: Props) => {
  const [code, setCode] = useState('')
  const [remember, setRemember] = useState(false)
  const [codeError, setCodeError] = useState<string>()
  const [newCodeSent, setNewCodeSent] = useState(false)
  const [busy, setBusy] = useState<'verify' | 'resend'>()

  const verify = async (event: FormEvent) => {
    event.preventDefault()
    setBusy('verify')
    setCodeError(undefined)
    setNewCodeSent(false)
    try {
      onLoggedIn(await trainerAuthApi.verifyLoginCode({
        verifyLoginCodeRequest: { email, code, rememberDevice: remember },
      }))
    } catch (e) {
      setCodeError(await errorMessage(e, CODE_ERRORS))
      setBusy(undefined)
    }
  }

  const resend = async () => {
    setBusy('resend')
    setCodeError(undefined)
    setNewCodeSent(false)
    try {
      await trainerAuthApi.requestLoginCode({ requestLoginCodeRequest: { email } })
      setCode('')
      setNewCodeSent(true)
    } catch (e) {
      setCodeError(await errorMessage(e, CODE_ERRORS))
    } finally {
      setBusy(undefined)
    }
  }

  const changeCode = (value: string) => {
    setCode(value)
    setCodeError(undefined)
  }

  return (
    <Paper withBorder p="lg">
      <form onSubmit={verify}>
        <Stack>
          <Title order={2} size="h3">{strings.trainerCodeTitle}</Title>
          <Text c="dimmed">
            {strings.trainerCodeSent}{' '}
            <Text span fw={600} c="var(--mantine-color-text)" style={{ overflowWrap: 'anywhere' }}>{email}</Text>
          </Text>
          <Input.Wrapper label={strings.trainerCodeLabel} labelElement="div" error={codeError}>
            <PinInput
              mt={4}
              size="md"
              gap="xs"
              length={CODE_LENGTH}
              type="number"
              inputMode="numeric"
              oneTimeCode
              autoFocus
              ariaLabel={strings.trainerCodeLabel}
              value={code}
              onChange={changeCode}
              error={!!codeError}
            />
          </Input.Wrapper>
          <Checkbox
            label={strings.trainerRememberDevice}
            checked={remember}
            onChange={(e) => setRemember(e.currentTarget.checked)}
          />
          {newCodeSent && <Alert>{strings.trainerNewCodeSent}</Alert>}
          <Button
            type="submit"
            size="md"
            fullWidth
            loading={busy === 'verify'}
            disabled={code.length !== CODE_LENGTH || busy === 'resend'}
          >
            {strings.trainerLogIn}
          </Button>
          <Group justify="space-between" gap="xs">
            <Button variant="subtle" px="xs" onClick={onBack} disabled={busy !== undefined}>
              {strings.trainerChangeEmail}
            </Button>
            <Button variant="subtle" px="xs" onClick={resend} loading={busy === 'resend'} disabled={busy === 'verify'}>
              {strings.trainerNewCode}
            </Button>
          </Group>
        </Stack>
      </form>
    </Paper>
  )
}