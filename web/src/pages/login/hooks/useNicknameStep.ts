import { useState } from "react";
import { useRegisterApi } from "../api/useRegisterApi.ts";
import { useAppDispatch } from "../../../store/hooks.ts";
import { setUserInfo } from "../../../store/userSlice.ts";

interface UseNicknameStepProps {
    registerToken: string;
    onSuccess: () => void;
}

export function useNicknameStep({ registerToken, onSuccess }: UseNicknameStepProps) {
    const [nickname, setNickname] = useState("");
    const [registerAsTrainer, setRegisterAsTrainer] = useState(false);
    const { registerUser, isLoading, error, setError } = useRegisterApi();
    const dispatch = useAppDispatch();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (nickname.trim()) {
            const res = await registerUser({
                registrationToken: registerToken,
                name: nickname.trim(),
                registerAsTrainer,
            });
            dispatch(setUserInfo({ user: res }));
            onSuccess();
        }
    };

    return {
        isLoading,
        error,
        setError,
        stepProps: {
            nickname,
            onChange: setNickname,
            registerAsTrainer,
            onRegisterAsTrainerChange: setRegisterAsTrainer,
            onSubmit: handleSubmit,
        },
    };
}
