package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.driver.SimpleStatsDTO;
import com.tim1.daimler.dtos.ride.GraphStatsDTO;
import com.tim1.daimler.dtos.ride.RidesDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.dtos.user.UpdateUserDTO;
import com.tim1.daimler.dtos.user.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverService {

    @GET("driver/{id}")
    Call<UserDTO> getById(@Path("id") int id);
    @GET("driver/{id}/vehicle")
    Call<VehicleDTO> getVehicle(@Path("id") int id);

    @PUT("driver/{id}")
    Call<UserDTO> update(@Path("id") int id, @Body UpdateUserDTO updated);

    @GET("driver/stats/simple/{id}/{from}/{to}")
    Call<SimpleStatsDTO> getSimpleStats(@Path("id") int id, @Path("from") long from, @Path("to") long to);

    @GET("driver/stats/graph/{id}/{from}/{to}")
    Call<GraphStatsDTO> getGraphStats(@Path("id") int id, @Path("from") long from, @Path("to") long to);

    @GET("driver/{id}/ride")
    Call<RidesDTO> getRides(@Path("id") int id, @Query("page") int page, @Query("size") int size, @Query("sort") String sort, @Query("from") String from, @Query("to") String to);
}
