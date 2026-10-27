package edu.instituto.service;
import edu.instituto.model.Materia;
import edu.instituto.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public List<Materia> listar() {
        return materiaRepository.findAll();
    }

    public Materia buscarPorId(Integer id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Materia no encontrada"));
    }

    // El nombre de una materia no se puede repetir
    public Materia crear(Materia materia) {
        if (materiaRepository.existsByNombre(materia.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una materia con ese nombre");
        }
        materia.setId(null);
        return materiaRepository.save(materia);
    }

    public Materia actualizar(Integer id, Materia datos) {
        Materia materia = buscarPorId(id);

        if (materiaRepository.existsByNombreAndIdNot(datos.getNombre(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra materia con ese nombre");
        }

        materia.setNombre(datos.getNombre());
        materia.setDescripcion(datos.getDescripcion());
        return materiaRepository.save(materia);
    }
}