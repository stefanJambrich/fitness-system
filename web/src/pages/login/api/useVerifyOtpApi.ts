import { useState } from "react";
import axios from "axios";
import { getOrCreateDeviceKey } from "../utils/deviceKey.ts";
import type { UserBaseDto } from "../../../commons/api/schemas/UserBaseDto.ts";

export interface OtpVerificationUserResponseDto {
    user: UserBaseDto;
    authMethod: "DEVICE_KEY" | "SESSION_COOKIE";
}

export interface OtpVerificationNewUserResponseDto {
    status: "REGISTRATION_REQUIRED";
    registerToken: string;
}

export type VerifyResponse = OtpVerificationUserResponseDto | OtpVerificationNewUserResponseDto;

export function useVerifyOtpApi() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const verifyOtp = async (
        email: string,
        code: string
    ): Promise<{ data: VerifyResponse; status: number }> => {
        setIsLoading(true);
        setError(null);
        try {
            const publicHashKey = getOrCreateDeviceKey();
            const response = await axios.post<VerifyResponse>(
                "/auth/api/v1/otp/verify",
                {
                    email: email.trim().toLowerCase(),
                    code: code.trim(),
                    publicHashKey,
                }
            );
            return { data: response.data, status: response.status };
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || "Kód se nepodařilo ověřit.";
            setError(errorMsg);
            throw new Error(errorMsg, { cause: err });
        } finally {
            setIsLoading(false);
        }
    };

    return { verifyOtp, isLoading, error, setError };
}
