import { useState } from "react";
import { useNavigate } from "react-router";
import { useEmailStep } from "./useEmailStep.ts";
import { useOtpStep } from "./useOtpStep.ts";
import { useNicknameStep } from "./useNicknameStep.ts";
import type { OtpVerificationResponseDto } from "../api/useVerifyOtpApi.ts";
import { useAppDispatch } from "../../../store/hooks.ts";
import { setUser } from "../../../store/userSlice.ts";

export type LoginStep = "email" | "otp" | "nickname";

export function useLoginFlow() {
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
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
        onSuccess: (data: OtpVerificationResponseDto) => {
            if (data.status == "REGISTRATION_REQUIRED") {
                const newData = data as OtpVerificationResponseDto;
                setRegisterToken(newData.registrationToken);
                setStep("nickname");
            } else {
                dispatch(setUser(data.user));
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
