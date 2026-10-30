import { Configuration, HealthApi } from './generated'

const config = new Configuration({ basePath: '/api' })

export const healthApi = new HealthApi(config)