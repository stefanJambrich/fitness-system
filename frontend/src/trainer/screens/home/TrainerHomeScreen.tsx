import { Anchor, Stack } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { Link } from 'react-router'
import { TRAINER_PATHS } from '../../../common/paths'
import { strings } from '../../../common/strings'
import { useTrainerGroups } from '../../hooks/useTrainerGroups'
import { CreateGroupModal } from './CreateGroupModal'
import { GroupsCard } from './GroupsCard'

type Props = {
  onSessionExpired: () => void
}

export const TrainerHomeScreen = ({ onSessionExpired }: Props) => {
  const { groups, error, createGroup } = useTrainerGroups(onSessionExpired)
  const [createOpen, create] = useDisclosure(false)

  return (
    <Stack>
      <GroupsCard groups={groups} error={error} onNewGroup={create.open} />
      <Anchor component={Link} to={TRAINER_PATHS.account} ta="center">{strings.trainerAccountLink}</Anchor>
      <CreateGroupModal opened={createOpen} onClose={create.close} onCreate={createGroup} />
    </Stack>
  )
}