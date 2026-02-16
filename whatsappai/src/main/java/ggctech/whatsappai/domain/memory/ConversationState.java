package ggctech.whatsappai.domain.memory;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationState implements Serializable {

    private String stage;

    @Builder.Default
    private Map<String, Object> fields = new HashMap<>();
}
