import { createBrowserRouter } from "react-router";
import { Login } from "./pages/login/Login.tsx";
import { Dashboard } from "./pages/dashboard/Dashboard.tsx";
import { ProtectedRoute } from "./commons/components/ProtectedRoute.tsx";
import { GuestRoute } from "./commons/components/GuestRoute.tsx";

export const router = createBrowserRouter([
    {
        Component: GuestRoute,
        children: [
            { path: "/login", Component: Login },
        ],
    },
    {
        Component: ProtectedRoute,
        children: [
            { path: "/", Component: Dashboard },
        ],
    },
])