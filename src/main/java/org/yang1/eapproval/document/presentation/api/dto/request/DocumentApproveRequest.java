package org.yang1.eapproval.document.presentation.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.document.application.command.DocumentApproveCommand;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentApproveRequest {

    @NotNull(message = "상신할 문서번호는 누락될 수 없습니다.")
    private Long documentId;

    @NotNull(message = "결재자는 누락될 수 없습니다.")
    private Long approverId;

    private String commentText;


    public DocumentApproveCommand toCommand() {
        return DocumentApproveCommand.of(this.documentId, this.approverId, this.commentText);
    }

}
