import { ResponseError } from './generated'
import type { ErrorResponse } from './generated'

/** Error code from an API error response (`VALIDATION`, `RATE_LIMITED`, …), or undefined for network errors. */
export async function apiErrorCode(error: unknown): Promise<string | undefined> {
  if (!(error instanceof ResponseError)) {
    return undefined
  }
  try {
    const body = (await error.response.json()) as ErrorResponse
    return body.error
  } catch {
    return undefined
  }
}