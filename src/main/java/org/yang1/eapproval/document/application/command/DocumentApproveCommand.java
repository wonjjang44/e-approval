package org.yang1.eapproval.document.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentApproveCommand {

    private Long documentId;
    private Long approverId;

    private String commentText;


    public static DocumentApproveCommand of(Long documentId, Long approverId, String commentText) {
        return new DocumentApproveCommand(documentId, approverId, commentText);
    }
}
