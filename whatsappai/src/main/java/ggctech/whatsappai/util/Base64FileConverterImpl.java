package ggctech.whatsappai.util;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
public class Base64FileConverterImpl implements Base64FileConverter {

    @Override
    public File convert(String base64, String filename, String mimeType) {

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);

            String extension = resolveExtension(mimeType, filename);

            Path tempFile = Files.createTempFile(
                    filename != null ? filename.replace("." + extension, "") : "file",
                    "." + extension
            );

            Files.write(tempFile, decodedBytes);

            return tempFile.toFile();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert base64 to file", e);
        }
    }

    private String resolveExtension(String mimeType, String filename) {

        // 1️⃣ Se o filename já tiver extensão, respeita
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1);
        }

        if (mimeType == null) {
            return "tmp";
        }

        // 2️⃣ Normaliza mimeType (remove codecs)
        String normalizedMime = mimeType.toLowerCase().split(";")[0].trim();

        return switch (normalizedMime) {

            // ÁUDIO
            case "audio/mpeg", "audio/mp3" -> "mp3";
            case "audio/ogg" -> "ogg";      // WhatsApp envia opus aqui
            case "audio/wav" -> "wav";
            case "audio/webm" -> "webm";
            case "audio/mp4" -> "mp4";

            // IMAGEM
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";

            default -> "tmp";
        };
    }
}


