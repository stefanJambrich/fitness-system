import { base64url, sha256 } from './base64url'
import type { Bytes } from './base64url'

/** Generates the device key pair. The private key is non-extractable: it can sign but never be read or exported. */
export const generateIdentityKey = (): Promise<CryptoKeyPair> =>
  crypto.subtle.generateKey({ name: 'ECDSA', namedCurve: 'P-256' }, false, ['sign', 'verify'])

export const exportPublicKey = async (keyPair: CryptoKeyPair): Promise<Bytes> =>
  new Uint8Array(await crypto.subtle.exportKey('spki', keyPair.publicKey))

export const keyId = async (keyPair: CryptoKeyPair): Promise<string> =>
  base64url(await sha256(await exportPublicKey(keyPair)))