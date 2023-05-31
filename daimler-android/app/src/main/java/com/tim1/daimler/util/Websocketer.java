package com.tim1.daimler.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tim1.daimler.BuildConfig;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class Websocketer {

    public static final String api_path = Config.IP_ADDRESS + ":8080/aws";
    public static WebSocketClient webSocketClient;

    public static void createWebSocketClient(String id, MessageReceiver callback) {
        URI uri;
        try {
            uri = new URI("ws://" + api_path);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.d("WebSocket", "Session is starting");
                webSocketClient.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                Log.d("WebSocket", "Message received");
                Log.d("WebSocket", s);
                callback.receive(s);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                this.connect();
            }
        };
        webSocketClient.addHeader("id", id);
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(200);
        webSocketClient.connect();
    }
}
