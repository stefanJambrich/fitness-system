export type Bytes = Uint8Array<ArrayBuffer>

export const base64url = (bytes: Uint8Array): string => {
  let binary = ''
  for (const byte of bytes) {
    binary += String.fromCharCode(byte)
  }
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

export const sha256 = async (data: Bytes): Promise<Bytes> =>
  new Uint8Array(await crypto.subtle.digest('SHA-256', data))

export const utf8 = (text: string): Bytes => new TextEncoder().encode(text)