package org.yang1.eapproval.document.domain.status;

public enum ActionType {

    CREATED,    // 임시저장
    UPDATED,    // 수정
    SUBMITTED,  // 상신
    APPROVED,   // 승인
    REJECTED,   // 반려
    WITHDRAWN,  // 회수
    DELETED     // 삭제

}
