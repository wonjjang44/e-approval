package org.yang1.eapproval.document.presentation.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.document.application.command.DocumentRejectCommand;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentRejectRequest {

    @NotNull(message = "문서번호는 누락될 수 없습니다.")
    private Long documentId;

    @NotNull(message = "결재자는 누락될 수 없습니다.")
    private Long approverId;

    @NotBlank(message = "반려 사유는 누락될 수 없습니다.")
    private String commentText;



    public DocumentRejectCommand toCommand() {
        return DocumentRejectCommand.of(this.documentId, this.approverId, this.commentText);
    }

}
