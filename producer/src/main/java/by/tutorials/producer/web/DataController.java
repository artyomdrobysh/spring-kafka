package by.tutorials.producer.web;

import by.tutorials.common.dto.MessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DataController {
    
    @Autowired
    private KafkaTemplate<String, MessageData> dataKafkaTemplate;
    @Value("${app.kafka.topics.data}")
    private String dataTopic; 
    
    @PostMapping
    public void sendData(@RequestBody MessageData data) {
        dataKafkaTemplate.send(dataTopic, data).addCallback(
            sr -> log.info("SUCCESS : {}", sr),
            t -> log.error("ERROR", t)
        );
    }
}
