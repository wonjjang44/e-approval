package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.document.domain.ActionStatus;
import org.yang1.eapproval.document.domain.DocumentStatus;
import org.yang1.eapproval.user.domain.entity.User;

import java.util.Objects;

@Entity
@Table(name = "document_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DocumentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="document_id", nullable = false)
    @ToString.Exclude
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="actor_id")
    @ToString.Exclude
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name="action_type", nullable = false, length = 30)
    private ActionStatus actionType;

    @Enumerated(EnumType.STRING)
    @Column(name="from_document_status", length = 30)
    private DocumentStatus fromDocumentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="to_document_status", length = 30)
    private DocumentStatus toDocumentStatus;

    @Column(name="memo", length = 2000)
    private String memo;



    @Builder
    private DocumentHistory(User actor, ActionStatus actionType, DocumentStatus fromDocumentStatus, DocumentStatus toDocumentStatus, String memo) {
        this.actor = Objects.requireNonNull(actor);
        this.actionType = Objects.requireNonNull(actionType);
        this.fromDocumentStatus = fromDocumentStatus;
        this.toDocumentStatus = toDocumentStatus;
        this.memo = memo;
    }


    public static DocumentHistory createDocumentHistory(User actor, ActionStatus actionType, DocumentStatus fromDocumentStatus, DocumentStatus toDocumentStatus, String memo) {
        return DocumentHistory.builder()
                .actor(actor)
                .actionType(actionType)
                .fromDocumentStatus(fromDocumentStatus)
                .toDocumentStatus(toDocumentStatus)
                .memo(memo)
                .build();
    }


    /**
     * 문서와 문서 이력의 연관관계 동기화 메서드
     *
     * @param document 문서 이력이 소속될 문서
     */
    void addDocument(Document document) {
        this.document = document;
    }
}
