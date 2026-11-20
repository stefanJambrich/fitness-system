import { Button, Group, Paper, Stack, Text } from '@mantine/core'
import type { TrainerProfile } from '../../../api/generated'
import { strings } from '../../../common/strings'

type Props = {
  trainer: TrainerProfile
  loggingOut: boolean
  disabled: boolean
  onLogOut: () => void
}

export const AccountCard = ({ trainer, loggingOut, disabled, onLogOut }: Props) => (
  <Paper withBorder p="lg">
    <Group justify="space-between" wrap="nowrap">
      <Stack gap={2} miw={0}>
        <Text size="sm" c="dimmed">{strings.trainerLoggedInAs}</Text>
        <Text fw={600} truncate>{trainer.displayName}</Text>
        <Text size="sm" c="dimmed" truncate>{trainer.email}</Text>
      </Stack>
      <Button variant="default" onClick={onLogOut} loading={loggingOut} disabled={disabled && !loggingOut}>
        {strings.trainerLogOut}
      </Button>
    </Group>
  </Paper>
)