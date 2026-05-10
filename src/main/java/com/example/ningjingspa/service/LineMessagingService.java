package com.example.ningjingspa.service;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.PushMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineMessagingService {

    @Autowired
    private MessagingApiClient messagingApiClient;

    /**
     * 推送純文字訊息給指定用戶
     * @param userId LINE 用戶的 User ID
     * @param message 訊息內容
     */
    public void pushMessage(String userId, String message) {
        try {
            PushMessageRequest request = new PushMessageRequest(
                    userId,
                    List.of(new TextMessage(message)),
                    false,
                    null
            );
            messagingApiClient.pushMessage(null, request).get();
        } catch (Exception e) {
            e.printStackTrace();
            // 這裡可以記錄日誌或重試
        }
    }
}