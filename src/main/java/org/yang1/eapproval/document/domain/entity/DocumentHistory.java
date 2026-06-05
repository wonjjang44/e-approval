package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yang1.eapproval.common.entity.BaseCreatedEntity;
import org.yang1.eapproval.document.domain.status.ActionType;
import org.yang1.eapproval.document.domain.status.DocumentStatus;
import org.yang1.eapproval.user.domain.entity.User;

@Entity
@Table(name = "document_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DocumentHistory extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus beforeDocumentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus afterDocumentStatus;

    @Column(length = 2000)
    private String memo;


    @Builder(access = AccessLevel.PRIVATE)
    private DocumentHistory(Document document, User actor, ActionType actionType, DocumentStatus beforeDocumentStatus, DocumentStatus afterDocumentStatus, String memo) {
        this.document = document;
        this.actor = actor;
        this.actionType = actionType;
        this.beforeDocumentStatus = beforeDocumentStatus;
        this.afterDocumentStatus = afterDocumentStatus;
        this.memo = memo;
    }


    public static DocumentHistory create(Document document, User actor, ActionType actionType, DocumentStatus beforeDocumentStatus, DocumentStatus afterDocumentStatus, String memo) {
        return DocumentHistory.builder()
                .document(document)
                .actor(actor)
                .actionType(actionType)
                .beforeDocumentStatus(beforeDocumentStatus)
                .afterDocumentStatus(afterDocumentStatus)
                .memo(memo)
                .build();
    }
}
