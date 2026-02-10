package ggctech.whatsappai.service.conversation.message;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageProcessorRegistry {

    private final Map<String, MessageProcessor> processors;

    public MessageProcessorRegistry(List<MessageProcessor> processorList) {
        this.processors = processorList.stream()
                .collect(Collectors.toMap(
                        MessageProcessor::supports,
                        Function.identity()
                ));
    }

    public String process(IncomingMessageDTO message) {

        String type = message.getMessageType();

        MessageProcessor processor = processors.get(type);

        if (processor == null) {
            throw new IllegalArgumentException(
                    "No processor found for message type: " + type
            );
        }

        return processor.process(message);
    }
}
