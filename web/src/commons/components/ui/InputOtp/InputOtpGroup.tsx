import {cn} from "../../utils.ts";

export const InputOtpGroup = ({ className, ...props }: React.ComponentProps<"div">) => {
    return (
        <div
            data-slot="input-otp-group"
            className={cn("flex items-center gap-1", className)}
            {...props}
        />
    );
}