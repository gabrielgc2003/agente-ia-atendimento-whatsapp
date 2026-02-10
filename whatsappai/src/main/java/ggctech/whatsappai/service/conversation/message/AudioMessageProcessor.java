package ggctech.whatsappai.service.conversation.message;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.service.ai.openapi.AudioTranscriptionService;
import ggctech.whatsappai.util.Base64FileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class AudioMessageProcessor implements MessageProcessor {

    private final Base64FileConverter fileConverter;
    private final AudioTranscriptionService transcriptionService;

    @Override
    public String supports() {
        return "audioMessage";
    }

    @Override
    public String process(IncomingMessageDTO message) {
        File audioFile = fileConverter.convert(
                message.getBase64Content(),
                "audio",
                message.getAudioType()
        );
        try {
            return transcriptionService.transcribe(audioFile);
        } finally {
            audioFile.delete();
        }
    }
}

