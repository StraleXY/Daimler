package com.tim1.daimlerback.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;

public class WebsocketMessage {
    public String topic;
    public String body;

    public WebsocketMessage(String topic, Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.topic = topic;
        try {
            this.body = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            this.body = "{}";
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.body = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            this.body = "{}";
        }
    }
}
