import { Alert, Button, Center, Divider, Group, Loader, Paper, Stack, Text, Title } from '@mantine/core'
import { Fragment } from 'react'
import type { TrainerGroup } from '../../../api/generated'
import { strings } from '../../../common/strings'
import { GroupRow } from './GroupRow'

type Props = {
  groups: TrainerGroup[] | undefined
  error: string | undefined
  onNewGroup: () => void
}

export const GroupsCard = ({ groups, error, onNewGroup }: Props) => (
  <Paper withBorder p="lg">
    <Stack>
      <Group justify="space-between" wrap="nowrap">
        <Title order={2} size="h4">{strings.groupsTitle}</Title>
        <Button size="xs" onClick={onNewGroup} disabled={groups === undefined}>{strings.groupsNew}</Button>
      </Group>
      {error && <Alert color="red">{error}</Alert>}
      {groups === undefined && !error && <Center><Loader size="sm" /></Center>}
      {groups?.length === 0 && <Text size="sm" c="dimmed">{strings.groupsEmpty}</Text>}
      {groups !== undefined && groups.length > 0 && (
        <Stack gap="sm">
          {groups.map((group, index) => (
            <Fragment key={group.id}>
              {index > 0 && <Divider />}
              <GroupRow group={group} />
            </Fragment>
          ))}
        </Stack>
      )}
    </Stack>
  </Paper>
)