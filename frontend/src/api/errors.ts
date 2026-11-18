import { ResponseError } from './generated'
import type { ErrorResponse } from './generated'

const codes = new WeakMap<ResponseError, Promise<string | undefined>>()

export const apiErrorCode = (error: unknown): Promise<string | undefined> => {
  if (!(error instanceof ResponseError)) {
    return Promise.resolve(undefined)
  }
  let code = codes.get(error)
  if (!code) {
    code = readCode(error)
    codes.set(error, code)
  }
  return code
}

const readCode = async (error: ResponseError): Promise<string | undefined> => {
  try {
    const body = (await error.response.json()) as ErrorResponse
    return body.error
  } catch {
    return undefined
  }
}