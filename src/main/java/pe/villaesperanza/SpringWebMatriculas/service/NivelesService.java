package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.NivelesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TNivelesEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.NivelesRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NivelesService {

    private final NivelesRepository nivelesRepository;

    private void validarUnicidad(String descripcion, String identifier) {
        Optional<TNivelesEntity> porDescripcion = nivelesRepository.findByDescripcion(descripcion);
        if (porDescripcion.isPresent() && !porDescripcion.get().getIdentifier().equals(identifier)) {
            throw new AppException("La descripción '" + descripcion + "' ya se encuentra registrada.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public NivelesDto add(NivelesDto nivelesDto) {
        validarUnicidad(nivelesDto.getDescripcion(), null);
        TNivelesEntity entity = new TNivelesEntity(nivelesDto);
        TNivelesEntity result = nivelesRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public NivelesDto update(String identifier, NivelesDto nivelesDto) {
        validarUnicidad(nivelesDto.getDescripcion(), identifier);
        TNivelesEntity entity = nivelesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este nivel no existe"));

        entity.update(nivelesDto);
        nivelesRepository.save(entity);
        return entity.toDto();
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TNivelesEntity entity = nivelesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El nivel que intenta eliminar no existe."));

        // VALIDACIÓN: No eliminar si tiene grados asociados.
        if (!entity.getGrados().isEmpty()) {
            throw new AppException("No se puede eliminar el nivel porque tiene grados asociados.");
        }

        nivelesRepository.delete(entity);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<NivelesDto> get(String identifier) {
        return nivelesRepository.findByIdentifier(identifier).map(TNivelesEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<NivelesDto> getSearch(int page, int size, String descripcion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado == null) ? null : estado.getValue();
        Page<TNivelesEntity> pageList = nivelesRepository.searchNiveles(descripcion, estadoValue, fechaDesde, fechaHasta, pageable);
        return pageList.map(TNivelesEntity::toDto);
    }
}
