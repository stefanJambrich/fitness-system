import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { apiErrorCode } from '../api/errors'
import type { MemberIdentity } from '../api/generated'
import { isPersistent } from '../crypto/keyStore'
import { strings } from '../shared/strings'
import { createIdentity } from './identity'

type Props = {
  onCreated: (profile: MemberIdentity) => void
}

const NICKNAME_MAX = 32

function isValidNickname(nickname: string): boolean {
  const trimmed = nickname.trim()
  // eslint-disable-next-line no-control-regex
  return trimmed.length >= 1 && trimmed.length <= NICKNAME_MAX && !/[#\u0000-\u001f\u007f-\u009f]/.test(trimmed)
}

export function FirstRunScreen({ onCreated }: Props) {
  const [step, setStep] = useState<'choose' | 'nickname'>('choose')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState<string>()
  const [submitting, setSubmitting] = useState(false)
  const [persistent, setPersistent] = useState(true)

  useEffect(() => {
    isPersistent().then(setPersistent)
  }, [])

  async function submit(event: FormEvent) {
    event.preventDefault()
    if (!isValidNickname(nickname)) {
      setError(strings.nicknameInvalid)
      return
    }
    setSubmitting(true)
    setError(undefined)
    try {
      onCreated(await createIdentity(nickname.trim()))
    } catch (e) {
      const code = await apiErrorCode(e)
      setError(code === 'RATE_LIMITED' ? strings.errorRateLimited
        : code === 'VALIDATION' ? strings.nicknameInvalid
          : strings.errorGeneric)
      setSubmitting(false)
    }
  }

  if (step === 'choose') {
    return (
      <section>
        <p>{strings.firstRunIntro}</p>
        <button type="button" onClick={() => setStep('nickname')}>{strings.firstRunStart}</button>
        <button type="button" disabled title={strings.comingSoon}>{strings.firstRunOtherDevice}</button>
        <button type="button" disabled title={strings.comingSoon}>{strings.firstRunRestoreEmail}</button>
      </section>
    )
  }

  return (
    <form onSubmit={submit}>
      {!persistent && <p role="alert">{strings.notPersistentWarning}</p>}
      <label>
        {strings.nicknameLabel}
        <input
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          maxLength={NICKNAME_MAX}
          autoFocus
          required
        />
      </label>
      <p>{strings.nicknameHint}</p>
      {error && <p role="alert">{error}</p>}
      <button type="button" onClick={() => setStep('choose')} disabled={submitting}>{strings.back}</button>
      <button type="submit" disabled={submitting}>{strings.nicknameSubmit}</button>
    </form>
  )
}