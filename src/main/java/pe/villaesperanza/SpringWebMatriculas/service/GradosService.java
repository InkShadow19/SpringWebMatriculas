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
import pe.villaesperanza.SpringWebMatriculas.entity.TGradosEntity;
import pe.villaesperanza.SpringWebMatriculas.entity.TNivelesEntity;
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

    private void validarUnicidad(String descripcion, String nivelIdentifier, String gradoIdentifier) {
        Optional<TGradosEntity> porDescripcionYNivel = gradosRepository.findByDescripcionAndNivelesEntityIdentifier(descripcion, nivelIdentifier);
        if (porDescripcionYNivel.isPresent() && !porDescripcionYNivel.get().getIdentifier().equals(gradoIdentifier)) {
            throw new AppException("La descripción '" + descripcion + "' ya existe para este nivel.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public GradosDto add(GradosDto gradosDto) {
        validarUnicidad(gradosDto.getDescripcion(), gradosDto.getNivel(), null);
        
        TNivelesEntity nivel = nivelesRepository.findByIdentifier(gradosDto.getNivel())
                .orElseThrow(() -> new AppException("El nivel especificado no existe."));
        
        TGradosEntity entity = new TGradosEntity(gradosDto, nivel);
        TGradosEntity result = gradosRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public GradosDto update(String identifier, GradosDto gradosDto) {
        TGradosEntity entity = gradosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identificador de este grado no existe."));

        validarUnicidad(gradosDto.getDescripcion(), entity.getNivelesEntity().getIdentifier(), identifier);
        
        entity.update(gradosDto);
        TGradosEntity result = gradosRepository.save(entity);
        return result.toDto();
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TGradosEntity entity = gradosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El grado que intenta eliminar no existe."));

        // VALIDACIÓN: No eliminar si tiene matrículas asociadas.
        if (!entity.getMatriculas().isEmpty()) {
            throw new AppException("No se puede eliminar el grado porque tiene matrículas asociadas. Por favor, inactivelo en su lugar.");
        }
        
        gradosRepository.delete(entity);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<GradosDto> get(String identifier) {
        return gradosRepository.findByIdentifier(identifier).map(TGradosEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<GradosDto> getSearch(int page, int size, String descripcion, EstadoReference estado, String nivelIdentifier, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Long idNivel = null;
        if (nivelIdentifier != null && !nivelIdentifier.isEmpty()) {
            idNivel = nivelesRepository.findByIdentifier(nivelIdentifier)
                    .orElseThrow(() -> new AppException("El nivel para el filtro no existe")).getId();
        }
        Integer estadoValue = (estado != null) ? estado.getValue() : null;
        Page<TGradosEntity> pageList = gradosRepository.searchGrados(descripcion, estadoValue, idNivel, fechaDesde, fechaHasta, pageable);
        return pageList.map(TGradosEntity::toDto);
    }
}