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
import pe.villaesperanza.SpringWebMatriculas.util.PagedResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NivelesService {

    private final NivelesRepository nivelesRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public NivelesDto add(NivelesDto nivelesDto) {

        TNivelesEntity requirementEntity = new TNivelesEntity(nivelesDto);

        TNivelesEntity result = nivelesRepository.save(requirementEntity);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public NivelesDto get(String identifier) {

        TNivelesEntity result = nivelesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este nivel no existe"));

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PagedResponse<NivelesDto> getSearch(int page, int size, String descripcion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);

        Page<TNivelesEntity> pageList =  nivelesRepository.searchNiveles(descripcion, estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        List<NivelesDto> requirementDtos = pageList.map(TNivelesEntity::toDto).toList();

        return new PagedResponse<>(requirementDtos, pageList.getNumber(), pageList.getSize(), pageList.getTotalElements(), pageList.getTotalPages(), pageList.isLast());
    }
}
