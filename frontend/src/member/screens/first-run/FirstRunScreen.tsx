import { useState } from 'react'
import type { MemberIdentity } from '../../../api/generated'
import { ChooseStep } from './ChooseStep'
import { NicknameStep } from './NicknameStep'

type Props = {
  onCreated: (profile: MemberIdentity) => void
}

export const FirstRunScreen = ({ onCreated }: Props) => {
  const [step, setStep] = useState<'choose' | 'nickname'>('choose')

  if (step === 'choose') {
    return <ChooseStep onStart={() => setStep('nickname')} />
  }
  return <NicknameStep onBack={() => setStep('choose')} onCreated={onCreated} />
}