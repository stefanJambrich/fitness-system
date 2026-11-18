import { BrowserRouter, Route, Routes } from 'react-router'
import { MemberApp } from './member/MemberApp'
import { PageShell } from './common/components/PageShell'
import { TrainerApp } from './trainer/TrainerApp'

const App = () => (
  <BrowserRouter>
    <PageShell>
      <Routes>
        <Route path="/trainer/*" element={<TrainerApp />} />
        <Route path="*" element={<MemberApp />} />
      </Routes>
    </PageShell>
  </BrowserRouter>
)

export default App