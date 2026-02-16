package ggctech.whatsappai.service.lead;

import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.domain.lead.Lead;
import ggctech.whatsappai.enums.LeadStatus;
import ggctech.whatsappai.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    //private final ServiceDestinationRepository serviceDestinationRepository;

    public Lead getOrCreateLead(CompanyNumber companyNumber, String remoteJid, String name) {
        Lead lead = leadRepository.findByCompanyNumberAndClientNumber(companyNumber, remoteJid)
                .orElseGet(() -> createLead(companyNumber, remoteJid, name));
        return lead;
    }

    private Lead createLead(CompanyNumber companyNumber, String remoteJid, String name) {
        Lead lead = new Lead();
        lead.setCompanyNumber(companyNumber);
        lead.setClientNumber(remoteJid);
        lead.setName(name);
        return leadRepository.save(lead);
    }

    public boolean isBlocked(Lead lead) {
        // verifica se está bloqueado pelo blockedUntil
        // atualiza o blockedUntil se o tempo já passou
        Instant blockedUntil = lead.getBlockedUntil();
        if (blockedUntil != null) {
            if (Instant.now().isBefore(blockedUntil)) {
                return true;
            } else {
                lead.setBlockedUntil(null);
                lead.setLeadStatus(LeadStatus.NORMAL);
                leadRepository.save(lead);
            }
        }
        return false;
    }

    public void transferLead(Lead lead) {
        lead.setLeadStatus(LeadStatus.TRANSFERRED);
        lead.setBlockedUntil(
                // bloqueia por 3 horas
                Instant.now().plusSeconds(3 * 60 * 60)
        );
    }

    public void blockTemporarily(CompanyNumber companyNumber, String remoteJid, Duration duration) {
        Lead lead = leadRepository.findByCompanyNumberAndClientNumber(companyNumber, remoteJid)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setBlockedUntil(Instant.now().plus(duration));
        lead.setLeadStatus(LeadStatus.BLOCKED);
        leadRepository.save(lead);
    }

}
