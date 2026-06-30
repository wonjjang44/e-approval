package org.yang1.eapproval.document.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovalStepCommand {

    private Long approverId;
    private int stepOrder;
    private String commentText;


    public static ApprovalStepCommand of(Long approverId, int stepOrder, String commentText) {
        return new ApprovalStepCommand(approverId, stepOrder, commentText);
    }


}
