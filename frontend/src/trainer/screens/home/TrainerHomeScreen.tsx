import { Stack } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import type { TrainerProfile } from '../../../api/generated'
import { useTrainerSessions } from '../../hooks/useTrainerSessions'
import { AccountCard } from './AccountCard'
import { DevicesCard } from './DevicesCard'
import { LogOutOthersModal } from './LogOutOthersModal'

type Props = {
  trainer: TrainerProfile
  onLoggedOut: () => void
  onSessionExpired: () => void
}

export const TrainerHomeScreen = ({ trainer, onLoggedOut, onSessionExpired }: Props) => {
  const devices = useTrainerSessions(onLoggedOut, onSessionExpired)
  const [confirmOpen, confirm] = useDisclosure(false)

  const confirmRevokeOthers = async () => {
    await devices.revokeOthers()
    confirm.close()
  }

  return (
    <Stack>
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