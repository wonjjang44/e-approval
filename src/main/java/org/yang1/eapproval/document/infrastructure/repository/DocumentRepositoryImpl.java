package org.yang1.eapproval.document.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.document.domain.entity.Document;
import org.yang1.eapproval.document.domain.entity.QApprovalLine;
import org.yang1.eapproval.document.domain.entity.QApprovalStep;
import org.yang1.eapproval.document.domain.entity.QDocument;
import org.yang1.eapproval.document.domain.repository.DocumentRepository;
import org.yang1.eapproval.document.infrastructure.repository.jpa.DocumentJpaRepository;
import org.yang1.eapproval.user.domain.entity.QUser;

import java.util.List;
import java.util.Optional;

import static org.yang1.eapproval.document.domain.entity.QApprovalLine.approvalLine;
import static org.yang1.eapproval.document.domain.entity.QApprovalStep.approvalStep;
import static org.yang1.eapproval.document.domain.entity.QDocument.*;
import static org.yang1.eapproval.document.domain.entity.QDocument.document;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository documentJpaRepository;
    private final JPAQueryFactory queryFactory;


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


    /**
     * 문서 상세 조회
     *
     * fetch join 적용
     *
     * @param id pk
     * @return
     */
    @Override
    public Optional<Document> findDetailById(Long id) {
        QDocument doc = document;
        QApprovalLine line = approvalLine;
        QApprovalStep step = approvalStep;

        QUser drafter = new QUser("drafter");
        QUser approver = new QUser("approver");

        Document result = queryFactory
                .selectFrom(doc)
                .join(doc.drafter, drafter).fetchJoin()
                .leftJoin(doc.approvalLine, line).fetchJoin()
                .leftJoin(line.approvalSteps, step).fetchJoin()
                .leftJoin(step.approver, approver).fetchJoin()
                .where(doc.id.eq(id))
                .orderBy(step.stepOrder.asc())
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
