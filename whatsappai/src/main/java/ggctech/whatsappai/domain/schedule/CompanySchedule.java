package ggctech.whatsappai.domain.schedule;

import ggctech.whatsappai.domain.BaseModel;
import ggctech.whatsappai.domain.company.CompanyNumber;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "company_schedules")
public class CompanySchedule extends BaseModel {
    @ManyToOne
    private CompanyNumber companyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY...

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // ex: 08:00

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;   // ex: 12:00

}
