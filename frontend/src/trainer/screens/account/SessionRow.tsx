import { Badge, Button, Group, Stack, Text } from '@mantine/core'
import type { TrainerSessionInfo } from '../../../api/generated'
import { formatDateTime } from '../../../common/utils/format'
import { strings } from '../../../common/strings'

type Props = {
  session: TrainerSessionInfo
  busy: boolean
  disabled: boolean
  onRevoke: () => void
}

export const SessionRow = ({ session, busy, disabled, onRevoke }: Props) => (
  <Group justify="space-between" wrap="nowrap">
    <Stack gap={4} miw={0}>
      <Group gap="xs">
        <Text fw={500}>{session.deviceLabel ?? strings.trainerUnknownDevice}</Text>
        {session.current && <Badge size="sm" variant="light">{strings.trainerThisDevice}</Badge>}
        {session.remembered && <Badge size="sm" variant="light" color="gray">{strings.trainerRemembered}</Badge>}
      </Group>
      <Text size="sm" c="dimmed">{strings.trainerLastUsed} {formatDateTime(session.lastUsedAt)}</Text>
    </Stack>
    {!session.current && (
      <Button variant="subtle" color="red" size="xs" onClick={onRevoke} loading={busy} disabled={disabled && !busy}>
        {strings.trainerLogOutDevice}
      </Button>
    )}
  </Group>
)