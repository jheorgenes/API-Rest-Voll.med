package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //É utilizado para que o Spring carregue uma classe/componente genérico
public class SecurityFilter extends OncePerRequestFilter { //OncePerRequestFilter chama Filter (Do Servlet) por baixo dos panos, mas sua implementação já é entendida pelo Spring

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);
        if(tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT); // Validando o token
            var usuario = repository.findByLogin(subject); // Buscando o usuário no banco de dados

            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()); // Criando o objeto de autenticação
            SecurityContextHolder.getContext().setAuthentication(authentication); // Colocando o objeto de autenticação no contexto do Spring Security para forçar a autenticação
        }
        // Prosseguindo com a requisição e resposta
        filterChain.doFilter(request, response);
    }

    // Recuperando o token do cabeçalho da requisição
    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
