export interface UserBaseDto {
    id: number;
    email: string;
    role: "TRAINER" | "MEMBER";
    name?: string;
    availableCredits?: number;
    uniqueCode?: string;
}