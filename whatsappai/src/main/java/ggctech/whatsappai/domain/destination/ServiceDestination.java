package ggctech.whatsappai.domain.destination;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.enums.DestinationType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "service_destinations")
public class ServiceDestination  extends BaseModel {

    @ManyToOne
    private CompanyNumber companyNumber;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> specialties;

    private String destinationNumber;

    @Enumerated(EnumType.STRING)
    private DestinationType type;

    private boolean active;

    private boolean aiEnabled;
}
