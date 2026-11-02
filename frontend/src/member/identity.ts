import { membersApi } from '../api/client'
import { apiErrorCode } from '../api/errors'
import { KeyAlgorithm } from '../api/generated'
import type { MemberIdentity } from '../api/generated'
import { base64url } from '../crypto/base64url'
import { exportPublicKey, generateIdentityKey } from '../crypto/identityKey'
import { deleteKeyPair, loadKeyPair, saveKeyPair, saveProfile } from '../crypto/keyStore'

export function formatIdentifier(member: Pick<MemberIdentity, 'nickname' | 'code'>): string {
  return `${member.nickname}#${member.code}`
}

/**
 * UC-01: registers this device as a new member. A key left over from a failed attempt is reused;
 * if the server already knows it, a fresh key is generated once.
 */
export async function createIdentity(nickname: string): Promise<MemberIdentity> {
  try {
    return await register(nickname)
  } catch (error) {
    if ((await apiErrorCode(error)) !== 'KEY_ALREADY_REGISTERED') {
      throw error
    }
    await deleteKeyPair()
    return register(nickname)
  }
}

async function register(nickname: string): Promise<MemberIdentity> {
  let keyPair = await loadKeyPair()
  if (!keyPair) {
    keyPair = await generateIdentityKey()
    await saveKeyPair(keyPair)
  }

  const profile = await membersApi.createMember({
    createMemberRequest: {
      nickname,
      publicKey: base64url(await exportPublicKey(keyPair)),
      algorithm: KeyAlgorithm.EcdsaP256,
      deviceLabel: deviceLabel(),
    },
  })
  await saveProfile(profile)
  return profile
}

function deviceLabel(): string {
  const ua = navigator.userAgent
  const browser = /Edg\//.test(ua) ? 'Edge'
    : /Firefox\//.test(ua) ? 'Firefox'
      : /Chrome\//.test(ua) ? 'Chrome'
        : /Safari\//.test(ua) ? 'Safari'
          : 'Browser'
  const os = /Android/.test(ua) ? 'Android'
    : /iPhone|iPad/.test(ua) ? 'iOS'
      : /Windows/.test(ua) ? 'Windows'
        : /Mac OS X/.test(ua) ? 'macOS'
          : /Linux/.test(ua) ? 'Linux'
            : undefined
  return os ? `${browser} on ${os}` : browser
}