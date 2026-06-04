import { Dumbbell } from "lucide-react";
import { Card } from "../../commons/components/ui/Card.tsx";
import { EmailStep } from "./components/EmailStep.tsx";
import { OtpStep } from "./components/OtpStep.tsx";
import { NicknameStep } from "./components/NicknameStep.tsx";
import { useLoginFlow } from "./hooks/useLoginFlow.ts";

export function Login() {
    const {
        step,
        error,
        progressPercent,
        emailStepProps,
        otpStepProps,
        nicknameStepProps,
    } = useLoginFlow();

    return (
        <div className="min-h-screen bg-linear-to-br from-primary/10 via-background to-secondary/5 flex items-center justify-center p-4 transition-colors duration-300">
            <Card className="w-full max-w-md p-8 bg-card border-border/40 shadow-2xl rounded-2xl relative overflow-hidden transition-all duration-300">
                <div className="absolute top-0 left-0 w-full h-1.5 bg-muted">
                    <div
                        className="bg-primary h-full transition-all duration-500 ease-out"
                        style={{ width: `${progressPercent}%` }}
                    />
                </div>

                <div className="flex flex-col items-center mb-8 mt-2">
                    <div className="bg-primary/10 text-primary p-4 rounded-2xl mb-4 shadow-sm hover:scale-110 hover:rotate-6 transition-all duration-300">
                        <Dumbbell className="w-8 h-8" />
                    </div>
                    <h1 className="text-3xl font-extrabold text-foreground tracking-tight">
                        FitReserve
                    </h1>
                    <p className="text-muted-foreground text-center mt-2 text-sm">
                        Rezervační systém tréninků
                    </p>
                </div>

                {error && (
                    <div className="mb-6 p-4 rounded-xl bg-destructive/10 border border-destructive/20 text-destructive text-sm text-center animate-fade-in flex items-center justify-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5 shrink-0">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z" />
                        </svg>
                        <span>{error}</span>
                    </div>
                )}

                <div className="min-h-55 flex flex-col justify-center">
                    {step === "email" && <EmailStep {...emailStepProps} />}
                    {step === "otp" && <OtpStep {...otpStepProps} />}
                    {step === "nickname" && <NicknameStep {...nicknameStepProps} />}
                </div>
            </Card>
        </div>
    );
}
