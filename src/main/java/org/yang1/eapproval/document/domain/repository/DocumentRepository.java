package org.yang1.eapproval.document.domain.repository;

import org.yang1.eapproval.document.domain.entity.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {

    Optional<Document> findById(Long id);

    Document save(Document document);

    List<Document> findAll();
}
