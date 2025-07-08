package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.ApoderadosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TApoderadosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.ApoderadosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApoderadosService {

    private final ApoderadosRepository apoderadosRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ApoderadosDto add(ApoderadosDto apoderadosDto) {

        TApoderadosEntity entity = new TApoderadosEntity(apoderadosDto);
        TApoderadosEntity result = apoderadosRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ApoderadosDto update(String identifier, ApoderadosDto apoderadosDto) {

        TApoderadosEntity entity = apoderadosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este apoderado no existe"));

        entity.update(apoderadosDto);
        TApoderadosEntity result = apoderadosRepository.save(entity);
        apoderadosRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<ApoderadosDto> get(String identifier) {

        TApoderadosEntity result = apoderadosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este apoderado no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<ApoderadosDto> getSearch(int page, int size, String descripcion, GeneroReference genero, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TApoderadosEntity> pageList =  apoderadosRepository.searchApoderados(descripcion,
                genero == null ? null : genero.getValue(), estado == null ? null : estado.getValue(),
                fechaDesde, fechaHasta, pageable);

        return pageList.map(TApoderadosEntity::toDto);
    }
}
