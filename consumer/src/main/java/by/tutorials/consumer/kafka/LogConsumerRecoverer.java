package by.tutorials.consumer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogConsumerRecoverer implements ConsumerRecordRecoverer {

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        log.error("""
                
                CONSUME EXCEPTION:
                    consumer record   : %s
                    exception message : %s
                """.formatted(consumerRecord, e.getMessage()), e);
    }
}
