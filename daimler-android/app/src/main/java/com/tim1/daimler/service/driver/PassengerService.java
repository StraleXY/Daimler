package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimler.dtos.ride.RidesDTO;
import com.tim1.daimler.dtos.user.UpdateUserDTO;
import com.tim1.daimler.dtos.user.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassengerService {

    @GET("passenger/{id}")
    Call<UserDTO> getById(@Path("id") int id);

    @PUT("passenger/{id}")
    Call<UserDTO> update(@Path("id") int id, @Body UpdateUserDTO updated);

    @GET("ride/{id}/favorite")
    Call<List<FavoriteRouteDTO>> getFavoriteRoutes(@Path("id") int id);

    @DELETE("ride/{id}/favorite/{routeId}")
    Call<FavoriteRouteDTO> deleteFavoriteRoute(@Path("id") int id, @Path("routeId") int routeId);

    @GET("passenger/{id}/ride")
    Call<RidesDTO> getRides(@Path("id") int id, @Query("page") int page, @Query("size") int size, @Query("sort") String sort, @Query("from") String from, @Query("to") String to);

    @POST("ride/{id}/favorite")
    Call<FavoriteRouteDTO> addRouteToFavorite(@Path("id") int id, @Query("departureId") int departureId, @Query("destinationId") int destinationId);
}
