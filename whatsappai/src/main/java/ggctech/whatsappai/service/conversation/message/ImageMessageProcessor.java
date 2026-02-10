package ggctech.whatsappai.service.conversation.message;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.service.ai.openapi.ImageAnalysisService;
import ggctech.whatsappai.util.Base64FileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class ImageMessageProcessor implements MessageProcessor {
    private final Base64FileConverter fileConverter;
    private final ImageAnalysisService imageAnalysisService;


    @Override
    public String supports() {
        return "imageMessage";
    }

    @Override
    public String process(IncomingMessageDTO message) {
        File imageFile = fileConverter.convert(
                message.getBase64Content(),
                "image",
                null
        );

        try {
            String legenda = "";
            String description = imageAnalysisService.describe(imageFile);
            if (message.getCaption() != null) {
                legenda =
                        "- Legenda da imagem enviada pelo lead:\n" +
                                message.getCaption()+ "\n";
            }
            String template =
            "A pessoa enviou uma imagem durante a conversa.\n" +
                    legenda +
                    "- Descrição da imagem enviada pelo lead:\n" +
                    description
                    .replace("\n", "\\n")
                    .replaceAll("['*]", "")
                    .trim();
            return template;

        } finally {
            imageFile.delete();
        }
    }
}
