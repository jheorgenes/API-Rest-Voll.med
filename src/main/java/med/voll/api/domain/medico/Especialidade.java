package med.voll.api.domain.medico;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Especialidade {

    ORTOPEDIA,
    CARDIOLOGIA,
    GINECOLOGIA,
    DERMATOLOGIA;

    @JsonCreator
    public static Especialidade fromString(String value) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidade nao pode ser vazia.");
        }

        for(Especialidade especialidade : Especialidade.values()) {
            if(especialidade.name().equalsIgnoreCase(value)) {
                return especialidade;
            }
        }
        throw new IllegalArgumentException("Especialidade inválida: " + value);
    }
}
