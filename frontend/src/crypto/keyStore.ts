import type { MemberIdentity } from '../api/generated'

const DB_NAME = 'fitness-system'
const STORE = 'identity'
const KEY_PAIR = 'keyPair'
const PROFILE = 'profile'

let dbPromise: Promise<IDBDatabase | null> | undefined
const memoryFallback = new Map<string, unknown>()

function openDb(): Promise<IDBDatabase | null> {
  dbPromise ??= new Promise((resolve) => {
    try {
      const request = indexedDB.open(DB_NAME, 1)
      request.onupgradeneeded = () => request.result.createObjectStore(STORE)
      request.onsuccess = () => resolve(request.result)
      request.onerror = () => resolve(null)
    } catch {
      resolve(null)
    }
  })
  return dbPromise
}

/** False when IndexedDB is unavailable and the identity lives only in memory until the tab closes. */
export async function isPersistent(): Promise<boolean> {
  return (await openDb()) !== null
}

async function read<T>(key: string): Promise<T | undefined> {
  const db = await openDb()
  if (!db) {
    return memoryFallback.get(key) as T | undefined
  }
  return new Promise((resolve, reject) => {
    const request = db.transaction(STORE).objectStore(STORE).get(key)
    request.onsuccess = () => resolve(request.result as T | undefined)
    request.onerror = () => reject(request.error)
  })
}

async function write(key: string, value: unknown): Promise<void> {
  const db = await openDb()
  if (!db) {
    if (value === undefined) {
      memoryFallback.delete(key)
    } else {
      memoryFallback.set(key, value)
    }
    return
  }
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE, 'readwrite')
    const store = tx.objectStore(STORE)
    if (value === undefined) {
      store.delete(key)
    } else {
      store.put(value, key)
    }
    tx.oncomplete = () => resolve()
    tx.onerror = () => reject(tx.error)
  })
}

export const loadKeyPair = () => read<CryptoKeyPair>(KEY_PAIR)
export const saveKeyPair = (keyPair: CryptoKeyPair) => write(KEY_PAIR, keyPair)
export const deleteKeyPair = () => write(KEY_PAIR, undefined)

export const loadProfile = () => read<MemberIdentity>(PROFILE)
export const saveProfile = (profile: MemberIdentity) => write(PROFILE, profile)