export function getOrCreateDeviceKey(): string {
    const KEY = "fitreserve_device_key";
    let deviceKey = localStorage.getItem(KEY);
    if (!deviceKey) {
        const array = new Uint8Array(16);
        window.crypto.getRandomValues(array);
        deviceKey = Array.from(array, (byte) => byte.toString(16).padStart(2, "0")).join("");
        localStorage.setItem(KEY, deviceKey);
    }
    return deviceKey;
}
