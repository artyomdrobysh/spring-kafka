package by.tutorials.consumer.config;

import by.tutorials.consumer.config.property.KafkaProps;
import by.tutorials.consumer.exception.RecoverException;
import by.tutorials.consumer.exception.RetryException;
import by.tutorials.consumer.exception.RetryRecoverException;
import by.tutorials.consumer.kafka.LogConsumerRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static java.util.Map.entry;

@Configuration
public class KafkaConfig implements KafkaListenerConfigurer {

    @Autowired
    private LocalValidatorFactoryBean validator;
    @Autowired
    private KafkaProps kafkaProps;
    @Autowired
    private LogConsumerRecoverer recoverer;

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar kafkaListenerEndpointRegistrar) {
        kafkaListenerEndpointRegistrar.setValidator(validator);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConsumerFactory<Object, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<?, ?> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory);
        containerFactory.setMessageConverter(new StringJsonMessageConverter());
        containerFactory.setRetryTemplate(retryTemplate());
        containerFactory.setErrorHandler(errorHandler());
        return containerFactory;
    }

    private ErrorHandler errorHandler() {
        SeekToCurrentErrorHandler errorHandler = new SeekToCurrentErrorHandler(recoverer, new FixedBackOff(kafkaProps.getRecoverBackOffPeriod(), kafkaProps.getRecoverMaxAttempts()));
        errorHandler.addNotRetryableExceptions(
            MethodArgumentNotValidException.class,
            RetryException.class
        );
        return errorHandler;
    }

    private RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = Map.ofEntries(
            entry(RetryException.class, true),
            entry(RecoverException.class, false),
            entry(RetryRecoverException.class, true),
            entry(MethodArgumentNotValidException.class, false)
        );
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(kafkaProps.getRetryMaxAttempts(), retryableExceptions, true);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(kafkaProps.getRetryBackOffPeriod());

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
