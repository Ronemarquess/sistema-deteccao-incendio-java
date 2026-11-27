package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.dto.ErroResponse;
import br.com.estacio.incendio.exception.AutenticacaoException;
import br.com.estacio.incendio.exception.RegraNegocioException;
import br.com.estacio.incendio.exception.RecursoNaoEncontradoException;
import br.com.estacio.incendio.exception.ValidacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, "Erro de validação", "Campos inválidos", campos);
    }

    @ExceptionHandler({ValidacaoException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponse> requisicaoInvalida(Exception ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.CONFLICT, "Conflito de regra de negócio", ex.getMessage(), null);
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroResponse> autenticacao(AutenticacaoException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "Não autorizado", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception ex) {
        LOG.error("Erro não tratado na API", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente.", null);
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String erro,
                                                   String mensagem, Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ErroResponse(
                Instant.now(), status.value(), erro, mensagem, campos));
    }
}
