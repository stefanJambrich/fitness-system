import { base64url, sha256 } from './base64url'
import type { Bytes } from './base64url'

/** Generates the device key pair. The private key is non-extractable: it can sign but never be read or exported. */
export function generateIdentityKey(): Promise<CryptoKeyPair> {
  return crypto.subtle.generateKey({ name: 'ECDSA', namedCurve: 'P-256' }, false, ['sign', 'verify'])
}

export async function exportPublicKey(keyPair: CryptoKeyPair): Promise<Bytes> {
  return new Uint8Array(await crypto.subtle.exportKey('spki', keyPair.publicKey))
}

export async function keyId(keyPair: CryptoKeyPair): Promise<string> {
  return base64url(await sha256(await exportPublicKey(keyPair)))
}