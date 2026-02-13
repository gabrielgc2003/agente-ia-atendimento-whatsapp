package ggctech.whatsappai.domain.dto;

public record EvolutionChatPresenceRequest(
        String number,
        String presence,
        int delay
) {
}
