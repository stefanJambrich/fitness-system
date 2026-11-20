import { Anchor, Stack } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { Link } from 'react-router'
import type { TrainerProfile } from '../../../api/generated'
import { TRAINER_PATHS } from '../../../common/paths'
import { strings } from '../../../common/strings'
import { useTrainerSessions } from '../../hooks/useTrainerSessions'
import { AccountCard } from './AccountCard'
import { DevicesCard } from './DevicesCard'
import { LogOutOthersModal } from './LogOutOthersModal'

type Props = {
  trainer: TrainerProfile
  onLoggedOut: () => void
  onSessionExpired: () => void
}

export const AccountScreen = ({ trainer, onLoggedOut, onSessionExpired }: Props) => {
  const devices = useTrainerSessions(onLoggedOut, onSessionExpired)
  const [confirmOpen, confirm] = useDisclosure(false)

  const confirmRevokeOthers = async () => {
    await devices.revokeOthers()
    confirm.close()
  }

  return (
    <Stack>
      <Anchor component={Link} to={TRAINER_PATHS.home}>{strings.trainerBackToGroups}</Anchor>
      <AccountCard
        trainer={trainer}
        loggingOut={devices.loggingOut}
        disabled={devices.busy !== undefined}
        onLogOut={devices.logOut}
      />
      <DevicesCard
        sessions={devices.sessions}
        error={devices.error}
        busy={devices.busy}
        onRevoke={devices.revokeSession}
        onRevokeOthers={confirm.open}
      />
      <LogOutOthersModal
        opened={confirmOpen}
        count={devices.others.length}
        busy={devices.revokingOthers}
        onClose={confirm.close}
        onConfirm={confirmRevokeOthers}
      />
    </Stack>
  )
}