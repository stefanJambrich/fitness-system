import { useState } from 'react'
import type { FormEvent } from 'react'
import { trainerAuthApi } from '../api/client'
import { apiErrorCode } from '../api/errors'
import type { TrainerProfile } from '../api/generated'
import { strings } from '../shared/strings'

type Props = {
  onLoggedIn: (trainer: TrainerProfile) => void
}

const CODE_LENGTH = 6

const ERROR_MESSAGES: Record<string, string> = {
  OTP_INVALID: strings.trainerCodeInvalid,
  OTP_EXPIRED: strings.trainerCodeExpired,
  OTP_LOCKED: strings.trainerCodeLocked,
  RATE_LIMITED: strings.trainerRateLimited,
  VALIDATION: strings.trainerFormInvalid,
}

export function TrainerLoginScreen({ onLoggedIn }: Props) {
  const [registering, setRegistering] = useState(false)
  const [step, setStep] = useState<'email' | 'code'>('email')
  const [email, setEmail] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [code, setCode] = useState('')
  const [remember, setRemember] = useState(false)
  const [error, setError] = useState<string>()
  const [submitting, setSubmitting] = useState(false)

  async function run(action: () => Promise<void>) {
    setSubmitting(true)
    setError(undefined)
    try {
      await action()
    } catch (e) {
      setError(ERROR_MESSAGES[(await apiErrorCode(e)) ?? ''] ?? strings.errorGeneric)
    } finally {
      setSubmitting(false)
    }
  }

  function sendCode(event: FormEvent) {
    event.preventDefault()
    run(async () => {
      if (registering) {
        await trainerAuthApi.registerTrainer({
          registerTrainerRequest: { email: email.trim(), displayName: displayName.trim() },
        })
      } else {
        await trainerAuthApi.requestLoginCode({ requestLoginCodeRequest: { email: email.trim() } })
      }
      setCode('')
      setStep('code')
    })
  }

  function verify(event: FormEvent) {
    event.preventDefault()
    run(async () => {
      onLoggedIn(await trainerAuthApi.verifyLoginCode({
        verifyLoginCodeRequest: { email: email.trim(), code, rememberDevice: remember },
      }))
    })
  }

  function backToEmail() {
    setStep('email')
    setError(undefined)
  }

  function toggleRegistering() {
    setRegistering(!registering)
    setError(undefined)
  }

  if (step === 'code') {
    return (
      <form onSubmit={verify}>
        <p>{strings.trainerCodeSent} <strong>{email.trim()}</strong></p>
        <label>
          {strings.trainerCodeLabel}
          <input
            value={code}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, CODE_LENGTH))}
            inputMode="numeric"
            autoComplete="one-time-code"
            autoFocus
            required
          />
        </label>
        <label>
          <input type="checkbox" checked={remember} onChange={(e) => setRemember(e.target.checked)} />
          {strings.trainerRememberDevice}
        </label>
        {error && <p role="alert">{error}</p>}
        <button type="button" onClick={backToEmail} disabled={submitting}>{strings.trainerNewCode}</button>
        <button type="submit" disabled={submitting || code.length !== CODE_LENGTH}>{strings.trainerLogIn}</button>
      </form>
    )
  }

  return (
    <form onSubmit={sendCode}>
      <h2>{registering ? strings.trainerRegisterTitle : strings.trainerLoginTitle}</h2>
      <label>
        {strings.trainerEmailLabel}
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          autoComplete="email"
          maxLength={254}
          autoFocus
          required
        />
      </label>
      {registering && (
        <label>
          {strings.trainerDisplayNameLabel}
          <input value={displayName} onChange={(e) => setDisplayName(e.target.value)} maxLength={64} required />
        </label>
      )}
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={submitting}>{strings.trainerSendCode}</button>
      <button type="button" onClick={toggleRegistering} disabled={submitting}>
        {registering ? strings.trainerHaveAccount : strings.trainerNoAccount}
      </button>
    </form>
  )
}