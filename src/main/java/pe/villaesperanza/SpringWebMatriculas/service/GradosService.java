package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.GradosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.GradosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.NivelesRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradosService {

    private final GradosRepository gradosRepository;
    private final NivelesRepository nivelesRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public GradosDto add(GradosDto gradosDto) {

        TNivelesEntity nivel = null;

        try {
            nivel = nivelesRepository.findByIdentifier(gradosDto.getNivel()).orElse(null);
        } catch (Exception e) {
            log.error("Error inesperado al validar los identificadores: " + e.getMessage());
        }

        TGradosEntity entity = new TGradosEntity(gradosDto, nivel);
        TGradosEntity result = gradosRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public GradosDto update(String identifier, GradosDto gradosDto) {

        TGradosEntity entity = gradosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este grado no existe"));

        entity.update(gradosDto);
        TGradosEntity result = gradosRepository.save(entity);
        gradosRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<GradosDto> get(String identifier) {

        TGradosEntity result = gradosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este grado no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<GradosDto> getSearch(int page, int size, String description, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TGradosEntity> pageList =  gradosRepository.searchGrados(description,
                estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TGradosEntity::toDto);
    }
}
