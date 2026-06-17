package org.yang1.eapproval.document.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.repository.DocumentRepository;
import org.yang1.eapproval.document.infrastructure.repository.jpa.DocumentJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository documentJpaRepository;



    @Override
    public Optional<Document> findById(Long id) {
        return documentJpaRepository.findById(id);
    }


    @Override
    public Document save(Document document) {
        return documentJpaRepository.save(document);
    }


    @Override
    public List<Document> findAll() {
        return documentJpaRepository.findAll();
    }
}
