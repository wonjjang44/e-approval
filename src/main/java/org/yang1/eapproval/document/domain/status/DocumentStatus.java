package org.yang1.eapproval.document.domain.status;

public enum DocumentStatus {


    DRAFT, // 임시저장
    IN_PROGRESS, // 결재 진행 중
    APPROVED, // 최종 승인 완료
    REJECTED, // 반려
    WITHDRAWN, // 회수
    DELETED // 삭제
    ;
}
