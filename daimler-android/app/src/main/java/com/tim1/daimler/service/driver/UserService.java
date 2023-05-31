package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.ride.GraphStatsDTO;
import com.tim1.daimler.dtos.user.UserGraphStatsDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {

    @GET("user/stats/{id}/{from}/{to}")
    Call<UserGraphStatsDTO> getGraphStats(@Path("id") int id, @Path("from") long from, @Path("to") long to);
}
