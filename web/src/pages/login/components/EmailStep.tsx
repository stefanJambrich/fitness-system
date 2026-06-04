import * as React from "react";
import { Button } from "../../../commons/components/ui/Button.tsx";
import { Input } from "../../../commons/components/ui/Input.tsx";

interface EmailStepProps {
    email: string;
    onChange: (value: string) => void;
    onSubmit: (e: React.FormEvent) => void;
    isLoading?: boolean;
}

export const EmailStep = ({ email, onChange, onSubmit, isLoading = false }: EmailStepProps) => {
    return (
        <form onSubmit={onSubmit} className="space-y-6 animate-fade-in">
            <div className="space-y-2">
                <label 
                    htmlFor="email" 
                    className="block text-sm font-medium text-foreground"
                >
                    E-mailová adresa
                </label>
                <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder="tvuj@email.cz"
                    required
                    className="w-full"
                    autoFocus
                    disabled={isLoading}
                />
            </div>
            <Button 
                type="submit" 
                disabled={isLoading || !email.trim()}
                className="w-full transition-all duration-300 font-semibold shadow-md hover:shadow-lg active:scale-[0.98] flex items-center justify-center gap-2"
            >
                {isLoading ? (
                    <>
                        <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                        Odesílám...
                    </>
                ) : (
                    "Pokračovat"
                )}
            </Button>
        </form>
    );
};
