package org.yang1.eapproval.document.domain.status;

public enum ApprovalStepStatus {

    WAITING, // 나의 결재 차례가 아님(결재 대기)
    PENDING, // 나의 결재 차례(결재 가능)
    APPROVED, // 승인
    REJECTED, // 반려
    CANCELED // 결재 취소
    ;
}
