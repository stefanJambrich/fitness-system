import { Configuration, HealthApi, MembersApi } from './generated'
import { signingMiddleware } from './signingMiddleware'

const config = new Configuration({ basePath: '/api' })
const memberConfig = new Configuration({ basePath: '/api', middleware: [signingMiddleware] })

export const healthApi = new HealthApi(config)
export const membersApi = new MembersApi(memberConfig)