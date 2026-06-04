import { useState } from "react";
import axios from "axios";
import { getOrCreateDeviceKey } from "../utils/deviceKey.ts";
import type { UserBaseDto } from "../../../commons/api/schemas/UserBaseDto.ts";
import {apiDefinitions} from "./apiDefinitions.ts";

export interface OtpVerificationResponseDto {
    user: UserBaseDto;
    status: "REGISTRATION_REQUIRED";
    registrationToken: string;
}

export function useVerifyOtpApi() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const verifyOtp = async (
        email: string,
        code: string
    ): Promise<{ data: OtpVerificationResponseDto; status: number }> => {
        setIsLoading(true);
        setError(null);
        try {
            const publicHashKey = getOrCreateDeviceKey();
            const response = await axios.post<OtpVerificationResponseDto>(
                apiDefinitions.verifyApi,
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
