package org.yang1.eapproval.department.exception;

public class ParentDepartmentNotFoundException extends RuntimeException {
    public ParentDepartmentNotFoundException(String message) {
        super(message);
    }
}
