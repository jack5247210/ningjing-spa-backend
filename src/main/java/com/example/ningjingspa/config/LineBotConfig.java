package com.example.ningjingspa.config;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class LineBotConfig {

    @Value("${line.bot.channel-token}")
    private String channelToken;

    @Bean
    public MessagingApiClient messagingApiClient() {
        return MessagingApiClient
                .builder(channelToken)
                .apiEndPoint(URI.create("https://api.line.me"))
                .build();
    }
}