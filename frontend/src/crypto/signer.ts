import { base64url, sha256, utf8 } from './base64url'
import { keyId } from './identityKey'

/** BR-01 canonical string; must stay byte-for-byte identical to the server's `CanonicalRequest`. */
export function canonicalString(
  method: string,
  pathAndQuery: string,
  timestamp: string,
  nonce: string,
  bodyHash: string,
): string {
  return [method, pathAndQuery, timestamp, nonce, bodyHash].join('\n')
}

/** Signs a request and returns the X-Key-Id, X-Timestamp, X-Nonce and X-Signature headers. */
export async function signatureHeaders(
  keyPair: CryptoKeyPair,
  method: string,
  url: string,
  body: string | undefined,
): Promise<Record<string, string>> {
  const target = new URL(url, window.location.origin)
  const timestamp = new Date().toISOString()
  const nonce = base64url(crypto.getRandomValues(new Uint8Array(16)))
  const bodyHash = base64url(await sha256(utf8(body ?? '')))
  const canonical = canonicalString(method.toUpperCase(), target.pathname + target.search, timestamp, nonce, bodyHash)

  const signature = await crypto.subtle.sign(
    { name: 'ECDSA', hash: 'SHA-256' },
    keyPair.privateKey,
    utf8(canonical),
  )

  return {
    'X-Key-Id': await keyId(keyPair),
    'X-Timestamp': timestamp,
    'X-Nonce': nonce,
    'X-Signature': base64url(new Uint8Array(signature)),
  }
}