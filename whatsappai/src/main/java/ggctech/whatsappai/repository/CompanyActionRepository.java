package ggctech.whatsappai.repository;

import ggctech.whatsappai.domain.destination.CompanyAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyActionRepository
        extends JpaRepository<CompanyAction, UUID> {

    @Query("""
        SELECT a
        FROM CompanyAction a
        WHERE a.companyNumber.instanceId = :instanceId
          AND a.active = true
        ORDER BY a.name ASC
    """)
    List<CompanyAction> findActiveAiActionsByInstanceId(
            @Param("instanceId") String instanceId
    );
}
