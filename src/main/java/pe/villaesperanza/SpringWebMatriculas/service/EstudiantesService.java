package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.EstudiantesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TEstudiantesEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.EstudiantesRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstudiantesService {

    private final EstudiantesRepository estudiantesRepository;

    private void validarUnicidad(String dni, String email, String identifier) {
        // Validar DNI
        Optional<TEstudiantesEntity> estudiantePorDni = estudiantesRepository.findByDni(dni);
        if (estudiantePorDni.isPresent() && !estudiantePorDni.get().getIdentifier().equals(identifier)) {
            throw new AppException("El DNI '" + dni + "' ya se encuentra registrado.");
        }

        // Validar Email
        Optional<TEstudiantesEntity> estudiantePorEmail = estudiantesRepository.findByEmail(email);
        if (estudiantePorEmail.isPresent() && !estudiantePorEmail.get().getIdentifier().equals(identifier)) {
            throw new AppException("El correo electrónico '" + email + "' ya se encuentra registrado.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, IOException.class})
    public EstudiantesDto add(EstudiantesDto estudiantesDto) {
        // Para un nuevo estudiante, el identifier a comparar es null.
        validarUnicidad(estudiantesDto.getDni(), estudiantesDto.getEmail(), null);
        
        TEstudiantesEntity entity = new TEstudiantesEntity(estudiantesDto);
        TEstudiantesEntity result = estudiantesRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, IOException.class})
    public EstudiantesDto update(String identifier, EstudiantesDto estudiantesDto) {
        // Para actualizar, pasamos el identifier del estudiante que estamos editando.
        validarUnicidad(estudiantesDto.getDni(), estudiantesDto.getEmail(), identifier);

        TEstudiantesEntity entity = estudiantesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este estudiante no existe"));

        entity.update(estudiantesDto);
        TEstudiantesEntity result = estudiantesRepository.save(entity);
        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<EstudiantesDto> get(String identifier) {
        return estudiantesRepository.findByIdentifier(identifier).map(TEstudiantesEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<EstudiantesDto> getSearch(int page, int size, String descripcion, GeneroReference genero,
                                          EstadoAcademicoReference estadoA, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Integer generoValue = (genero != null) ? genero.getValue() : null;
        Integer estadoAValue = (estadoA != null) ? estadoA.getValue() : null;
        
        Page<TEstudiantesEntity> pageList = estudiantesRepository.searchEstudiantes(descripcion, generoValue, estadoAValue, fechaDesde, fechaHasta, pageable);
        return pageList.map(TEstudiantesEntity::toDto);
    }

    // --- NUEVO MÉTODO PARA BUSCAR SOLO ESTUDIANTES ACTIVOS ---
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<EstudiantesDto> getSearchActivos(int page, int size, String descripcion) {
        Pageable pageable = PageRequest.of(page, size);
        // Forzamos la búsqueda para que solo incluya el estado ACTIVO
        Integer estadoActivo = EstadoAcademicoReference.ACTIVO.getValue();
        
        Page<TEstudiantesEntity> pageList = estudiantesRepository.searchEstudiantes(
            descripcion, null, estadoActivo, null, null, pageable);
            
        return pageList.map(TEstudiantesEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, IOException.class})
    public void delete(String identifier) {
        TEstudiantesEntity entity = estudiantesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este estudiante no existe para eliminar"));

        // --- VALIDACIÓN AÑADIDA ---
        // Se comprueba si la lista de matrículas asociadas no está vacía.
        if (!entity.getMatriculas().isEmpty()) {
            throw new AppException("No se puede eliminar un alumno con matrículas asociadas.");
        }

        estudiantesRepository.delete(entity);
    }
}
