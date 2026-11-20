import { strings } from '../../../common/strings'

export const CODE_ERRORS: Record<string, string> = {
  OTP_INVALID: strings.trainerCodeInvalid,
  OTP_EXPIRED: strings.trainerCodeExpired,
  OTP_LOCKED: strings.trainerCodeLocked,
  RATE_LIMITED: strings.trainerRateLimited,
}

export const REQUEST_ERRORS: Record<string, string> = {
  RATE_LIMITED: strings.trainerRateLimited,
  VALIDATION: strings.trainerFormInvalid,
}
