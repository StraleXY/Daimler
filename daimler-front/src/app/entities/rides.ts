export interface UserShortDTO {
    id : number;
    email : string;
}

export interface UserInRideDTO {
    id : number;
    email : string;
    name: string;
    surname: string;
    profilePicture: string;
}

export interface RejectionDTO {
    reason : string;
    timeOfRejection : string;
}

export interface LocationDTO {
    id : number;
    address : string;
    latitude : number;
    longitude : number;
}

export interface DepartureDestinationDTO {
    departure : LocationDTO;
    destination : LocationDTO;
}

export interface RideDTO {
    id: number;
    startTime: string;
    endTime: string;
    totalCost : number;
    driver : UserInRideDTO;
    passengers : UserInRideDTO[];
    estimatedTimeInMinutes : number;
    vehicleType : string;
    petTransport : boolean;
    babyTransport : boolean;
    rejection : RejectionDTO;
    locations : DepartureDestinationDTO[];
    status : string;
    distance: number;
    scheduledTimestamp: number;
}

export interface UserRidesDTO {
    totalCount : number;
    results : RideDTO[];
}

export interface FavoriteRouteDTO {
    id: number;
    departure: LocationDTO;
    destination: LocationDTO;
    passengerId: number;
}

export interface InvitationDTO {
    inviterId: number;
    invitedEmail: string;
    addressFrom: string;
    addressTo: string;
}

export interface InvitationResponseDTO {
    inviterId: number;
    invitedId: number;
    invitedEmail: string;
    accepted: boolean;
}

// {
  //   id: 0,
  //   startTime: '2017-07-21T17:32:28Z',
  //   endTime: '',
  //   totalCost: 360,
  //   driver: {
  //     id : 0,
  //     email : ''
  //   },
  //   passengers: [
  //     {
  //       id : 0,
  //       email : ''
  //     }
  //   ],
  //   estimatedTimeInMinutes: 0,
  //   vehicleType: '',
  //   petTransport: false,
  //   babyTransport: false,
  //   rejection: {
  //     reason: '',
  //     timeOfRejection: ''
  //   },
  //   locations: [
  //     {
  //       address : "Fruskogorska 25",
  //       latitude : 40.123,
  //       longitude: 19.324
  //     },
  //     {
  //       address : "Prote Mirkovica 8",
  //       latitude : 40.123,
  //       longitude: 19.324
  //     },
  //     {
  //       address : "Bulevar Oslobodjenja 25",
  //       latitude : 40.123,
  //       longitude: 19.324
  //     }
  //   ],
  //   status: ''
  // }
