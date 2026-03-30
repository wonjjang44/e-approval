package org.yang1.eapproval.document.domain;

public enum DocumentStatus {

    DRAFT, // 작성중/임시저장
    SUBMITTED, // 상신
    APPROVED, // 승인
    REJECTED, // 반려
    WITHDRAWN, // 회수
    DELETED // 삭제
    ;
}
