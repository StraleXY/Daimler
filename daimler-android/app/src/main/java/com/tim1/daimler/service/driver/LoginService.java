package com.tim1.daimler.service.driver;


import com.tim1.daimler.dtos.user.LoginDTO;
import com.tim1.daimler.dtos.user.RegisterDTO;
import com.tim1.daimler.dtos.user.TokenDTO;
import com.tim1.daimler.dtos.user.UpdateUserDTO;
import com.tim1.daimler.dtos.user.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LoginService {

    @POST("user/login")
    Call<TokenDTO> doLogin(@Body LoginDTO dto);

    @POST("passenger")
    Call<UserDTO> register(@Body RegisterDTO user);

    @GET("passenger/activate/{token}")
    Call<String> activate(@Path("token") String token);
}
