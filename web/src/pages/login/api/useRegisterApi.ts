import { useState } from "react";
import axios from "axios";
import { getOrCreateDeviceKey } from "../utils/deviceKey.ts";
import {apiDefinitions} from "./apiDefinitions.ts";
import type {UserBaseDto} from "../../../commons/api/schemas/UserBaseDto.ts";

export interface RegisterRequest {
    registrationToken: string;
    name: string;
    registerAsTrainer?: boolean;
}

export function useRegisterApi() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const registerUser = async (
        request: RegisterRequest
    ): Promise<UserBaseDto> => {
        setIsLoading(true);
        setError(null);
        try {
            const publicKeyHash = getOrCreateDeviceKey();
            const response = await axios.post<UserBaseDto>(
                apiDefinitions.registerApi,
                {
                    ...request,
                    publicKeyHash,
                }
            );
            return {
                ...response.data,
            };
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || "Registrace se nezdařila.";
            setError(errorMsg);
            throw new Error(errorMsg, { cause: err });
        } finally {
            setIsLoading(false);
        }
    };

    return { registerUser, isLoading, error, setError };
}
