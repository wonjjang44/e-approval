package org.yang1.eapproval.document.presentation.api.dto.reponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.status.DocumentStatus;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentRejectResponse {

    private Long documentId;
    private String drafterName;

    private String title;
    private String content;
    private DocumentStatus status;

    private List<ApprovalStepResponse> steps;



    public static DocumentRejectResponse from(Document document) {
        List<ApprovalStepResponse> approvalSteps = document.getApprovalLine().getApprovalSteps() == null ? List.of() : document.getApprovalLine()
                .getApprovalSteps().stream()
                    .map(ApprovalStepResponse::from)
                    .toList();

        return new DocumentRejectResponse(
                document.getId(),
                document.getDrafter().getUserName(),
                document.getTitle(),
                document.getContent(),
                document.getDocumentStatus(),
                approvalSteps
        );
    }

}
