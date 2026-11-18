import { apiErrorCode } from '../api/errors'

const SESSION_ERRORS = ['AUTH_INVALID', 'SESSION_EXPIRED']

/** True when the request failed because the trainer is not (or no longer) logged in. */
export const isSessionError = async (error: unknown): Promise<boolean> =>
  SESSION_ERRORS.includes((await apiErrorCode(error)) ?? '')