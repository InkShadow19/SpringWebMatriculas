package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.ConceptosPagoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TConceptosPagoEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.ConceptosPagoRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptosPagoService {

    private final ConceptosPagoRepository conceptosPagoRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ConceptosPagoDto add(ConceptosPagoDto bancosDto) {

        TConceptosPagoEntity entity = new TConceptosPagoEntity(bancosDto);
        TConceptosPagoEntity result = conceptosPagoRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ConceptosPagoDto update(String identifier, ConceptosPagoDto bancosDto) {

        TConceptosPagoEntity entity = conceptosPagoRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este concepto no existe"));

        entity.update(bancosDto);
        TConceptosPagoEntity result = conceptosPagoRepository.save(entity);
        conceptosPagoRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<ConceptosPagoDto> get(String identifier) {

        TConceptosPagoEntity result = conceptosPagoRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este concepto no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<ConceptosPagoDto> getSearch(int page, int size, String codigo, String description, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TConceptosPagoEntity> pageList =  conceptosPagoRepository.searchConceptos(codigo, description,
                estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TConceptosPagoEntity::toDto);
    }
}
