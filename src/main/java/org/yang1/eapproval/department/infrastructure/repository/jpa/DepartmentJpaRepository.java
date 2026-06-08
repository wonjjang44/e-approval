package org.yang1.eapproval.department.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yang1.eapproval.department.domain.entity.Department;

public interface DepartmentJpaRepository extends JpaRepository<Department, Long> {
}
