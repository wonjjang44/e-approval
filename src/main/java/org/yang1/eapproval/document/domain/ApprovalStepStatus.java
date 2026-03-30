package org.yang1.eapproval.document.domain;

public enum ApprovalStepStatus {

    WAITING, // 아직 나의 결재 차례 아님
    PENDING, // 나의 결재 차례
    APPROVED, // 승인
    REJECTED  // 반려
    ;
}
