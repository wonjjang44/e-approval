package org.yang1.eapproval.document.presentation.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.document.application.command.ApprovalStepDraftCommand;
import org.yang1.eapproval.document.application.command.DraftedDocumentSubmitCommand;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DraftedDocumentSubmitRequest {

    @NotNull(message = "기안자는 누락될 수 없습니다.")
    private Long drafterId;

    @NotBlank(message = "문서 제목은 누락될 수 없습니다.")
    private String title;
    private String content;

    @Valid
    @NotEmpty(message = "결재단계는 누락될 수 없습니다.")
    private List<ApprovalStepDraftRequest> steps;



    public DraftedDocumentSubmitCommand toCommand(Long documentId) {
        List<ApprovalStepDraftCommand> approvalSteps = this.steps == null ? List.of() : steps.stream()
                .map(ApprovalStepDraftRequest::toCommand).toList();

        return DraftedDocumentSubmitCommand.of(documentId, this.drafterId, this.title, this.content, approvalSteps);
    }

}
