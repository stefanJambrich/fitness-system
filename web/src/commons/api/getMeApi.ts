import axios from "axios";
import type { UserBaseDto } from "./schemas/UserBaseDto.ts";
import {apiDefinitions} from "./apiDefinitions.ts";
import {getDeviceKey} from "../../pages/login/utils/getDeviceKey.ts";

export async function getMeApi(): Promise<UserBaseDto> {
    const deviceKey = getDeviceKey();
    const response = await axios.get<UserBaseDto>(
        apiDefinitions.meApi,
        {
            headers: { "X-Device-Key": deviceKey },
        }
    );
    return response.data;
}
