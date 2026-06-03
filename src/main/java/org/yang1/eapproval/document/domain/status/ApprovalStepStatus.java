package org.yang1.eapproval.document.domain.status;

public enum ApprovalStepStatus {

    WAITING,   // 결재 차례가 아닌 상태
    PENDING,   // 결제 차례
    APPROVED,  // 결재 승인
    REJECTED,  // 결재 반려
    CANCELED   // 취소

}
