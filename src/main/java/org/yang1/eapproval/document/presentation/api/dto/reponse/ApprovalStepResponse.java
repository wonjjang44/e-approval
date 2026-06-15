package org.yang1.eapproval.document.presentation.api.dto.reponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.document.domain.entity.ApprovalStep;
import org.yang1.eapproval.document.domain.status.ApprovalStepStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovalStepResponse {

    private Long approvalStepId;

    private int stepOrder;
    private String approverName;
    private String commentText;
    private LocalDateTime actedAt;

    private ApprovalStepStatus stepStatus;



    public static ApprovalStepResponse from(ApprovalStep step) {
        return new ApprovalStepResponse(
                step.getId(),
                step.getStepOrder(),
                step.getApprover().getUserName(),
                step.getCommentText(),
                step.getActedAt(),
                step.getStepStatus()
        );
    }

}
