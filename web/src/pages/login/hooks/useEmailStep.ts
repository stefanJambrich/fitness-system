import { useState } from "react";
import { useRequestOtpApi } from "../api/useRequestOtpApi.ts";

interface UseEmailStepProps {
    onSuccess: () => void;
    clearErrors: () => void;
}

export function useEmailStep({ onSuccess, clearErrors }: UseEmailStepProps) {
    const [email, setEmail] = useState("");
    const { requestOtp, isLoading, error, setError } = useRequestOtpApi();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email.trim()) return;

        clearErrors();
        await requestOtp(email);
        onSuccess();
    };

    return {
        isLoading,
        error,
        setError,
        stepProps: {
            email,
            onChange: setEmail,
            onSubmit: handleSubmit,
            isLoading,
        },
    };
}
