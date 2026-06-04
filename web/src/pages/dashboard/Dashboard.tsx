import {useSelector} from "react-redux";
import type {UserState} from "../../store/userSlice.ts";

export const Dashboard = () => {
    const user = useSelector((state: UserState) => state.user)

    return (
        <>
            {
                user?.fullname
            }
        </>
    )
}