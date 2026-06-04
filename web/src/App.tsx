import {RouterProvider} from "react-router";
import {router} from "./router.tsx";
import "./styles/index.css";

export default function App() {
  return <RouterProvider router={router} />;
}