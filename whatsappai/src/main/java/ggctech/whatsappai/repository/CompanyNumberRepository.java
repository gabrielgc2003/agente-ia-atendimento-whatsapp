package ggctech.whatsappai.repository;

import ggctech.whatsappai.domain.company.CompanyNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyNumberRepository extends JpaRepository<CompanyNumber, UUID> {
    Optional<CompanyNumber> findByInstanceId(String instanceId);
}
