package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.BancosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TBancosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.BancosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BancosService {

    private final BancosRepository bancosRepository;

    private void validarUnicidad(String codigo, String descripcion, String identifier) {
        Optional<TBancosEntity> porCodigo = bancosRepository.findByCodigo(codigo);
        if (porCodigo.isPresent() && !porCodigo.get().getIdentifier().equals(identifier)) {
            throw new AppException("El código '" + codigo + "' ya está en uso.");
        }

        Optional<TBancosEntity> porDescripcion = bancosRepository.findByDescripcion(descripcion);
        if (porDescripcion.isPresent() && !porDescripcion.get().getIdentifier().equals(identifier)) {
            throw new AppException("La descripción '" + descripcion + "' ya está en uso.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public BancosDto add(BancosDto bancosDto) {
        validarUnicidad(bancosDto.getCodigo(), bancosDto.getDescripcion(), null);
        TBancosEntity entity = new TBancosEntity(bancosDto);
        TBancosEntity result = bancosRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public BancosDto update(String identifier, BancosDto bancosDto) {
        validarUnicidad(bancosDto.getCodigo(), bancosDto.getDescripcion(), identifier);
        TBancosEntity entity = bancosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este banco no existe"));
        entity.update(bancosDto);
        bancosRepository.save(entity);
        return entity.toDto();
    }


    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<BancosDto> get(String identifier) {
        return bancosRepository.findByIdentifier(identifier).map(TBancosEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<BancosDto> getSearch(int page, int size, String descripcion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TBancosEntity> pageList =  bancosRepository.searchBancos(descripcion,
                estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TBancosEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TBancosEntity entity = bancosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este banco no existe para eliminar"));
        bancosRepository.delete(entity);
    }
}
