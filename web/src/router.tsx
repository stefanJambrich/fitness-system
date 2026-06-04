import {createBrowserRouter} from "react-router";
import {Login} from "./pages/login/Login.tsx";

export const router = createBrowserRouter([
    {
        path: "/login",
        Component: Login
    }
])