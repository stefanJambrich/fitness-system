import { Configuration, GroupsApi, HealthApi, MembersApi, TrainerAuthApi } from './generated'
import { signingMiddleware } from './signingMiddleware'

const config = new Configuration({ basePath: '/api' })
const memberConfig = new Configuration({ basePath: '/api', middleware: [signingMiddleware] })

export const healthApi = new HealthApi(config)
export const membersApi = new MembersApi(memberConfig)
export const trainerAuthApi = new TrainerAuthApi(config)
export const groupsApi = new GroupsApi(config)