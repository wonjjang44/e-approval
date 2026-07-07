package org.yang1.eapproval.document.domain.repository;

import org.yang1.eapproval.document.domain.entity.ApprovalHistory;

public interface ApprovalHistoryRepository {

    ApprovalHistory save(ApprovalHistory approvalHistory);

}
