import { Alert, Button, Center, Divider, Loader, Paper, Stack, Text, Title } from '@mantine/core'
import { Fragment } from 'react'
import type { TrainerSessionInfo } from '../../../api/generated'
import { strings } from '../../../common/strings'
import { SessionRow } from './SessionRow'

type Props = {
  sessions: TrainerSessionInfo[] | undefined
  error: string | undefined
  busy: string | undefined
  onRevoke: (sessionId: string) => void
  onRevokeOthers: () => void
}

export const DevicesCard = ({ sessions, error, busy, onRevoke, onRevokeOthers }: Props) => {
  const hasOthers = sessions?.some((session) => !session.current) ?? false

  return (
    <Paper withBorder p="lg">
      <Stack>
        <Title order={2} size="h4">{strings.trainerDevicesTitle}</Title>
        {error && <Alert color="red">{error}</Alert>}
        {sessions === undefined
          ? <Center><Loader size="sm" /></Center>
          : (
            <Stack gap="sm">
              {sessions.map((session, index) => (
                <Fragment key={session.id}>
                  {index > 0 && <Divider />}
                  <SessionRow
                    session={session}
                    busy={busy === session.id}
                    disabled={busy !== undefined}
                    onRevoke={() => onRevoke(session.id)}
                  />
                </Fragment>
              ))}
            </Stack>
          )}
        {sessions !== undefined && (hasOthers
          ? (
            <Button variant="light" color="red" fullWidth onClick={onRevokeOthers} disabled={busy !== undefined}>
              {strings.trainerLogOutOthers}
            </Button>
          )
          : <Text size="sm" c="dimmed">{strings.trainerOnlyDevice}</Text>)}
      </Stack>
    </Paper>
  )
}