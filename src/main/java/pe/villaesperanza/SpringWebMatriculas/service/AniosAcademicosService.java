package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.AniosAcademicosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TAniosAcademicosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.AniosAcademicosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AniosAcademicosService {

    private final AniosAcademicosRepository aniosAcademicosRepository;

    private void validarUnicidad(Integer anio, String identifier) {
        Optional<TAniosAcademicosEntity> porAnio = aniosAcademicosRepository.findByAnio(anio);
        if (porAnio.isPresent() && !porAnio.get().getIdentifier().equals(identifier)) {
            throw new AppException("El año académico " + anio + " ya se encuentra registrado.");
        }
    }

    private void validarEstadoActivo(String identifier) {
        Optional<TAniosAcademicosEntity> anioActivo = aniosAcademicosRepository.findByEstadoAcademico(EstadoAcademicoReference.ACTIVO.getValue());
        if (anioActivo.isPresent() && !anioActivo.get().getIdentifier().equals(identifier)) {
            throw new AppException("Ya existe un año académico activo. Solo puede haber uno a la vez.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public AniosAcademicosDto add(AniosAcademicosDto aniosAcademicosDto) {
        validarUnicidad(aniosAcademicosDto.getAnio(), null);
        if (aniosAcademicosDto.getEstadoAcademico() == EstadoAcademicoReference.ACTIVO) {
            validarEstadoActivo(null);
        }
        
        TAniosAcademicosEntity entity = new TAniosAcademicosEntity(aniosAcademicosDto);
        TAniosAcademicosEntity result = aniosAcademicosRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public AniosAcademicosDto update(String identifier, AniosAcademicosDto aniosAcademicosDto) {
        validarUnicidad(aniosAcademicosDto.getAnio(), identifier);
        if (aniosAcademicosDto.getEstadoAcademico() == EstadoAcademicoReference.ACTIVO) {
            validarEstadoActivo(identifier);
        }

        TAniosAcademicosEntity entity = aniosAcademicosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este año no existe"));

        entity.update(aniosAcademicosDto);
        aniosAcademicosRepository.save(entity);
        return entity.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<AniosAcademicosDto> get(String identifier) {
        return aniosAcademicosRepository.findByIdentifier(identifier).map(TAniosAcademicosEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<AniosAcademicosDto> getSearch(int page, int size, Integer anio, EstadoAcademicoReference estadoA, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estadoA != null) ? estadoA.getValue() : null;
        Page<TAniosAcademicosEntity> pageList = aniosAcademicosRepository.searchAcademicos(anio, estadoValue, fechaDesde, fechaHasta, pageable);
        return pageList.map(TAniosAcademicosEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TAniosAcademicosEntity entity = aniosAcademicosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El año académico a eliminar no existe."));

        // VALIDACIÓN: No eliminar si tiene matrículas asociadas.
        if (!entity.getMatriculas().isEmpty()) {
            throw new AppException("No se puede eliminar un año académico con matrículas asociadas.");
        }

        aniosAcademicosRepository.delete(entity);
    }
}
