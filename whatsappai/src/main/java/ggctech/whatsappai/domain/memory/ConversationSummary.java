package ggctech.whatsappai.domain.memory;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummary implements Serializable {

    @Builder.Default
    private List<String> bullets = new ArrayList<>();
}
