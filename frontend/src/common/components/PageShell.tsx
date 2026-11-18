import { Container, Group, Stack, Title } from '@mantine/core'
import type { ReactNode } from 'react'
import { strings } from '../strings'

const PAGE_WIDTH = 480
const LOGO_SIZE = 32

export const PageShell = ({ children }: { children: ReactNode }) => (
  <Container size={PAGE_WIDTH} px="md" py="lg">
    <Stack gap="xl">
      <Group component="header" gap="sm" wrap="nowrap">
        <img src="/icon.svg" width={LOGO_SIZE} height={LOGO_SIZE} alt="" />
        <Title order={1} size="h3">{strings.appName}</Title>
      </Group>
      <main>{children}</main>
    </Stack>
  </Container>
)