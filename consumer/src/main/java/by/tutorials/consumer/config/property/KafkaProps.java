package by.tutorials.consumer.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProps {

    @Min(1)
    private int retryMaxAttempts;
    @Min(100)
    private long retryBackOffPeriod;
    @Min(1)
    private int recoverMaxAttempts;
    @Min(100)
    private long recoverBackOffPeriod;
}
