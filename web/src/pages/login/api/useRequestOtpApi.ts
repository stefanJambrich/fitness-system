import { useState } from "react";
import axios from "axios";
import type {MessageResponse} from "../../../commons/api/schemas/MessageResponse.ts";

export function useRequestOtpApi() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const requestOtp = async (email: string): Promise<MessageResponse> => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await axios.post<MessageResponse>(
                "/auth/api/v1/otp",
                null,
                {
                    params: {
                        email: email.trim().toLowerCase(),
                    },
                }
            );
            return response.data;
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || "Nepodařilo se odeslat kód.";
            setError(errorMsg);
            throw new Error(errorMsg, { cause: err });
        } finally {
            setIsLoading(false);
        }
    };

    return { requestOtp, isLoading, error, setError };
}
