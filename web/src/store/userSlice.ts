import { createAsyncThunk, createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { UserBaseDto } from "../commons/api/schemas/UserBaseDto.ts";
import { getMeApi } from "../commons/api/getMeApi.ts";

export interface UserState {
    user: UserBaseDto | null;
    initialized: boolean;
}

const initialState: UserState = {
    user: null,
    initialized: false,
};

export const fetchCurrentUser = createAsyncThunk(
    "user/fetchCurrentUser",
    async () => {
        return await getMeApi();
    }
);

const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        setUser(state, action: PayloadAction<UserBaseDto>) {
            state.user = action.payload;
            state.initialized = true;
        },
        clearUser(state) {
            state.user = null;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchCurrentUser.fulfilled, (state, action) => {
                state.user = action.payload;
                state.initialized = true;
            })
            .addCase(fetchCurrentUser.rejected, (state) => {
                state.user = null;
                state.initialized = true;
            });
    },
});

export const { setUser, clearUser } = userSlice.actions;
export default userSlice.reducer;
