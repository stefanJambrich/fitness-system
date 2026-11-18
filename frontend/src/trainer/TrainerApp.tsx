import { Alert } from '@mantine/core'
import { Navigate, Outlet, Route, Routes, useLocation } from 'react-router'
import { PageLoader } from '../common/components/PageLoader'
import { TRAINER_PATHS } from '../common/paths'
import { strings } from '../common/strings'
import { useTrainerAuth } from './hooks/useTrainerAuth'
import { TrainerHomeScreen } from './screens/home/TrainerHomeScreen'
import { TrainerLoginScreen } from './screens/login/TrainerLoginScreen'

type LoginRedirect = { from?: string }

export const TrainerApp = () => {
  const { trainer, sessionExpired, failed, logIn, logOut, expire } = useTrainerAuth()

  if (failed) {
    return <Alert color="red">{strings.errorGeneric}</Alert>
  }
  if (trainer === undefined) {
    return <PageLoader />
  }

  return (
    <Routes>
      <Route
        path="login"
        element={trainer
          ? <ReturnAfterLogin />
          : <TrainerLoginScreen onLoggedIn={logIn} sessionExpired={sessionExpired} />}
      />
      <Route element={<RequireTrainer loggedIn={trainer !== null} />}>
        <Route
          index
          element={trainer && (
            <TrainerHomeScreen trainer={trainer} onLoggedOut={logOut} onSessionExpired={expire} />
          )}
        />
      </Route>
      <Route path="*" element={<Navigate to={TRAINER_PATHS.home} replace />} />
    </Routes>
  )
}

const RequireTrainer = ({ loggedIn }: { loggedIn: boolean }) => {
  const location = useLocation()
  if (!loggedIn) {
    return <Navigate to={TRAINER_PATHS.login} replace state={{ from: location.pathname } satisfies LoginRedirect} />
  }
  return <Outlet />
}

const ReturnAfterLogin = () => {
  const from = (useLocation().state as LoginRedirect | null)?.from
  return <Navigate to={from ?? TRAINER_PATHS.home} replace />
}