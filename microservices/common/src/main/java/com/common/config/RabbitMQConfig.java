package com.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String BANKING_EXCHANGE = "banking.exchange";
    public static final String TRANSACTION_EXCHANGE = "transaction.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // Queue names
    public static final String TRANSACTION_COMPLETED_QUEUE = "transaction.completed.queue";
    public static final String PAYMENT_INITIATED_QUEUE = "payment.initiated.queue";
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";
    public static final String FRAUD_DETECTED_QUEUE = "fraud.detected.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String SETTLEMENT_QUEUE = "settlement.queue";

    // Routing keys
    public static final String TXN_COMPLETED_KEY = "transaction.completed";
    public static final String PAYMENT_INITIATED_KEY = "payment.initiated";
    public static final String PAYMENT_COMPLETED_KEY = "payment.completed";
    public static final String FRAUD_DETECTED_KEY = "fraud.detected";
    public static final String NOTIFICATION_KEY = "notification.send";
    public static final String AUDIT_KEY = "audit.log";
    public static final String SETTLEMENT_KEY = "settlement.process";

    // Exchanges
    @Bean
    public TopicExchange bankingExchange() {
        return new TopicExchange(BANKING_EXCHANGE);
    }

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue transactionCompletedQueue() {
        return QueueBuilder.durable(TRANSACTION_COMPLETED_QUEUE).build();
    }

    @Bean
    public Queue paymentInitiatedQueue() {
        return QueueBuilder.durable(PAYMENT_INITIATED_QUEUE).build();
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(PAYMENT_COMPLETED_QUEUE).build();
    }

    @Bean
    public Queue fraudDetectedQueue() {
        return QueueBuilder.durable(FRAUD_DETECTED_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE).build();
    }

    @Bean
    public Queue settlementQueue() {
        return QueueBuilder.durable(SETTLEMENT_QUEUE).build();
    }

    // Bindings
    @Bean
    public Binding transactionCompletedBinding(Queue transactionCompletedQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(transactionCompletedQueue).to(transactionExchange).with(TXN_COMPLETED_KEY);
    }

    @Bean
    public Binding paymentInitiatedBinding(Queue paymentInitiatedQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentInitiatedQueue).to(paymentExchange).with(PAYMENT_INITIATED_KEY);
    }

    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentCompletedQueue).to(paymentExchange).with(PAYMENT_COMPLETED_KEY);
    }

    @Bean
    public Binding fraudDetectedBinding(Queue fraudDetectedQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(fraudDetectedQueue).to(transactionExchange).with(FRAUD_DETECTED_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_KEY);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange bankingExchange) {
        return BindingBuilder.bind(auditQueue).to(bankingExchange).with(AUDIT_KEY);
    }

    @Bean
    public Binding settlementBinding(Queue settlementQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(settlementQueue).to(paymentExchange).with(SETTLEMENT_KEY);
    }

    // Message converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
