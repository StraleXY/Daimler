import { RideDTO } from "./rides";

export interface UserFull {
    id: number;
    name: string;
    surname: string;
    profilePicture: string;
    telephoneNumber: string;
    email: string;
    address: string;
}

export interface SimpleUserDTO {
    id: number;
    name: string;
    surname: string;
    email: string;
}

export interface UserWithRole {
    id: number;
    name: string;
    surname: string;
    profilePicture: string;
    telephoneNumber: string;
    email: string;
    address: string;
    blocked: boolean;
    role: ERole;
}

export enum ERole {
    ROLE_ADMIN = 0,
    ROLE_DRIVER,
    ROLE_PASSENGER
}

export interface UsersDTO {
    totalCount: number;
    results: UserWithRole[];
}

export interface UpdateUserDTO {
    name: string;
    surname: string;
    profilePicture: string;
    telephoneNumber: string;
    email: string;
    address: string;
    password: string;
}

export interface UpdateRequest {
    old : UserFull;
    updated : UserFull;
}

export interface Note {
    id: number;
    date: string;
    message: string;
    userId: number;
}

export interface NotesDTO {
    totalCount: number;
    results: Note[];
}

export interface UserStatsDTO {
    amount: number;
    totalRides: number;
    totalDistance: number;
    ridesPerDay: number[];
    distancePerDay: number[];
}

export interface CreateMessageDTO {
    receiverId: number;
    message: string;
    type: string;
    rideId: number;
    timestamp: number;
}

export interface MessageDTO {
    id: number;
    timeOfSending: string;
    senderId: number;
    receiverId: number;
    message: string;
    type: string;
    rideId: number;
    timestamp: number;
}

export interface MessagesDTO {
    totalCount: number;
    results: MessageDTO[];
}

export interface InboxDTO {
    with: SimpleUserDTO;
    lastMessage: MessageDTO;
    destination: string;
}

export interface PanicRideDTO {
    id: number;
    user: UserFull;
    ride: RideDTO;
    time: string;
    reason: string;
    vehicleId: number;
}