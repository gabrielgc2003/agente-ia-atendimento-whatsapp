package ggctech.whatsappai.service.lead;

import ggctech.whatsappai.domain.lead.ChatHistory;
import ggctech.whatsappai.enums.Sender;
import ggctech.whatsappai.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    private static final int MAX_HISTORY_SIZE = 15;

    public List<ChatHistory> getLastMessage(String instanceId, String remoteJid) {
        List<ChatHistory> list = new ArrayList<>();
        list = chatHistoryRepository.getLastMessage(instanceId, remoteJid, MAX_HISTORY_SIZE);
        return list;
    }

    @Transactional
    public void saveMessage(String instanceId, String remoteJid, String message, Sender sender) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setInstanceId(instanceId);
        chatHistory.setRemoteJid(remoteJid);
        chatHistory.setMessage(message);
        chatHistory.setSender(sender);
        chatHistoryRepository.save(chatHistory);
    }

    public String lastMessages(String instanceId, String remoteJid) {
        List<ChatHistory> messages = getLastMessage(instanceId, remoteJid);
        if (messages.isEmpty()) {
            return null;
        }
        return messages
                .stream()
                .map(msg -> msg.getSender().name() + ": " + msg.getMessage())
                .collect(Collectors.joining("\n"));
    }
}
