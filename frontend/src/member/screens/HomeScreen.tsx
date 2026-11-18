import { Paper, Stack, Text } from '@mantine/core'
import type { MemberIdentity } from '../../api/generated'
import { strings } from '../../common/strings'

type Props = {
  profile: MemberIdentity
}

export const HomeScreen = ({ profile }: Props) => (
  <Paper withBorder p="lg">
    <Stack gap="xs">
      <Text size="sm" c="dimmed">{strings.homeIdentifierLabel}</Text>
      <Text fz={32} fw={700} lh={1.2} style={{ overflowWrap: 'anywhere' }}>
        {profile.nickname}
        <Text span inherit c="dimmed">#{profile.code}</Text>
      </Text>
      <Text size="sm" c="dimmed">{strings.homeIdentifierHint}</Text>
    </Stack>
  </Paper>
)