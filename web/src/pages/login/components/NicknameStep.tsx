import * as React from "react";
import { Button } from "../../../commons/components/ui/Button.tsx";
import { Input } from "../../../commons/components/ui/Input.tsx";

interface NicknameStepProps {
    nickname: string;
    onChange: (value: string) => void;
    onSubmit: (e: React.FormEvent) => void;
    registerAsTrainer: boolean;
    onRegisterAsTrainerChange: (value: boolean) => void;
}

export const NicknameStep = ({ 
    nickname, 
    onChange, 
    onSubmit,
    registerAsTrainer,
    onRegisterAsTrainerChange 
}: NicknameStepProps) => {
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

            <div className="flex items-center space-x-3 p-3 rounded-xl border border-border/40 bg-muted/30 hover:bg-muted/50 transition-all duration-300">
                <input
                    id="registerAsTrainer"
                    type="checkbox"
                    checked={registerAsTrainer}
                    onChange={(e) => onRegisterAsTrainerChange(e.target.checked)}
                    className="w-4 h-4 rounded border-input text-primary focus:ring-primary accent-primary cursor-pointer"
                />
                <label 
                    htmlFor="registerAsTrainer" 
                    className="text-sm font-medium text-foreground cursor-pointer select-none flex-1"
                >
                    Chci se registrovat jako trenér
                    <span className="block text-xs text-muted-foreground font-normal mt-0.5">
                        Získáte možnost spravovat tréninky a rezervace
                    </span>
                </label>
            </div>

            <Button type="submit" className="w-full transition-all duration-300 font-semibold shadow-md hover:shadow-lg active:scale-[0.98]">
                Vytvořit profil
            </Button>
        </form>
    );
};
