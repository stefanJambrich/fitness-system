import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { UserBaseDto } from "../commons/api/schemas/UserBaseDto.ts";

export interface UserState {
    user: UserBaseDto | null;
}

const initialState: UserState = {
    user: null,
};

const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        setUser(state, action: PayloadAction<UserBaseDto>) {
            state.user = action.payload;
        },
        setUserInfo(state, action: PayloadAction<{ user: UserBaseDto; authMethod?: string }>) {
            state.user = action.payload.user;
        },
        clearUser(state) {
            state.user = null;
        },
    },
});

export const { setUser, setUserInfo, clearUser } = userSlice.actions;
export default userSlice.reducer;
