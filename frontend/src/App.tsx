import { useEffect, useState } from 'react'
import { healthApi } from './api/client'
import { strings } from './shared/strings'

type BackendState = 'checking' | 'ok' | 'down'

function App() {
  const [backend, setBackend] = useState<BackendState>('checking')

  useEffect(() => {
    healthApi.ping({})
      .then(() => setBackend('ok'))
      .catch(() => setBackend('down'))
  }, [])

  return (
    <main>
      <h1>{strings.appName}</h1>
      <p>
        {backend === 'checking' && strings.backendChecking}
        {backend === 'ok' && strings.backendOk}
        {backend === 'down' && strings.backendDown}
      </p>
    </main>
  )
}

export default App