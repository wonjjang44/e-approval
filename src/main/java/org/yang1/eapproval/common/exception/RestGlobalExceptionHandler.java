package org.yang1.eapproval.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yang1.eapproval.common.response.ApiResult;
import org.yang1.eapproval.department.exception.DepartmentNotFoundException;
import org.yang1.eapproval.department.exception.DuplicateDepartmentNameException;
import org.yang1.eapproval.document.exception.DocumentNotFoundException;
import org.yang1.eapproval.user.exception.DuplicateUserLoginIdException;
import org.yang1.eapproval.user.exception.UserNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestGlobalExceptionHandler {


    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleDepartmentNotFoundException(DepartmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.failure(ex.getMessage()));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.failure(ex.getMessage()));
    }


    @ExceptionHandler(DuplicateDepartmentNameException.class)
    public ResponseEntity<ApiResult<String>> handleDuplicateDepartmentNameException(DuplicateDepartmentNameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.failure(ex.getMessage()));
    }


    @ExceptionHandler(DuplicateUserLoginIdException.class)
    public ResponseEntity<ApiResult<String>> handleDuplicateUserLoginIdException(DuplicateUserLoginIdException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.failure(ex.getMessage()));
    }


    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.failure(ex.getMessage()));
    }




    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.failure(ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.failure(message));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<String>> handleException(Exception ex) {
        log.error("예상하지 못한 오류 => ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failure("서버에 오류가 발생했습니다."));
    }

}
