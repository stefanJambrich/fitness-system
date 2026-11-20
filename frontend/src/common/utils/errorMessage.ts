import { apiErrorCode } from '../../api/errors'
import { strings } from '../strings'

export const errorMessage = async (error: unknown, messages: Record<string, string>): Promise<string> =>
  messages[(await apiErrorCode(error)) ?? ''] ?? strings.errorGeneric
