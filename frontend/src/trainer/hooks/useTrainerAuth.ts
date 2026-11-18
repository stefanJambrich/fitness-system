import { useCallback, useEffect, useState } from 'react'
import { trainerAuthApi } from '../../api/client'
import type { TrainerProfile } from '../../api/generated'
import { isSessionError } from '../sessionErrors'

/**
 * Logged-in trainer: undefined while the session is being checked, null when logged out.
 * `expire` logs out and remembers why, so the login screen can say the session has expired.
 */
export const useTrainerAuth = () => {
  const [trainer, setTrainer] = useState<TrainerProfile | null>()
  const [sessionExpired, setSessionExpired] = useState(false)
  const [failed, setFailed] = useState(false)

  useEffect(() => {
    trainerAuthApi.getTrainerProfile()
      .then(setTrainer)
      .catch(async (error) => {
        if (await isSessionError(error)) {
          setTrainer(null)
        } else {
          setFailed(true)
        }
      })
  }, [])

  const logIn = useCallback((profile: TrainerProfile) => {
    setSessionExpired(false)
    setTrainer(profile)
  }, [])

  const logOut = useCallback(() => setTrainer(null), [])

  const expire = useCallback(() => {
    setSessionExpired(true)
    setTrainer(null)
  }, [])

  return { trainer, sessionExpired, failed, logIn, logOut, expire }
}