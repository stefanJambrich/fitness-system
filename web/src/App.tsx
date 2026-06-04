import { useEffect } from "react";
import { RouterProvider } from "react-router";
import { Provider } from "react-redux";
import { store } from "./store/store.ts";
import { useAppDispatch } from "./store/hooks.ts";
import { fetchCurrentUser } from "./store/userSlice.ts";
import { router } from "./router.tsx";
import "./styles/index.css";

function AppContent() {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(fetchCurrentUser());
  }, [dispatch]);

  return <RouterProvider router={router} />;
}

export default function App() {
  return (
    <Provider store={store}>
      <AppContent />
    </Provider>
  );
}