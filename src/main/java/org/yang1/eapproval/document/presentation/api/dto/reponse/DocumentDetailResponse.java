package org.yang1.eapproval.document.presentation.api.dto.reponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.status.DocumentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDetailResponse {

    private Long documentId;
    private String drafterName;
    private String title;
    private String content;
    private DocumentStatus documentStatus;

    private List<ApprovalStepResponse> steps;

    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime withdrawnAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public static DocumentDetailResponse from(Document document) {
        return new DocumentDetailResponse(
                document.getId(),
                document.getDrafter().getUserName(),
                document.getTitle(),
                document.getContent(),
                document.getDocumentStatus(),
                document.getApprovalLine() == null ? List.of() : document.getApprovalLine().getApprovalSteps().stream().
                        map(ApprovalStepResponse::from).toList(),
                document.getSubmittedAt(),
                document.getCompletedAt(),
                document.getRejectedAt(),
                document.getWithdrawnAt(),
                document.getDeletedAt(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }


}
