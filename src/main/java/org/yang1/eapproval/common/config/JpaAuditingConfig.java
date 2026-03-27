package org.yang1.eapproval.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * EnableJpaAuditing 활성화
 * EapprovalApplication 클래스에 EnableJpaAuditing 어노테이션 추가해도 되지만 별도 설정 파일로 뺌
 * 관리 목적으로..
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
