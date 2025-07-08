package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.RolesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TRolesEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.RolesRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public RolesDto add(RolesDto nivelesDto) {

        TRolesEntity entity = new TRolesEntity(nivelesDto);
        TRolesEntity result = rolesRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public RolesDto update(String identifier, RolesDto nivelesDto) {

        TRolesEntity entity = rolesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este rol no existe"));

        entity.update(nivelesDto);
        TRolesEntity result = rolesRepository.save(entity);
        rolesRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RolesDto> get(String identifier) {

        TRolesEntity result = rolesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este rol no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<RolesDto> getSearch(int page, int size, String descripcion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TRolesEntity> pageList =  rolesRepository.searchRoles(descripcion, estado == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TRolesEntity::toDto);
    }
}
