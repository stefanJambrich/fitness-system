import { useState } from "react";
import {type OtpVerificationResponseDto, useVerifyOtpApi} from "../api/useVerifyOtpApi.ts";

interface UseOtpStepProps {
    email: string;
    onSuccess: (data: OtpVerificationResponseDto, status: number) => void;
    onBack: () => void;
    clearErrors: () => void;
}

export function useOtpStep({ email, onSuccess, onBack, clearErrors }: UseOtpStepProps) {
    const [otp, setOtp] = useState("");
    const { verifyOtp, isLoading, error, setError } = useVerifyOtpApi();

    const handleOtpChange = async (value: string) => {
        setOtp(value);
        if (value.length === 6) {
            clearErrors();
            try {
                const { data, status } = await verifyOtp(email, value);
                onSuccess(data, status);
            } catch {
                setOtp("");
            }
        }
    };

    return {
        isLoading,
        error,
        setError,
        stepProps: {
            otp,
            onChange: handleOtpChange,
            onBack,
            isLoading,
        },
    };
}
