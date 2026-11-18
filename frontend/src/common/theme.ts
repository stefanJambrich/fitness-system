import { createTheme } from '@mantine/core'

// When changing the colour, also update theme-color in index.html, manifest.webmanifest and icon.svg.
export const theme = createTheme({
  primaryColor: 'teal',
  primaryShade: 9,
  defaultRadius: 'md',
  cursorType: 'pointer',
})