package com.tim1.daimlerback.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimestampProvider {

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
