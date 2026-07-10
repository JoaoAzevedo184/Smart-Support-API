package io.github.joaovictor.smartsupport.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Tratamento centralizado de exceções: converte erros em respostas
 * {@link ProblemDetail} (RFC 7807) padronizadas, com um {@code timestamp}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== Erros de negócio (status + motivo explícitos) =====
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // ===== Erros de validação de DTO (@Valid) — inclui mapa de campos =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos");
        problemDetail.setTitle("Erro de validação");
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problemDetail.setProperty("errors", fieldErrors);

        return problemDetail;
    }

    // ===== Rede de segurança (qualquer erro inesperado → 500) =====
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado");
        problemDetail.setTitle("Erro interno");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
