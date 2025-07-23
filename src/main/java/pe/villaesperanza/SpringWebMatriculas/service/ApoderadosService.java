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

    private void validarUnicidad(String dni, String email, String identifier) {
        Optional<TApoderadosEntity> porDni = apoderadosRepository.findByDni(dni);
        if (porDni.isPresent() && !porDni.get().getIdentifier().equals(identifier)) {
            throw new AppException("El DNI '" + dni + "' ya se encuentra registrado.");
        }

        Optional<TApoderadosEntity> porEmail = apoderadosRepository.findByEmail(email);
        if (porEmail.isPresent() && !porEmail.get().getIdentifier().equals(identifier)) {
            throw new AppException("El correo electrónico '" + email + "' ya se encuentra registrado.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ApoderadosDto add(ApoderadosDto apoderadosDto) {
        validarUnicidad(apoderadosDto.getDni(), apoderadosDto.getEmail(), null);
        TApoderadosEntity entity = new TApoderadosEntity(apoderadosDto);
        TApoderadosEntity result = apoderadosRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ApoderadosDto update(String identifier, ApoderadosDto apoderadosDto) {
        validarUnicidad(apoderadosDto.getDni(), apoderadosDto.getEmail(), identifier);
        TApoderadosEntity entity = apoderadosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este apoderado no existe"));

        entity.update(apoderadosDto);
        apoderadosRepository.save(entity);
        return entity.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<ApoderadosDto> get(String identifier) {
        return apoderadosRepository.findByIdentifier(identifier).map(TApoderadosEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<ApoderadosDto> getSearch(int page, int size, String descripcion, GeneroReference genero,
            EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Integer generoValue = (genero != null) ? genero.getValue() : null;
        Integer estadoValue = (estado != null) ? estado.getValue() : null;

        Page<TApoderadosEntity> pageList = apoderadosRepository.searchApoderados(descripcion,
                generoValue, estadoValue, fechaDesde, fechaHasta, pageable);

        return pageList.map(TApoderadosEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TApoderadosEntity entity = apoderadosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este apoderado no existe para eliminar"));
        apoderadosRepository.delete(entity);
    }
}
