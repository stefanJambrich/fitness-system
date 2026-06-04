import {RouterProvider} from "react-router";
import {Provider} from "react-redux";
import {store} from "./store/store.ts";
import {router} from "./router.tsx";
import "./styles/index.css";

export default function App() {
  return (
    <Provider store={store}>
      <RouterProvider router={router} />
    </Provider>
  );
}