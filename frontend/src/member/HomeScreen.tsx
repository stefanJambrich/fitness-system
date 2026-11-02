import type { MemberIdentity } from '../api/generated'
import { strings } from '../shared/strings'
import { formatIdentifier } from './identity'

type Props = {
  profile: MemberIdentity
}

export function HomeScreen({ profile }: Props) {
  return (
    <section>
      <p>{strings.homeIdentifierLabel}</p>
      <p><strong>{formatIdentifier(profile)}</strong></p>
    </section>
  )
}