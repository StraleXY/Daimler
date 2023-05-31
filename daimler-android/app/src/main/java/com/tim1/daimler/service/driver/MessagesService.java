package com.tim1.daimler.service.driver;

import com.tim1.daimler.dtos.message.CreateMessageDTO;
import com.tim1.daimler.dtos.message.InboxDTO;
import com.tim1.daimler.dtos.message.MessageDTO;
import com.tim1.daimler.dtos.message.MessagesDTO;
import com.tim1.daimler.dtos.user.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessagesService {

    @GET("user/message/{from_id}/{to_id}/{type}/{rideId}")
    Call<MessagesDTO> getMessagesBetweenUsers(@Path("from_id") int fromId, @Path("to_id") int toId, @Path("type") String type, @Path("rideId") int rideId);

    @GET("user/inbox/{id}")
    Call<List<InboxDTO>> getInbox(@Path("id") int userId);

    @POST("user/{from_id}/message")
    Call<MessageDTO> sendMessage(@Path("from_id") int fromIf, @Body CreateMessageDTO messageDTO);
}
