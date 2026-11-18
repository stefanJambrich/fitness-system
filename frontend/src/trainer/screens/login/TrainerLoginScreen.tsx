import { useState } from 'react'
import type { TrainerProfile } from '../../../api/generated'
import { CodeStep } from './CodeStep'
import { EmailStep } from './EmailStep'

type Props = {
  onLoggedIn: (trainer: TrainerProfile) => void
  sessionExpired?: boolean
}

export const TrainerLoginScreen = ({ onLoggedIn, sessionExpired = false }: Props) => {
  const [email, setEmail] = useState('')
  const [codeSent, setCodeSent] = useState(false)

  const showCodeStep = (sentTo: string) => {
    setEmail(sentTo)
    setCodeSent(true)
  }

  if (!codeSent) {
    return <EmailStep initialEmail={email} sessionExpired={sessionExpired} onCodeSent={showCodeStep} />
  }
  return <CodeStep email={email} onBack={() => setCodeSent(false)} onLoggedIn={onLoggedIn} />
}