import { Button } from "../../../commons/components/ui/Button.tsx";
import { InputOtp } from "../../../commons/components/ui/InputOtp/InputOtp.tsx";
import { InputOtpGroup } from "../../../commons/components/ui/InputOtp/InputOtpGroup.tsx";
import { InputOtpSlot } from "../../../commons/components/ui/InputOtp/InputOtpSlot.tsx";

interface OtpStepProps {
    otp: string;
    onChange: (value: string) => void;
    onBack: () => void;
    isLoading?: boolean;
}

export const OtpStep = ({ otp, onChange, onBack, isLoading = false }: OtpStepProps) => {
    return (
        <div className="space-y-6 animate-fade-in text-center">
            <div className="space-y-2">
                <p className="text-sm text-muted-foreground">
                    Zadej 6-místný kód z e-mailu
                </p>
                <div className="flex justify-center py-2 relative">
                    <InputOtp 
                        maxLength={6} 
                        value={otp} 
                        onChange={onChange}
                        autoFocus
                        disabled={isLoading}
                    >
                        <InputOtpGroup>
                            <InputOtpSlot index={0} />
                            <InputOtpSlot index={1} />
                            <InputOtpSlot index={2} />
                            <InputOtpSlot index={3} />
                            <InputOtpSlot index={4} />
                            <InputOtpSlot index={5} />
                        </InputOtpGroup>
                    </InputOtp>
                    {isLoading && (
                        <div className="absolute inset-0 bg-card/60 backdrop-blur-[1px] flex items-center justify-center rounded-md animate-fade-in">
                            <span className="w-6 h-6 border-2 border-primary/30 border-t-primary rounded-full animate-spin" />
                        </div>
                    )}
                </div>
            </div>
            <Button
                variant="ghost"
                onClick={onBack}
                disabled={isLoading}
                className="w-full text-muted-foreground hover:text-foreground hover:bg-accent/50"
            >
                Zpět
            </Button>
        </div>
    );
};
