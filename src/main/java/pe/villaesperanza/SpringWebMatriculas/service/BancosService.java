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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public BancosDto add(BancosDto bancosDto) {

        TBancosEntity entity = new TBancosEntity(bancosDto);
        TBancosEntity result = bancosRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public BancosDto update(String identifier, BancosDto bancosDto) {

        TBancosEntity entity = bancosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este banco no existe"));

        entity.update(bancosDto);
        TBancosEntity result = bancosRepository.save(entity);
        bancosRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<BancosDto> get(String identifier) {

        TBancosEntity result = bancosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este banco no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<BancosDto> getSearch(int page, int size, String codigo, String description, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TBancosEntity> pageList =  bancosRepository.searchBancos(codigo, description,
                estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TBancosEntity::toDto);
    }
}
