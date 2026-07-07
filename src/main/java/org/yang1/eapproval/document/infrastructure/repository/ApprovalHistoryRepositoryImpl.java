package org.yang1.eapproval.document.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.document.domain.entity.ApprovalHistory;
import org.yang1.eapproval.document.domain.repository.ApprovalHistoryRepository;
import org.yang1.eapproval.document.infrastructure.repository.jpa.ApprovalHistoryJpaRepository;

@Repository
@RequiredArgsConstructor
public class ApprovalHistoryRepositoryImpl implements ApprovalHistoryRepository {

    private final ApprovalHistoryJpaRepository approvalHistoryJpaRepository;


    @Override
    public ApprovalHistory save(ApprovalHistory approvalHistory) {
        return approvalHistoryJpaRepository.save(approvalHistory);
    }
}
