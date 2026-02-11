package ggctech.whatsappai.repository;

import ggctech.whatsappai.domain.lead.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, UUID> {
    @Query("SELECT ch FROM ChatHistory ch WHERE ch.instanceId = :instanceId AND ch.remoteJid = :remoteJid ORDER BY ch.createdAt DESC LIMIT :limit")
    List<ChatHistory> getLastMessage(String instanceId, String remoteJid, int limit);
}
