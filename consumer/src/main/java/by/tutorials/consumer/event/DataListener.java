package by.tutorials.consumer.event;

import by.tutorials.common.dto.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataListener {

    @KafkaListener(topics = "${app.kafka.topics.data}")
    public void listen(@Payload Data payload) {
        log.info("""
                
                RECEIVED:
                    payload: {}\
                """, payload);
    }
}
