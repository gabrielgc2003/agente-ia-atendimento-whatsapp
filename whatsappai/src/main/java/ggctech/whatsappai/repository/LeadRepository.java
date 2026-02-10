package ggctech.whatsappai.repository;

import ggctech.whatsappai.domain.company.CompanyNumber;
import ggctech.whatsappai.domain.lead.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    Optional<Lead> findByCompanyNumberAndClientNumber(CompanyNumber companyNumber, String clientNumber);
}
