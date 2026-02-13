package ggctech.whatsappai.service.conversation.sender;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class InstanceExecutionRegistry {

    private final Map<String, ScheduledExecutorService> executors = new ConcurrentHashMap<>();

    public ScheduledExecutorService getExecutor(String instanceId, String remoteJid) {
        return executors.computeIfAbsent(
                instanceId+"_"+remoteJid,
                key -> Executors.newSingleThreadScheduledExecutor()
        );
    }
}
