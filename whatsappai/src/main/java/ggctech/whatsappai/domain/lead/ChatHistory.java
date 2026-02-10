package ggctech.whatsappai.domain.lead;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.enums.Sender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_histories")
@Getter
@Setter
public class ChatHistory extends BaseModel {
    private String instanceId;
    private String remoteJid;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Enumerated(EnumType.STRING)
    private Sender sender;
}
