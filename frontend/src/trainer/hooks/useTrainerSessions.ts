import { useCallback, useEffect, useState } from 'react'
import { trainerAuthApi } from '../../api/client'
import { apiErrorCode } from '../../api/errors'
import type { TrainerSessionInfo } from '../../api/generated'
import { strings } from '../../common/strings'
import { isSessionError } from '../sessionErrors'

const LOG_OUT = 'logout'
const LOG_OUT_OTHERS = 'others'

/**
 * The trainer's logged-in devices with logout and revoke actions. `busy` is the running action:
 * a session id while that device is being logged out. A session that is already gone is not an error.
 */
export const useTrainerSessions = (onLoggedOut: () => void, onSessionExpired: () => void) => {
  const [sessions, setSessions] = useState<TrainerSessionInfo[]>()
  const [error, setError] = useState<string>()
  const [busy, setBusy] = useState<string>()

  const others = sessions?.filter((session) => !session.current) ?? []

  const fail = useCallback(async (e: unknown) => {
    if (await isSessionError(e)) {
      onSessionExpired()
    } else if ((await apiErrorCode(e)) !== 'NOT_FOUND') {
      setError(strings.errorGeneric)
    }
  }, [onSessionExpired])

  const loadSessions = useCallback(
    () => trainerAuthApi.listSessions().then(setSessions).catch(fail),
    [fail],
  )

  useEffect(() => {
    loadSessions()
  }, [loadSessions])

  const logOut = async () => {
    setBusy(LOG_OUT)
    setError(undefined)
    try {
      await trainerAuthApi.logout()
      onLoggedOut()
    } catch (e) {
      await fail(e)
      setBusy(undefined)
    }
  }

  const revoke = async (ids: string[], action: string) => {
    setBusy(action)
    setError(undefined)
    try {
      await Promise.all(ids.map((sessionId) => trainerAuthApi.revokeSession({ sessionId })))
      setSessions((current) => current?.filter((session) => !ids.includes(session.id)))
    } catch (e) {
      await fail(e)
      await loadSessions()
    } finally {
      setBusy(undefined)
    }
  }

  return {
    sessions,
    others,
    error,
    busy,
    loggingOut: busy === LOG_OUT,
    revokingOthers: busy === LOG_OUT_OTHERS,
    logOut,
    revokeSession: (sessionId: string) => revoke([sessionId], sessionId),
    revokeOthers: () => revoke(others.map((session) => session.id), LOG_OUT_OTHERS),
  }
}