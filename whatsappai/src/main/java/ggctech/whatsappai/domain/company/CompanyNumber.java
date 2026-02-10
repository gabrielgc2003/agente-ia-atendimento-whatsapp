package ggctech.whatsappai.domain.company;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.enums.SourceType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "company_numbers")
@Data
public class CompanyNumber extends BaseModel {

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String instanceId;

    @Enumerated(EnumType.STRING)
    private SourceType source;

    private boolean active;

    private boolean aiEnabled;

    @Column(columnDefinition = "TEXT")
    private String basePrompt;
}
