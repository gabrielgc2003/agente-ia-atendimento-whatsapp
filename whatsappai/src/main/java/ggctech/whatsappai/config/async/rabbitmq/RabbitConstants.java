package ggctech.whatsappai.config.async.rabbitmq;

public final class RabbitConstants {

    public static final String CONVERSATION_EXCHANGE = "conversation.exchange";
    public static final String CONVERSATION_QUEUE = "conversation.queue";
    public static final String CONVERSATION_ROUTING_KEY = "conversation.received";

    private RabbitConstants() {}
}
