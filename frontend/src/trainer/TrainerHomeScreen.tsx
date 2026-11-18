import { useEffect, useState } from 'react'
import { trainerAuthApi } from '../api/client'
import type { TrainerProfile, TrainerSessionInfo } from '../api/generated'
import { strings } from '../shared/strings'
import { isSessionError } from './sessionErrors'

type Props = {
  trainer: TrainerProfile
  onLoggedOut: () => void
}

export function TrainerHomeScreen({ trainer, onLoggedOut }: Props) {
  const [sessions, setSessions] = useState<TrainerSessionInfo[]>()
  const [error, setError] = useState<string>()

  async function fail(e: unknown) {
    if (await isSessionError(e)) {
      onLoggedOut()
    } else {
      setError(strings.errorGeneric)
    }
  }

  useEffect(() => {
    trainerAuthApi.listSessions()
      .then(setSessions)
      .catch(async (e) => {
        if (await isSessionError(e)) {
          onLoggedOut()
        } else {
          setError(strings.errorGeneric)
        }
      })
  }, [onLoggedOut])

  async function logOut() {
    try {
      await trainerAuthApi.logout()
      onLoggedOut()
    } catch (e) {
      await fail(e)
    }
  }

  async function revoke(ids: string[]) {
    setError(undefined)
    try {
      await Promise.all(ids.map((sessionId) => trainerAuthApi.revokeSession({ sessionId })))
      setSessions((current) => current?.filter((s) => !ids.includes(s.id)))
    } catch (e) {
      await fail(e)
    }
  }

  const otherIds = sessions?.filter((s) => !s.current).map((s) => s.id) ?? []

  return (
    <section>
      <p>
        {strings.trainerLoggedInAs} <strong>{trainer.displayName}</strong> ({trainer.email})
      </p>
      <button type="button" onClick={logOut}>{strings.trainerLogOut}</button>

      <h2>{strings.trainerDevicesTitle}</h2>
      {error && <p role="alert">{error}</p>}
      {sessions === undefined ? <p>{strings.loading}</p> : (
        <ul>
          {sessions.map((session) => (
            <li key={session.id}>
              <strong>{session.deviceLabel ?? strings.trainerUnknownDevice}</strong>
              {session.current && ` (${strings.trainerThisDevice})`}
              <br />
              {strings.trainerLastUsed} {session.lastUsedAt.toLocaleString()}
              {session.remembered && ` · ${strings.trainerRemembered}`}
              {!session.current && (
                <button type="button" onClick={() => revoke([session.id])}>{strings.trainerLogOutDevice}</button>
              )}
            </li>
          ))}
        </ul>
      )}
      {otherIds.length > 0 && (
        <button type="button" onClick={() => revoke(otherIds)}>{strings.trainerLogOutOthers}</button>
      )}
    </section>
  )
}