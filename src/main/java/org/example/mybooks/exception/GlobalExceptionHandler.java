package org.example.mybooks.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        // 백엔드에서 throw new RuntimeException("메시지") 한 내용이 e.getMessage()로 들어옵니다.
        // status(400)이나 (500) 중 적절한 것을 선택하세요.
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AdminConstraintException.class)
    public ResponseEntity<String> handleAdminException(AdminConstraintException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}