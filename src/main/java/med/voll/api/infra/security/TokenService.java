package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service //Pro spring carregar essa classe
public class TokenService {

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("12345678"); //Chave de segurança
            return JWT.create()
                    .withIssuer("API Voll.med") //Pra identificar o emissor do token (no caso nossa própria API)
                    .withSubject(usuario.getLogin()) //Pra identificar o subject (no caso o login do usuário)
                    .withClaim("id", usuario.getId()) //Pra identificar o id do usuário
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    /**
     * Convertendo uma data de expiração para o tipo Instant para ser usada como data de expiração do token
     */
    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
