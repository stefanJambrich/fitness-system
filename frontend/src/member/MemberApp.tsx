import { useEffect, useState } from 'react'
import type { MemberIdentity } from '../api/generated'
import { loadProfile } from '../crypto/keyStore'
import { strings } from '../shared/strings'
import { FirstRunScreen } from './FirstRunScreen'
import { HomeScreen } from './HomeScreen'

export function MemberApp() {
  const [profile, setProfile] = useState<MemberIdentity | null>()

  useEffect(() => {
    loadProfile().then((stored) => setProfile(stored ?? null))
  }, [])

  if (profile === undefined) {
    return <p>{strings.loading}</p>
  }
  if (profile === null) {
    return <FirstRunScreen onCreated={setProfile} />
  }
  return <HomeScreen profile={profile} />
}