import { useState } from "react";

interface UseNicknameStepProps {
    email: string;
    onSuccess: () => void;
}

export function useNicknameStep({ email, onSuccess }: UseNicknameStepProps) {
    const [nickname, setNickname] = useState("");

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (nickname.trim()) {
            const storedUser = localStorage.getItem("fitreserve_user");
            const userObj = storedUser ? JSON.parse(storedUser) : {};
            userObj.fullname = nickname.trim();
            userObj.email = email;
            localStorage.setItem("fitreserve_user", JSON.stringify(userObj));
            onSuccess();
        }
    };

    return {
        stepProps: {
            nickname,
            onChange: setNickname,
            onSubmit: handleSubmit,
        },
    };
}
