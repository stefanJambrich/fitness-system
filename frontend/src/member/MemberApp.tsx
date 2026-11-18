import { useEffect, useState } from 'react'
import type { MemberIdentity } from '../api/generated'
import { loadProfile } from '../crypto/keyStore'
import { PageLoader } from '../common/components/PageLoader'
import { FirstRunScreen } from './screens/first-run/FirstRunScreen'
import { HomeScreen } from './screens/HomeScreen'

export const MemberApp = () => {
  const [profile, setProfile] = useState<MemberIdentity | null>()

  useEffect(() => {
    loadProfile().then((stored) => setProfile(stored ?? null))
  }, [])

  if (profile === undefined) {
    return <PageLoader />
  }
  if (profile === null) {
    return <FirstRunScreen onCreated={setProfile} />
  }
  return <HomeScreen profile={profile} />
}