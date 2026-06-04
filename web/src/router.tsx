import {createBrowserRouter} from "react-router";
import {Login} from "./pages/login/Login.tsx";
import {Dashboard} from "./pages/dashboard/Dashboard.tsx";

export const router = createBrowserRouter([
    {
        path: "/login",
        Component: Login
    },
    {
        path: "/",
        Component: Dashboard
    }
])