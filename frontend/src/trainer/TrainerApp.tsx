import { useEffect, useState } from 'react'
import { trainerAuthApi } from '../api/client'
import type { TrainerProfile } from '../api/generated'
import { strings } from '../shared/strings'
import { isSessionError } from './sessionErrors'
import { TrainerHomeScreen } from './TrainerHomeScreen'
import { TrainerLoginScreen } from './TrainerLoginScreen'

export function TrainerApp() {
  const [trainer, setTrainer] = useState<TrainerProfile | null>()
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

  if (failed) {
    return <p role="alert">{strings.errorGeneric}</p>
  }
  if (trainer === undefined) {
    return <p>{strings.loading}</p>
  }
  if (trainer === null) {
    return <TrainerLoginScreen onLoggedIn={setTrainer} />
  }
  return <TrainerHomeScreen trainer={trainer} onLoggedOut={() => setTrainer(null)} />
}