import { useState } from "react";
import { useNavigate } from "react-router";
import { useEmailStep } from "./useEmailStep.ts";
import { useOtpStep } from "./useOtpStep.ts";
import { useNicknameStep } from "./useNicknameStep.ts";
import type { VerifyResponse } from "../api/useVerifyOtpApi.ts";

export type LoginStep = "email" | "otp" | "nickname";

export function useLoginFlow() {
    const navigate = useNavigate();
    const [step, setStep] = useState<LoginStep>("email");
    const [registerToken, setRegisterToken] = useState<string>("");

    const clearAllErrors = () => {
        emailStep.setError(null);
        otpStep.setError(null);
        nicknameStep.setError(null);
    };

    const handleBackToEmail = () => {
        setStep("email");
        clearAllErrors();
    };

    const emailStep = useEmailStep({
        onSuccess: () => setStep("otp"),
        clearErrors: clearAllErrors,
    });

    const otpStep = useOtpStep({
        email: emailStep.stepProps.email,
        onBack: handleBackToEmail,
        clearErrors: clearAllErrors,
        onSuccess: (data: VerifyResponse, status: number) => {
            if (status === 202) {
                const newData = data as any;
                setRegisterToken(newData.register_token || newData.registerToken || "");
                setStep("nickname");
            } else {
                const successData = data as any;
                localStorage.setItem("fitreserve_user", JSON.stringify(successData.user));
                localStorage.setItem("fitreserve_auth_method", successData.auth_method);
                navigate("/");
            }
        },
    });

    const nicknameStep = useNicknameStep({
        registerToken,
        onSuccess: () => navigate("/"),
    });

    const isLoading = emailStep.isLoading || otpStep.isLoading || nicknameStep.isLoading;
    const error = emailStep.error || otpStep.error || nicknameStep.error;
    const progressPercent = step === "email" ? 33 : step === "otp" ? 66 : 100;

    return {
        step,
        isLoading,
        error,
        progressPercent,
        emailStepProps: emailStep.stepProps,
        otpStepProps: otpStep.stepProps,
        nicknameStepProps: nicknameStep.stepProps,
    };
}
