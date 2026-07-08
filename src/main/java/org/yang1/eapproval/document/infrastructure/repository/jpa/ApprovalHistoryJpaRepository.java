package org.yang1.eapproval.document.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yang1.eapproval.document.domain.entity.ApprovalHistory;

public interface ApprovalHistoryJpaRepository extends JpaRepository<ApprovalHistory, Long> {
}
