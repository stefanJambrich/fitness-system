import { apiErrorCode } from '../api/errors'

const SESSION_ERRORS = ['AUTH_INVALID', 'SESSION_EXPIRED']

/** True when the request failed because the trainer is not (or no longer) logged in. */
export async function isSessionError(error: unknown): Promise<boolean> {
  return SESSION_ERRORS.includes((await apiErrorCode(error)) ?? '')
}