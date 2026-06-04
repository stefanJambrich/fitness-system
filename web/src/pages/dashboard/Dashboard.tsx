import {useAppSelector} from "../../store/hooks.ts";

export const Dashboard = () => {
    const user = useAppSelector(state => state.user);

    return (
        <div>
            {
                user.user?.name
            }
        </div>
    )
}