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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public EstudiantesDto add(EstudiantesDto nivelesDto) {

        TEstudiantesEntity entity = new TEstudiantesEntity(nivelesDto);
        TEstudiantesEntity result = estudiantesRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public EstudiantesDto update(String identifier, EstudiantesDto nivelesDto) {

        TEstudiantesEntity entity = estudiantesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este estudiante no existe"));

        entity.update(nivelesDto);
        TEstudiantesEntity result = estudiantesRepository.save(entity);
        estudiantesRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<EstudiantesDto> get(String identifier) {

        TEstudiantesEntity result = estudiantesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este estudiante no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<EstudiantesDto> getSearch(int page, int size, String descripcion, GeneroReference genero, 
                EstadoAcademicoReference estadoA, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TEstudiantesEntity> pageList = estudiantesRepository.searchEstudiantes(descripcion,
                genero == null ? null : genero.getValue(),
                estadoA == null ? null : estadoA.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TEstudiantesEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TEstudiantesEntity entity = estudiantesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este estudiante no existe para eliminar"));
        estudiantesRepository.delete(entity);
    }
}
