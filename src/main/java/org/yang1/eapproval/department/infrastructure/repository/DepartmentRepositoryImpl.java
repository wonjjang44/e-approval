package org.yang1.eapproval.department.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yang1.eapproval.department.domain.entity.Department;
import org.yang1.eapproval.department.domain.repository.DepartmentRepository;
import org.yang1.eapproval.department.infrastructure.repository.jpa.DepartmentJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentJpaRepository departmentJpaRepository;



    @Override
    public Department save(Department department) {
        return departmentJpaRepository.save(department);
    }


    @Override
    public Optional<Department> findById(Long id) {
        return departmentJpaRepository.findById(id);
    }


    @Override
    public List<Department> findAll() {
        return departmentJpaRepository.findAll();
    }
}
