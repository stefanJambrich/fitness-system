import { Button, Group, Modal, Stack, Text } from '@mantine/core'
import { strings } from '../../../common/strings'

type Props = {
  opened: boolean
  count: number
  busy: boolean
  onClose: () => void
  onConfirm: () => void
}

export const LogOutOthersModal = ({ opened, count, busy, onClose, onConfirm }: Props) => (
  <Modal opened={opened} onClose={onClose} title={strings.trainerLogOutOthersTitle} centered>
    <Stack>
      <Text>{strings.trainerLogOutOthersConfirm(count)}</Text>
      <Group justify="flex-end">
        <Button variant="default" onClick={onClose} disabled={busy}>{strings.cancel}</Button>
        <Button color="red" onClick={onConfirm} loading={busy}>{strings.trainerLogOutOthers}</Button>
      </Group>
    </Stack>
  </Modal>
)