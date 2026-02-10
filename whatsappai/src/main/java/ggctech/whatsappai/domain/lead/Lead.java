package ggctech.whatsappai.domain.lead;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.domain.destination.ServiceDestination;
import ggctech.whatsappai.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "leads",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_number_id", "client_number"}))
@Data
public class Lead extends BaseModel {

    @ManyToOne
    private CompanyNumber companyNumber;

    @Column(nullable = false)
    private String clientNumber;

    private String name;

    @ManyToOne
    private ServiceDestination serviceDestination;

    private boolean routingLocked;

    private boolean aiEnabled;

    private Instant blockedUntil;

    private Instant lastMessageAt;

    @Enumerated(EnumType.STRING)
    private LeadStatus leadStatus;
}
