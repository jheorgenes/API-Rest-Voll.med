package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAllByAtivoTrue(Pageable paginacao);

    // JPQL não identifica Booleanos com Integer, então lembre-se sempre de trocar 0 e 1 por false e true
    @Query("""
            SELECT m FROM Medico m
                WHERE m.ativo = true
                AND m.especialidade = :especialidade
                AND m.id NOT IN (
                    SELECT c.medico.id FROM Consulta c
                        WHERE c.data = :data
                        AND c.motivoCancelamento IS NULL
                )
                ORDER BY rand()
                LIMIT 1
          """)
    Medico escolherMedicoAleatorioLivreNaData(Especialidade especialidade, LocalDateTime data);

    @Query("""
            SELECT m.ativo FROM Medico m
                WHERE m.id = :id
          """)
    Boolean findAtivoById(Long id);
}
