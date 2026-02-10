package ggctech.whatsappai.domain.dto;

import ggctech.whatsappai.enums.SourceType;
import lombok.Data;

@Data
public class IncomingMessageDTO{
    private String instanceId;
    private String instanceName;
    private String remoteJid;
    private String messageId;
    private String name;
    private String messageType;
    private String messageContent;
    private boolean fromMe;
    private String audioType;
    private String base64Content;
    private String caption;
    private SourceType sourceType;
    private MessageConfigDTO messageConfig;
}
