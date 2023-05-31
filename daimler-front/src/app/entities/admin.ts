export interface Admin {
    id: number;
    name: string;
    surname: string;
    email: string;
}

export interface UpdateAdminDTO {
    name: string;
    surname: string;
    email: string;
    password: string;
}