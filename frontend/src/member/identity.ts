import { membersApi } from '../api/client'
import { apiErrorCode } from '../api/errors'
import { KeyAlgorithm } from '../api/generated'
import type { MemberIdentity } from '../api/generated'
import { base64url } from '../crypto/base64url'
import { exportPublicKey, generateIdentityKey } from '../crypto/identityKey'
import { deleteKeyPair, loadKeyPair, saveKeyPair, saveProfile } from '../crypto/keyStore'

const UNUSABLE_KEY_ERRORS = ['DEVICE_UNKNOWN', 'DEVICE_REVOKED', 'MEMBER_DELETED']

/**
 * Registers this device as a new member. A key left over from a failed attempt is reused; if the server
 * already registered it (the response got lost), that identity is recovered instead of creating a second one.
 */
export const createIdentity = async (nickname: string): Promise<MemberIdentity> => {
  try {
    return await register(nickname)
  } catch (error) {
    if ((await apiErrorCode(error)) !== 'KEY_ALREADY_REGISTERED') {
      throw error
    }
  }
  const recovered = await recoverProfile()
  if (recovered) {
    return recovered
  }
  await deleteKeyPair()
  return register(nickname)
}

/** Profile of the already registered key, or undefined when the server no longer accepts that key. */
const recoverProfile = async (): Promise<MemberIdentity | undefined> => {
  try {
    const { memberId, nickname, code } = await membersApi.getMyProfile()
    const profile = { memberId, nickname, code }
    await saveProfile(profile)
    return profile
  } catch (error) {
    if (UNUSABLE_KEY_ERRORS.includes((await apiErrorCode(error)) ?? '')) {
      return undefined
    }
    throw error
  }
}

const register = async (nickname: string): Promise<MemberIdentity> => {
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

const deviceLabel = (): string => {
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