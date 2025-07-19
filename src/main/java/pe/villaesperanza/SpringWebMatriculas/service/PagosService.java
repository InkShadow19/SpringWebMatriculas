package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.BancosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.PagosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagosService {

    private final PagosRepository pagosRepository;
    private final BancosRepository bancosRepository;
    private final UsuariosRepository usuariosRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public PagosDto add(PagosDto pagosDto) {

        TBancosEntity banco = null;
        TUsuariosEntity usuario = null;

        try {
            banco = bancosRepository.findByIdentifier(pagosDto.getBanco()).orElse(null);
            usuario = usuariosRepository.findByIdentifier(pagosDto.getUsuario()).orElse(null);
        } catch (Exception e) {
            log.error("Error inesperado al validar los identificadores: " + e.getMessage());
        }

        TPagosEntity entity = new TPagosEntity(pagosDto, usuario, banco);
        TPagosEntity result = pagosRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public PagosDto update(String identifier, PagosDto pagosDto) {

        TPagosEntity entity = pagosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este pago no existe"));

        entity.update(pagosDto);
        TPagosEntity result = pagosRepository.save(entity);
        pagosRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<PagosDto> get(String identifier) {

        TPagosEntity result = pagosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este pago no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<PagosDto> getSearch(int page, int size, EstadoPagoReference estado, CanalReference canalPago, String ticket, Double monto, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TPagosEntity> pageList =  pagosRepository.searchPagos(estado == null ? null : estado.getValue(), canalPago == null ? null : canalPago.getValue(), ticket, monto, fechaDesde, fechaHasta, pageable);

        return pageList.map(TPagosEntity::toDto);
    }
}
