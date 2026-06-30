package org.yang1.eapproval.document.presentation.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.document.application.command.ApprovalStepCommand;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalStepRequest {

    @NotNull(message = "결재자는 누락될 수 없습니다.")
    private Long approverId;

    private int stepOrder;
    private String commentText;


    public ApprovalStepCommand toCommand() {
        return ApprovalStepCommand.of(this.approverId, this.stepOrder, this.commentText);
    }
}
