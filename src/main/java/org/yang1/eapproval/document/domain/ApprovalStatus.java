package org.yang1.eapproval.document.domain;

public enum ApprovalStatus {

    READY, // 내 결재 차례 아님
    PENDING, // 내 결재 차례
    IN_PROGRESS, // 결재 진행 중
    APPROVED, // 승인
    REJECTED, // 반려
    ;
}
