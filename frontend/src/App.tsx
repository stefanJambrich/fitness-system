import { useEffect, useState } from 'react'
import type { MemberIdentity } from './api/generated'
import { loadProfile } from './crypto/keyStore'
import { FirstRunScreen } from './member/FirstRunScreen'
import { HomeScreen } from './member/HomeScreen'
import { strings } from './shared/strings'

function App() {
  const [profile, setProfile] = useState<MemberIdentity | null>()

  useEffect(() => {
    loadProfile().then((stored) => setProfile(stored ?? null))
  }, [])

  return (
    <main>
      <h1>{strings.appName}</h1>
      {profile === undefined && <p>{strings.loading}</p>}
      {profile === null && <FirstRunScreen onCreated={setProfile} />}
      {profile && <HomeScreen profile={profile} />}
    </main>
  )
}

export default App