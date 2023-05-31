package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.ride.CreateRideDTO;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.ReasonDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RideService {

    @POST("ride")
    Call<CreatedRideDTO> createRide(@Body CreateRideDTO dto);

    @GET("ride/passenger/{passengerId}/active")
    Call<CreatedRideDTO> getPassengerActiveRide(@Path("passengerId") int id);

    @GET("ride/driver/{driverId}/active")
    Call<CreatedRideDTO> getDriverActiveRide(@Path("driverId") int id);

    @GET("driver/{id}/vehicle")
    Call<VehicleDTO> getDriversVehicle(@Path("id") int id);

    @GET("ride/{id}")
    Call<CreatedRideDTO> getRide(@Path("id") int id);

    @PUT("ride/{id}/start")
    Call<CreatedRideDTO> acceptRide(@Path("id") int id);

    @PUT("ride/{id}/end")
    Call<CreatedRideDTO> endRide(@Path("id") int id);

    @PUT("ride/{id}/cancel")
    Call<CreatedRideDTO> cancelRide(@Path("id") int id, @Body ReasonDTO dto);

    @PUT("ride/{id}/panic")
    Call<CreatedRideDTO> panicRide(@Path("id") int id, @Body ReasonDTO dto);
}
