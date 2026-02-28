package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.domain.dto.MessageConfigDTO;
import ggctech.whatsappai.domain.lead.Lead;
import ggctech.whatsappai.enums.LeadStatus;
import ggctech.whatsappai.enums.SourceType;
import ggctech.whatsappai.event.producer.ConversationProducer;
import ggctech.whatsappai.repository.CompanyNumberRepository;
import ggctech.whatsappai.service.lead.LeadService;
import ggctech.whatsappai.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final CompanyNumberRepository companyNumberRepository;
    private final LeadService leadService;
    private final ConversationProducer producer;
    private final RateLimitService rateLimitService;
    private final ScheduleService scheduleService;

    public void handleIncomingMessage(JsonNode template, SourceType sourceType) {

        IncomingMessageDTO incomingMessageDto = new IncomingMessageDTO();

         // Buscar CompanyNumber com base no número de telefone
        if(sourceType == SourceType.META_API){
            incomingMessageDto = fromMetaApiTemplate(template);
        }
        if (sourceType == SourceType.EVOLUTION_API) {
            incomingMessageDto = fromEvolutionApiTemplate(template);
        }else {
            throw new IllegalArgumentException("Unsupported source type: " + sourceType);
        }
        if (incomingMessageDto == null) {
            throw new IllegalArgumentException("Unsupported source type: " + sourceType);
        }
        if (incomingMessageDto.getInstanceId() == null) {
            throw new IllegalArgumentException("Unsupported instance ID: " + incomingMessageDto.getInstanceId());
        }
        incomingMessageDto.setSourceType(sourceType);

        IncomingMessageDTO finalIncomingMessageDto = incomingMessageDto;
        CompanyNumber companyNumber = companyNumberRepository
                .findByInstanceId(incomingMessageDto.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Company number not found for instance ID: " + finalIncomingMessageDto.getInstanceId()));

        if (!scheduleService.isWithinSchedule(companyNumber.getId())) {
            return;
        }

        Lead lead = leadService.getOrCreateLead(
                companyNumber,
                incomingMessageDto.getRemoteJid(),
                incomingMessageDto.getName()
        );

        if (rateLimitService.isSpam(incomingMessageDto.getInstanceId(), incomingMessageDto.getRemoteJid())) {
            leadService.blockTemporarily(
                    companyNumber,
                    incomingMessageDto.getRemoteJid(),
                    Duration.ofHours(3)
            );
            return;
        }
        if (lead == null) {
            throw new IllegalArgumentException("Lead not found for instance ID: " + companyNumber.getInstanceId());
        }

        if (leadService.isBlocked(lead)) {
            return;
        }

        if (incomingMessageDto.isFromMe()) {
            leadService.transferLead(lead);
            return;
        }
        if (companyNumber.getBasePrompt() == null) {
            throw new IllegalArgumentException("Base prompt not configured for company number: " + companyNumber.getInstanceId());
        }
        MessageConfigDTO messageConfig = new MessageConfigDTO();
        messageConfig.setBasePrompt(companyNumber.getBasePrompt());
        incomingMessageDto.setMessageConfig(messageConfig);

        producer.publish(incomingMessageDto);
    }

    private IncomingMessageDTO fromMetaApiTemplate(JsonNode template) {
        IncomingMessageDTO incomingMessageDto = new IncomingMessageDTO();
        return incomingMessageDto;
    }

    private IncomingMessageDTO fromEvolutionApiTemplate(JsonNode template) {
        IncomingMessageDTO incomingMessageDto = new IncomingMessageDTO();
        JsonNode body = template;
        // apikey
        try {
            if (body.get("apikey") != null) {
                incomingMessageDto.setInstanceId(template.get("apikey").asString());
            }
            if (body.get("instance") != null) {
                incomingMessageDto.setInstanceName(template.get("instance").asString());
            }
        } catch (Exception e) {

        }
        JsonNode data = body.get("data");
        // data.pushName
        if (data.get("pushName") != null) {
            incomingMessageDto.setName(data.get("pushName").asString());
        }
        // data.messageType
        if (data.get("messageType") != null) {
            incomingMessageDto.setMessageType(data.get("messageType").asString());
        }
        JsonNode key = data.get("key");
        // data.key.remoteJid
        if (key.get("remoteJid") != null) {
            incomingMessageDto.setRemoteJid(data.get("key").get("remoteJid").asString());
        }
        // data.key.fromMe
        if (key.get("fromMe") != null) {
            incomingMessageDto.setFromMe(data.get("key").get("fromMe").asBoolean());
        }

        JsonNode message = data.get("message");
        // data.message.conversation
        if (message.get("conversation") != null) {
            incomingMessageDto.setMessageContent(message.get("conversation").asString());
        }
        // data.message.base64
        if (message.get("base64") != null) {
            incomingMessageDto.setBase64Content(message.get("base64").asString());
        }

        if (message.get("audioMessage") != null) {
            JsonNode audioMessage = message.get("audioMessage");
            // data.message.audioMessage.mimetype
            if (audioMessage.get("mimetype") != null) {
                incomingMessageDto.setAudioType(audioMessage.get("mimetype").asString());
            }
        }
        if (message.get("imageMessage") != null) {
            JsonNode imageMessage = message.get("imageMessage");
            // data.message.imageMessage.caption
            if (imageMessage.get("caption") != null) {
                incomingMessageDto.setCaption(imageMessage.get("caption").asString());
            }
        }
        return incomingMessageDto;
    }
}
