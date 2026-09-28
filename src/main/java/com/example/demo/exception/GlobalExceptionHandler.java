package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        log.warn("Entidade não encontrada: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/recurso-nao-encontrado"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ProblemDetail handleVotoDuplicado(VotoDuplicadoException ex) {
        log.warn("Tentativa de voto duplicado: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Voto duplicado");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/voto-duplicado"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade no banco de dados: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Conflito de dados ou voto duplicado detectado para este associado."
        );
        problemDetail.setTitle("Conflito de integridade");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/conflito-integridade"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(SessaoNaoAbertaException.class)
    public ProblemDetail handleSessaoNaoAberta(SessaoNaoAbertaException ex) {
        log.warn("Sessão não disponível para voto: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Sessão de votação indisponível");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/sessao-indisponivel"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(AssociadoInaptoException.class)
    public ProblemDetail handleAssociadoInapto(AssociadoInaptoException ex) {
        log.warn("Associado inapto para votar: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Associado inapto para votação");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/associado-inapto"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ProblemDetail handleCpfInvalido(CpfInvalidoException ex) {
        log.warn("CPF inválido: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("CPF inválido");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/cpf-invalido"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail handleRegraDeNegocio(RegraDeNegocioException ex) {
        log.warn("Violação de regra de negócio: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Regra de negócio violada");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/regra-de-negocio"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Dados de entrada inválidos na requisição: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos estão inválidos.");
        problemDetail.setTitle("Dados de requisição inválidos");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/validacao-campos"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        Map<String, String> camposInvalidos = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            camposInvalidos.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("erros", camposInvalidos);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Falha na desserialização do corpo da requisição: {}", ex.getMessage());
        String mensagem = "Corpo da requisição ausente ou malformatado. Verifique os tipos de dados enviados.";
        if (ex.getMessage() != null && ex.getMessage().contains("OpcaoVoto")) {
            mensagem = "Opção de voto inválida. Valores aceitos: SIM ou NAO.";
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensagem);
        problemDetail.setTitle("Corpo da requisição inválido");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/requisicao-invalida"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Argumento inválido");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/argumento-invalido"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleExceptionGenerica(Exception ex) {
        log.error("Erro interno inesperado no servidor: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado no processamento da solicitação."
        );
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setType(URI.create("https://api.exemplo.com/erros/erro-interno"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}
