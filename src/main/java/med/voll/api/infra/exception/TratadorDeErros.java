package med.voll.api.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import med.voll.api.domain.ValidacaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 *  @RestControllerAdvice: define um controller gerenciado pelo próprio spring
 */
@RestControllerAdvice
public class TratadorDeErros {

    /**
     * @exceptionHandler(): Captura Exceptions lançadas
     * EntityNotFoundException: Tipo de Exception lançada. Ocorre quando uma entidade não é encontrada no banco de dados
     * @return status code 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    /**
     * MethodArgumentNotValidException: Lançada quando encontrar erros de validação
     * @return status code 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();

        // Gerando uma resposta com os erros de validação
        return ResponseEntity
                    .badRequest()
                    .body(erros.stream().map(DadosErroValidacao::new)
                    .toList());
    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity tratarErro400(HttpMessageNotReadableException ex) {
//        return ResponseEntity.badRequest().body(ex.getMessage());
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity tratarErroAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity tratarErro500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " +ex.getLocalizedMessage());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity tratarErroRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Tratador de erro que o CHATGPT gerou
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> tratarErroViolacaoDeIntegridade(DataIntegrityViolationException ex) {
        String mensagem = "Dados inválidos: " + extrairMensagemConstraint(ex);

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "erro", "Violação de integridade",
                        "mensagem", mensagem
                ));
    }

    /**
     * Tratador de erro que o CHATGPT gerou para capturar erros de validação de enums
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity tratarErro400(HttpMessageNotReadableException ex) {
        var mensagem = ex.getMostSpecificCause().getMessage(); // ou ex.getCause().getMessage()
        return ResponseEntity
                .badRequest()
                .body(Map.of("erro", mensagem));
    }

    /**
     * Record gerado para formatar os dados de FieldError enxugando apenas os dados necessários
     * @return {campo, mensagem}
     */
    private record DadosErroValidacao(String campo, String mensagem){

        // Parâmetro FieldError é uma classe de validação do spring, contendo a estrutura dos dados retornados quando ocorre um erro de validação
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage()); //Recuperando apenas o campo Field e o campo DefaultMessage
        }
    }

    private String extrairMensagemConstraint(DataIntegrityViolationException ex) {
        Throwable causa = ex.getMostSpecificCause();
        return causa != null ? causa.getMessage() : "Violação de restrição de dados";
    }
}
