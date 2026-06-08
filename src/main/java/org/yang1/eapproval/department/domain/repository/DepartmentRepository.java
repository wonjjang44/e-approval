package org.yang1.eapproval.department.domain.repository;

import org.yang1.eapproval.department.domain.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(Long id);

    List<Department> findAll();
}
