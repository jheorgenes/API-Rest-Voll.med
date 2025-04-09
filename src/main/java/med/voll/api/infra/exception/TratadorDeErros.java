package med.voll.api.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
