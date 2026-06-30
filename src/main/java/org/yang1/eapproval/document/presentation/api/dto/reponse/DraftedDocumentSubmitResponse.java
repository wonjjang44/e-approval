package org.yang1.eapproval.document.presentation.api.dto.reponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.status.DocumentStatus;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DraftedDocumentSubmitResponse {

    private Long documentId;
    private String drafterName;
    private String title;
    private String content;
    private DocumentStatus documentStatus;

    private List<ApprovalStepResponse> steps;



    public static DraftedDocumentSubmitResponse from(Document document) {
        return new DraftedDocumentSubmitResponse(
                document.getId(),
                document.getDrafter().getUserName(),
                document.getTitle(),
                document.getContent(),
                document.getDocumentStatus(),
                document.getApprovalLine() == null ? List.of() : document.getApprovalLine().getApprovalSteps().stream()
                        .map(ApprovalStepResponse::from)
                        .toList()
        );
    }
}
