package org.yang1.eapproval.document.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalStepDraftCommand {

    private Long approverId;
    private int stepOrder;
    private String commentText;


    public static ApprovalStepDraftCommand of(Long approverId, int stepOrder, String commentText) {
        return new ApprovalStepDraftCommand(approverId, stepOrder, commentText);
    }


}
