package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.ride.AssumptionDTO;
import com.tim1.daimler.dtos.ride.EstimationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UnregisteredUserService {

    @POST("unregisteredUser/")
    Call<EstimationDTO> getEstimate(@Body AssumptionDTO dto);
}
