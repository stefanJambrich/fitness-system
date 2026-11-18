import { Center, Loader, VisuallyHidden } from '@mantine/core'
import { strings } from '../strings'

export const PageLoader = () => (
  <Center py="xl" role="status">
    <Loader />
    <VisuallyHidden>{strings.loading}</VisuallyHidden>
  </Center>
)