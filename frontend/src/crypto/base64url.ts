export type Bytes = Uint8Array<ArrayBuffer>

export function base64url(bytes: Uint8Array): string {
  let binary = ''
  for (const byte of bytes) {
    binary += String.fromCharCode(byte)
  }
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

export async function sha256(data: Bytes): Promise<Bytes> {
  return new Uint8Array(await crypto.subtle.digest('SHA-256', data))
}

export function utf8(text: string): Bytes {
  return new TextEncoder().encode(text)
}