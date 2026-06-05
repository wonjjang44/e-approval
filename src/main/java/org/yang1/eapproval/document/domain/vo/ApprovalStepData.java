package org.yang1.eapproval.document.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.user.domain.entity.User;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovalStepData {

    private final User approver;
    private final int stepOrder;
    private final String commentText;


    /**
     * valid 적용 후 생성
     *
     * @param approver
     * @param stepOrder
     * @param commentText
     *
     * @return ApprovalStepData
     */
    public static ApprovalStepData of(User approver, int stepOrder, String commentText) {
        if(approver == null) throw new IllegalArgumentException("결재자는 필수값 입니다.");
        if(stepOrder < 1) throw new IllegalArgumentException("결재 순서는 1 이상이여야 합니다.");

        return new ApprovalStepData(approver, stepOrder, commentText);
    }

}
