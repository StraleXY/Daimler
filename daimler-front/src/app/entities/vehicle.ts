import { LocationDTO } from "./rides";

export interface VehicleDTO {
    model : string;
    licenseNumber : string;
    vehicleType: string;
    passengerSeats : number;
    babyTransport : boolean;
    petTransport : boolean;
    currentLocation : LocationDTO;
    driverId : number;
    id : number;
}