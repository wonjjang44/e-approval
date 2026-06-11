package org.yang1.eapproval.user.exception;

public class DuplicateUserLoginIdException extends RuntimeException {
    public DuplicateUserLoginIdException(String message) {
        super(message);
    }
}
