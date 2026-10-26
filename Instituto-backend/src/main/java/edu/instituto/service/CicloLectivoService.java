package edu.instituto.service;
import edu.instituto.model.CicloLectivo;
import edu.instituto.repository.CicloLectivoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service

public class CicloLectivoService {

    private final CicloLectivoRepository cicloRepository;

    public CicloLectivoService(CicloLectivoRepository cicloRepository) {
        this.cicloRepository = cicloRepository;
    }

    // Se listan todos (activos y cerrados) porque son parte del historial
    public List<CicloLectivo> listar() {
        return cicloRepository.findAll();
    }

    public CicloLectivo buscarPorId(Integer id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException( //exception que se lanza cuando no se encuentra el ciclo lectivo
                        HttpStatus.NOT_FOUND, "Ciclo lectivo no encontrado"));
    }

    public CicloLectivo crear(CicloLectivo ciclo) {
        validarFechas(ciclo);
        if (cicloRepository.existsByAnio(ciclo.getAnio())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un ciclo lectivo para ese año");
        }
        ciclo.setId(null);
        ciclo.setEstado("ACTIVO");
        return cicloRepository.save(ciclo);
    }

    public CicloLectivo actualizar(Integer id, CicloLectivo datos) {
        CicloLectivo ciclo = buscarPorId(id);

        validarFechas(datos);
        if (cicloRepository.existsByAnioAndIdNot(datos.getAnio(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro ciclo lectivo para ese año");
        }

        ciclo.setAnio(datos.getAnio());
        ciclo.setFechaInicio(datos.getFechaInicio());
        ciclo.setFechaFin(datos.getFechaFin());
        return cicloRepository.save(ciclo);
    }

    // Un ciclo no se borra: al terminar el año se cierra
    public CicloLectivo cerrar(Integer id) {
        CicloLectivo ciclo = buscarPorId(id);
        ciclo.setEstado("CERRADO");
        return cicloRepository.save(ciclo);
    }

    // La fecha de fin tiene que ser posterior a la de inicio
    private void validarFechas(CicloLectivo ciclo) {
        if (ciclo.getFechaInicio() == null || ciclo.getFechaFin() == null ||
                !ciclo.getFechaFin().isAfter(ciclo.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }
}