package ggctech.whatsappai.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AiAction {

    private String type;
    private Map<String, Object> payload;
}
