package org.yang1.eapproval.document.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.yang1.eapproval.common.entity.BaseEntity;
import org.yang1.eapproval.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "document_attachments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DocumentAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="document_id", nullable = false)
    @ToString.Exclude
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uploader_id", nullable = false)
    @ToString.Exclude
    private User uploader;

    @Column(name="original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name="stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(name="storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name="file_size", nullable = false)
    private Long fileSize;

    @Column(name="file_extension", nullable = false, length = 100)
    private String fileExtension;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;



    @Builder
    private DocumentAttachment(User uploader, String originalName, String storedName, String storagePath, Long fileSize, String fileExtension) {
        this.uploader = Objects.requireNonNull(uploader);
        this.originalName = Objects.requireNonNull(originalName);
        this.storedName = Objects.requireNonNull(storedName);
        this.storagePath = Objects.requireNonNull(storagePath);
        this.fileSize = Objects.requireNonNull(fileSize);
        this.fileExtension = Objects.requireNonNull(fileExtension);
    }


    public static DocumentAttachment createDocumentAttachment(User uploader, String originalName, String storedName, String storagePath, Long fileSize, String fileExtension) {
        return DocumentAttachment.builder()
                .uploader(uploader)
                .originalName(originalName)
                .storedName(storedName)
                .storagePath(storagePath)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .build();
    }
}
