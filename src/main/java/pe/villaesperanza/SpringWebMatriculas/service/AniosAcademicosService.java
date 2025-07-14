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
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public AniosAcademicosDto add(AniosAcademicosDto aniosAcademicosDto) {

        TAniosAcademicosEntity entity = new TAniosAcademicosEntity(aniosAcademicosDto);
        TAniosAcademicosEntity result = aniosAcademicosRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public AniosAcademicosDto update(String identifier, AniosAcademicosDto aniosAcademicosDto) {

        TAniosAcademicosEntity entity = aniosAcademicosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este año no existe"));

        entity.update(aniosAcademicosDto);
        TAniosAcademicosEntity result = aniosAcademicosRepository.save(entity);
        aniosAcademicosRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<AniosAcademicosDto> get(String identifier) {

        TAniosAcademicosEntity result = aniosAcademicosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este año no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<AniosAcademicosDto> getSearch(int page, int size, Integer anio, EstadoReference estado, EstadoAcademicoReference estadoA, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TAniosAcademicosEntity> pageList =  aniosAcademicosRepository.searchAcademicos(anio,
                estado == null ? null : estado.getValue(), estadoA == null ? null : estadoA.getValue(),
                fechaDesde, fechaHasta, pageable);

        return pageList.map(TAniosAcademicosEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TAniosAcademicosEntity entity = aniosAcademicosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este año académico no existe para eliminar"));
        aniosAcademicosRepository.delete(entity);
    }
}
