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

    private void validarUnicidad(String codigo, String descripcion, String identifier) {
        Optional<TConceptosPagoEntity> porCodigo = conceptosPagoRepository.findByCodigo(codigo);
        if (porCodigo.isPresent() && !porCodigo.get().getIdentifier().equals(identifier)) {
            throw new AppException("El código '" + codigo + "' ya se encuentra registrado.");
        }
       
        Optional<TConceptosPagoEntity> porDescripcion = conceptosPagoRepository.findByDescripcion(descripcion);
        if (porDescripcion.isPresent() && !porDescripcion.get().getIdentifier().equals(identifier)) {
            throw new AppException("La descripción '" + descripcion + "' ya está en uso.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ConceptosPagoDto add(ConceptosPagoDto conceptosPagoDto) {
        validarUnicidad(conceptosPagoDto.getCodigo(), conceptosPagoDto.getDescripcion(), null);
        TConceptosPagoEntity entity = new TConceptosPagoEntity(conceptosPagoDto);
        TConceptosPagoEntity result = conceptosPagoRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public ConceptosPagoDto update(String identifier, ConceptosPagoDto conceptosPagoDto) {
        validarUnicidad(conceptosPagoDto.getCodigo(), conceptosPagoDto.getDescripcion(), identifier);
        TConceptosPagoEntity entity = conceptosPagoRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este concepto no existe"));

        entity.update(conceptosPagoDto);
        conceptosPagoRepository.save(entity);
        return entity.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<ConceptosPagoDto> get(String identifier) {
        return conceptosPagoRepository.findByIdentifier(identifier).map(TConceptosPagoEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<ConceptosPagoDto> getSearch(int page, int size, String codigo, String description, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado != null) ? estado.getValue() : null;
        Page<TConceptosPagoEntity> pageList = conceptosPagoRepository.searchConceptos(codigo, description, estadoValue, fechaDesde, fechaHasta, pageable);
        return pageList.map(TConceptosPagoEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TConceptosPagoEntity entity = conceptosPagoRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este concepto no existe para eliminar"));
        conceptosPagoRepository.delete(entity);
    }
}
