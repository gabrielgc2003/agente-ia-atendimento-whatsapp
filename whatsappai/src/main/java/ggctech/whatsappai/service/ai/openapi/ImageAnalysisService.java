package ggctech.whatsappai.service.ai.openapi;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ImageAnalysisService {
    private final OpenAiService openAiService;

    public String describe(File imageFile) {
        return openAiService.readImage(imageFile,
                "Describe the content of this image in detail, including any objects, people, and the overall scene. Provide a comprehensive description that captures the essence of the image.");
    }
}
