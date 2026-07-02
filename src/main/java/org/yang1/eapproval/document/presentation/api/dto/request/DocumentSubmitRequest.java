package org.yang1.eapproval.document.presentation.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.document.application.command.ApprovalStepCommand;
import org.yang1.eapproval.document.application.command.DocumentSubmitCommand;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentSubmitRequest {

    @NotNull(message = "기안자는 누락될 수 없습니다.")
    private Long drafterId;

    @NotBlank(message = "제목은 누락될 수 없습니다.")
    private String title;
    private String content;

    @Valid
    @NotEmpty(message = "결재자는 누락될 수 없습니다.")
    private List<ApprovalStepRequest> steps;


    public DocumentSubmitCommand toCommand() {
        List<ApprovalStepCommand> approvalSteps =  this.steps == null ? List.of() : steps.stream()
                .map(ApprovalStepRequest::toCommand)
                .toList();

        return DocumentSubmitCommand.of(this.drafterId, this.title, this.content, approvalSteps);
    }

}
