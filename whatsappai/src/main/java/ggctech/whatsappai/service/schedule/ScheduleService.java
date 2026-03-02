package ggctech.whatsappai.service.schedule;

import ggctech.whatsappai.domain.schedule.CompanySchedule;
import ggctech.whatsappai.repository.CompanyScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {

    private final CompanyScheduleRepository scheduleRepository;

    public ScheduleService(CompanyScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Retorna true se o momento atual está dentro de algum
     * período configurado para o número da empresa.
     */
    public boolean isWithinSchedule(UUID companyNumberId) {

        LocalDateTime now       = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        DayOfWeek today      = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        List<CompanySchedule> periods = scheduleRepository
                .findByCompanyNumberIdAndDayOfWeek(companyNumberId, today);

        return periods.stream().anyMatch(period ->
                !currentTime.isBefore(period.getStartTime()) &&
                        !currentTime.isAfter(period.getEndTime())
        );
    }
}
