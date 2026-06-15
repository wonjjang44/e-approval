package org.yang1.eapproval.document.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yang1.eapproval.document.domain.entity.Document;

public interface DocumentJpaRepository extends JpaRepository<Document, Long> {
}
