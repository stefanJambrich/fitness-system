import * as React from "react";
import { Button } from "../../../commons/components/ui/Button.tsx";
import { Input } from "../../../commons/components/ui/Input.tsx";

interface NicknameStepProps {
    nickname: string;
    onChange: (value: string) => void;
    onSubmit: (e: React.SubmitEvent) => void;
}

export const NicknameStep = ({ nickname, onChange, onSubmit }: NicknameStepProps) => {
    return (
        <form onSubmit={onSubmit} className="space-y-6 animate-fade-in">
            <div className="space-y-2">
                <label 
                    htmlFor="nickname" 
                    className="block text-sm font-medium text-foreground"
                >
                    Zvol si přezdívku
                </label>
                <Input
                    id="nickname"
                    type="text"
                    value={nickname}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder="Karel"
                    required
                    className="w-full"
                    autoFocus
                />
                <p className="text-xs text-muted-foreground mt-1">
                    Tato přezdívka bude viditelná ostatním členům
                </p>
            </div>
            <Button type="submit" className="w-full transition-all duration-300 font-semibold shadow-md hover:shadow-lg active:scale-[0.98]">
                Vytvořit profil
            </Button>
        </form>
    );
};
