package org.yang1.eapproval.document.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DraftedDocumentSubmitCommand {

    private Long documentId;
    private Long drafterId;

    private String title;
    private String content;

    private List<ApprovalStepDraftCommand> steps;



    public static DraftedDocumentSubmitCommand of(Long documentId, Long drafterId, String title, String content, List<ApprovalStepDraftCommand> steps) {
        return new DraftedDocumentSubmitCommand(documentId, drafterId, title, content, steps);
    }
}
