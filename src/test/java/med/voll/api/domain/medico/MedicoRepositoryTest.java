package med.voll.api.domain.medico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //É utilizada para testar uma interface Repository
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //Determinando que o meu bd será o mesmo utilizado e não um banco in-memory
@ActiveProfiles("test") //Pra ler o application-test.properties
class MedicoRepositoryTest {

    @Test
    void escolherMedicoAleatorioLivreNaData() {

    }
}