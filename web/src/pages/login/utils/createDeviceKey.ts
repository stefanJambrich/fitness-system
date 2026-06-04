export const createDeviceKey = () => {
    const KEY = "fitreserve_device_key";
    const array = new Uint8Array(16);
    window.crypto.getRandomValues(array);
    const deviceKey = Array.from(array, (byte) => byte.toString(16).padStart(2, "0")).join("");
    localStorage.setItem(KEY, deviceKey);
}