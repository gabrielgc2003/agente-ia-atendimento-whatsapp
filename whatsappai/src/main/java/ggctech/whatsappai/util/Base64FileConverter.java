package ggctech.whatsappai.util;

import java.io.File;

public interface Base64FileConverter {
    File convert(String base64, String filename, String mimeType);
}
