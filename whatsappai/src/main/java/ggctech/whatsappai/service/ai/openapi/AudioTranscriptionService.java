package ggctech.whatsappai.service.ai.openapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AudioTranscriptionService {

    private final OpenAiService openAiService;

    public String transcribe(File audioFile) {
        return openAiService.transcribeAudio(audioFile);
    }
}
