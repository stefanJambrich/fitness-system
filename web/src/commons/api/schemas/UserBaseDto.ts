export interface UserBaseDto {
    id: number;
    email: string;
    role: "TRAINER" | "MEMBER";
    fullname?: string;
    uniqueCode?: string;
}