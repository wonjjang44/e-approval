package org.yang1.eapproval.document.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yang1.eapproval.document.domain.entity.DocumentHistory;

public interface DocumentHistoryJpaRepository extends JpaRepository<DocumentHistory, Long> {
}
