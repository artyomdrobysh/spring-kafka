package by.tutorials.consumer.event;

import by.tutorials.common.dto.MessageData;
import by.tutorials.consumer.exception.RecoverException;
import by.tutorials.consumer.exception.RetryException;
import by.tutorials.consumer.exception.RetryRecoverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Slf4j
@Component
public class DataListener {

    @KafkaListener(topics = "${app.kafka.topics.data}")
    public void listen(@Payload @Valid MessageData payload) {
        log.info("""
                
                RECEIVED:
                    payload: {}\
                """, payload);

        switch (payload.type()) {
            case SUCCESS -> log.debug("success");
            case ERROR -> throw new RuntimeException();
            case ERROR_RETRY -> throw new RetryException();
            case ERROR_RECOVER -> throw new RecoverException();
            case ERROR_RETRY_RECOVER -> throw new RetryRecoverException();
        }
    }
}
