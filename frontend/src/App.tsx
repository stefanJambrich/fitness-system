import { BrowserRouter, Route, Routes } from 'react-router'
import { MemberApp } from './member/MemberApp'
import { strings } from './shared/strings'
import { TrainerApp } from './trainer/TrainerApp'

function App() {
  return (
    <BrowserRouter>
      <main>
        <h1>{strings.appName}</h1>
        <Routes>
          <Route path="/trainer/*" element={<TrainerApp />} />
          <Route path="*" element={<MemberApp />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App