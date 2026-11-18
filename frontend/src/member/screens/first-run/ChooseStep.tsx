import { Anchor, Badge, Button, Divider, Paper, Stack, Text, Title } from '@mantine/core'
import { Link } from 'react-router'
import { strings } from '../../../common/strings'
import { TRAINER_PATHS } from '../../../common/paths'

type Props = {
  onStart: () => void
}

export const ChooseStep = ({ onStart }: Props) => (
  <Stack>
    <Paper withBorder p="lg">
      <Stack>
        <Title order={2} size="h3">{strings.firstRunTitle}</Title>
        <Text c="dimmed">{strings.firstRunIntro}</Text>
        <Button size="md" fullWidth onClick={onStart}>{strings.firstRunStart}</Button>
        <Divider label={strings.firstRunReturning} labelPosition="center" />
        <ComingSoonButton label={strings.firstRunOtherDevice} />
        <ComingSoonButton label={strings.firstRunRestoreEmail} />
      </Stack>
    </Paper>
    <Text size="sm" c="dimmed" ta="center">
      {strings.firstRunTrainerQuestion}{' '}
      <Anchor component={Link} to={TRAINER_PATHS.home} inherit>{strings.firstRunTrainerLogin}</Anchor>
    </Text>
  </Stack>
)

const ComingSoonButton = ({ label }: { label: string }) => (
  <Button
    variant="default"
    fullWidth
    disabled
    justify="space-between"
    h="auto"
    py="xs"
    styles={{ label: { whiteSpace: 'normal', textAlign: 'left' } }}
    rightSection={<Badge size="xs" variant="light" color="gray">{strings.comingSoon}</Badge>}
  >
    {label}
  </Button>
)