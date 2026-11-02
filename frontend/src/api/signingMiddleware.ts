import { loadKeyPair } from '../crypto/keyStore'
import { signatureHeaders } from '../crypto/signer'
import type { Middleware } from './generated'

/** Signs every request with the device key (BR-01); requests go out unsigned while there is no key yet. */
export const signingMiddleware: Middleware = {
  async pre({ url, init }) {
    const keyPair = await loadKeyPair()
    if (!keyPair) {
      return
    }
    if (init.body !== undefined && typeof init.body !== 'string') {
      throw new Error('Only string request bodies can be signed')
    }

    const headers = new Headers(init.headers)
    const signed = await signatureHeaders(keyPair, init.method ?? 'GET', url, init.body)
    for (const [name, value] of Object.entries(signed)) {
      headers.set(name, value)
    }
    return { url, init: { ...init, headers } }
  },
}