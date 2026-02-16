package ggctech.whatsappai.domain.destination;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_actions")
@Getter @Setter
public class CompanyAction extends BaseModel {

    @ManyToOne
    private CompanyNumber companyNumber;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ActionType type;

    @Column(columnDefinition = "TEXT")
    private String configJson;

    private boolean active;
}

