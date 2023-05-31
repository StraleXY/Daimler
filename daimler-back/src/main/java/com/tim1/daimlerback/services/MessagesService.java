package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.user.CreateMessageDTO;
import com.tim1.daimlerback.dtos.user.InboxDTO;
import com.tim1.daimlerback.dtos.user.MessageDTO;
import com.tim1.daimlerback.dtos.user.MessagesDTO;
import com.tim1.daimlerback.entities.Message;
import com.tim1.daimlerback.repositories.IMessageRepository;
import com.tim1.daimlerback.repositories.IRideRepository;
import com.tim1.daimlerback.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagesService {

    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private SimpMessagingTemplate template;

    private List<Message> getMessagesBetween(Integer fromId, Integer toId, String type, Integer rideId){
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(fromId);
        ids.add(toId);
        return messageRepository.getAllByReceiverIdInAndSenderIdInAndTypeAndRideId(ids, ids, type, rideId);
    }

    public MessagesDTO getAllBetween(Integer fromId, Integer toId, String type, Integer rideId) {
        List<Message> messages = getMessagesBetween(fromId, toId, type, type.equals("SUPPORT") ? 1 : rideId);
        MessagesDTO all = new MessagesDTO();
        all.setTotalCount(messages.size());
        all.setResults(messages.stream().map(MessageDTO::new).collect(Collectors.toList()));
        return all;
    }

    public List<InboxDTO> getUserInbox(Integer userId) {
        return messageRepository.getAllLastMessages(userId).stream().map(m -> new InboxDTO(userRepository.findById(m.messageWith(userId)).get(), m, rideRepository.findById(m.getRideId()).get().getDestination())).collect(Collectors.toList());
    }

    public MessageDTO sendMessage(Integer fromId, CreateMessageDTO dto) {
        if (dto.getMessage() == null || dto.getReceiverId() == null || dto.getRideId() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        if (userRepository.findById(dto.getReceiverId()).isEmpty()) {
            String value = "Receiver not found!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
//        if (rideRepository.findById(dto.getRideId()).isEmpty()) {
//            String value = "Ride not found!";
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
//        }
        Message message = new Message();
        message.setMessage(dto.getMessage());
        message.setReceiverId(dto.getReceiverId());
        message.setTimestamp(dto.getTimestamp());
        message.setRideId(dto.getRideId());
        message.setSenderId(fromId);
        message.setType(dto.getType());
        Message sent =messageRepository.save(message);
        template.convertAndSend("queue/message/" + dto.getReceiverId(), new MessageDTO(sent));
        return new MessageDTO(sent);
    }

    public MessagesDTO getUserMessages(Integer userId) {
        List<Message> sent = messageRepository.findBySenderId(userId);
        List<Message> received = messageRepository.findBySenderId(userId);
        MessagesDTO dto = new MessagesDTO();
        ArrayList<MessageDTO> total = new ArrayList<MessageDTO>();
        for (Message m : sent) total.add(new MessageDTO(m));
        for (Message m : received) total.add(new MessageDTO(m));
        dto.setResults(total);
        dto.setTotalCount(total.size());
        return dto;
    }
}
