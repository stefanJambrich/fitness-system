export const getDeviceKey = (): string | null => {
    const KEY = "fitreserve_device_key";
    const deviceKey = localStorage.getItem(KEY);
    return deviceKey ? deviceKey : null;
}