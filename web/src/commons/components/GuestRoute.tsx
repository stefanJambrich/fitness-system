import { Navigate, Outlet } from "react-router";
import { useAppSelector } from "../../store/hooks.ts";

export function GuestRoute() {
    const { user, initialized } = useAppSelector((state) => state.user);

    if (!initialized) return null;
    if (user) return <Navigate to="/" replace />;

    return <Outlet />;
}
