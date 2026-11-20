import { Badge, Group, Stack, Text } from '@mantine/core'
import { GroupRole } from '../../../api/generated'
import type { TrainerGroup } from '../../../api/generated'
import { strings } from '../../../common/strings'

type Props = {
  group: TrainerGroup
}

export const GroupRow = ({ group }: Props) => (
  <Stack gap={2} miw={0}>
    <Group gap="xs" wrap="nowrap">
      <Text fw={500} truncate>{group.name}</Text>
      {group.role === GroupRole.CoTrainer && (
        <Badge size="sm" variant="light" color="gray">{strings.groupCoTrainer}</Badge>
      )}
    </Group>
    {group.description && <Text size="sm" c="dimmed" lineClamp={1}>{group.description}</Text>}
  </Stack>
)