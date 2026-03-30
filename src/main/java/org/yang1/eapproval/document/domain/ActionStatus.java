package org.yang1.eapproval.document.domain;

public enum ActionStatus {

    CREATE, // 기안 생성
    SAVE, // 임시저장
    SUBMIT, // 상신
    APPROVE, // 승인
    REJECT, // 반려
    WITHDRAW, // 회수
    RESUBMIT, // 재상신
    HOLD // 보류
    ;
}
