package com.eclectics.collaboration.Tool.Task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class KeepAliveTask {

    @Scheduled(fixedRate = 300000)
    public void pingSelf() {
        String url = " https://syncboard-ptvu.onrender.com/public/health";
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
        }
    }
}
