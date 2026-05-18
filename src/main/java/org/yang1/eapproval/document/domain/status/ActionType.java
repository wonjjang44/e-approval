package org.yang1.eapproval.document.domain.status;

public enum ActionType {

    CREATED, // 기안 생성
    UPDATED, // 기안 수정
    SUBMITTED, // 기안 상신
    APPROVED, // 기안 승인
    REJECTED, // 기안 반려
    WITHDRAWN, // 기안 회수
    DELETED // 기안 삭제
    ;

}
