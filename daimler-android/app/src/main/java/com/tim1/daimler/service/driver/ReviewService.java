package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.review.CombinedReviewsDTO;
import com.tim1.daimler.dtos.review.RatingDTO;
import com.tim1.daimler.dtos.review.ReviewDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewService {

    @GET("review/{id}")
    Call<List<CombinedReviewsDTO>> getRideReviews(@Path("id") int id);

    @POST("review/{id}/driver")
    Call<ReviewDTO> reviewRideDriver(@Path("id") int id, @Body RatingDTO review);

    @POST("review/{id}/vehicle")
    Call<ReviewDTO> reviewRideVehicle(@Path("id") int id, @Body RatingDTO review);
}
