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

    private void validarUnicidad(String descripcion, String identifier) {
        Optional<TRolesEntity> porDescripcion = rolesRepository.findByDescripcion(descripcion);
        if (porDescripcion.isPresent() && !porDescripcion.get().getIdentifier().equals(identifier)) {
            throw new AppException("La descripción '" + descripcion + "' ya está en uso por otro rol.");
        }
    }

    private void protegerRolAdministrador(TRolesEntity rol) {
        if ("Administrador".equalsIgnoreCase(rol.getDescripcion())) {
            throw new AppException("El rol 'Administrador' no puede ser modificado o eliminado.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public RolesDto add(RolesDto rolesDto) {
        validarUnicidad(rolesDto.getDescripcion(), null);
        TRolesEntity entity = new TRolesEntity(rolesDto);
        TRolesEntity result = rolesRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public RolesDto update(String identifier, RolesDto rolesDto) {
        TRolesEntity entity = rolesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El rol que intenta actualizar no existe."));

        protegerRolAdministrador(entity);
        validarUnicidad(rolesDto.getDescripcion(), identifier);

        entity.update(rolesDto);
        TRolesEntity result = rolesRepository.save(entity);
        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TRolesEntity entity = rolesRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El rol que intenta eliminar no existe."));

        protegerRolAdministrador(entity);

        // VALIDACIÓN: No eliminar si tiene usuarios asociados.
        if (!entity.getUsuarios().isEmpty()) {
            throw new AppException("No se puede eliminar el rol porque tiene usuarios asociados. Por favor, inactivelo.");
        }

        rolesRepository.delete(entity);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RolesDto> get(String identifier) {
        return rolesRepository.findByIdentifier(identifier).map(TRolesEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<RolesDto> getSearch(int page, int size, String descripcion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {
        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado == null) ? null : estado.getValue();
        Page<TRolesEntity> pageList = rolesRepository.searchRoles(descripcion, estadoValue, fechaDesde, fechaHasta, pageable);
        return pageList.map(TRolesEntity::toDto);
    }
}
