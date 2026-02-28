package ggctech.whatsappai.repository;

import ggctech.whatsappai.domain.schedule.CompanySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyScheduleRepository extends JpaRepository<CompanySchedule, UUID> {

    List<CompanySchedule> findByCompanyNumberIdAndDayOfWeek(
            UUID companyNumberId,
            DayOfWeek dayOfWeek
    );
}
