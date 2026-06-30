package org.yang1.eapproval.document.application.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDraftCommand {

    private Long drafterId;
    private String title;
    private String content;

    private List<ApprovalStepCommand> steps;


    public static DocumentDraftCommand of(Long drafterId, String title, String content, List<ApprovalStepCommand> steps) {
        return new DocumentDraftCommand(
                drafterId,
                title,
                content,
                steps == null ? List.of() : steps
        );
    }

}
