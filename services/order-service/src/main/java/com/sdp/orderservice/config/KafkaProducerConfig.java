//package com.sdp.orderservice.config;
//
//import com.sdp.orderservice.kafka.OrderEvent;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaProducerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    /**
//     * Producer configuration for Kafka
//     *
//     * @return Map of producer configuration properties
//     */
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        // Additional settings for reliability and performance
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.RETRIES_CONFIG, 10);
//        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
//
//        return props;
//    }
//
//    /**
//     * Producer factory for OrderEvent messages
//     *
//     * @return ProducerFactory for OrderEvent
//     */
//    @Bean
//    public ProducerFactory<String, OrderEvent> orderEventProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    /**
//     * Kafka template for OrderEvent messages
//     *
//     * @return KafkaTemplate for OrderEvent
//     */
//    @Bean
//    public KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate() {
//        return new KafkaTemplate<>(orderEventProducerFactory());
//    }
//
////    @Value("${spring.kafka.bootstrap-servers}")
////    private String bootstrapServers;
////
////    @Bean
////    public ProducerFactory<String, Object> producerFactory() {
////        Map<String, Object> configProps = new HashMap<>();
////        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
////        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
////        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
////        return new DefaultKafkaProducerFactory<>(configProps);
////    }
////
////    @Bean
////    public KafkaTemplate<String, Object> kafkaTemplate() {
////        return new KafkaTemplate<>(producerFactory());
////    }
//}
