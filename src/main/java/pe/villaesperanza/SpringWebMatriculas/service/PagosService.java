package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.BancosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.CronogramaRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.PagosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagosService {

    private final PagosRepository pagosRepository;
    private final BancosRepository bancosRepository;
    private final UsuariosRepository usuariosRepository;
    private final CronogramaRepository cronogramaRepository;
    private final TimeTravelService timeTravelService; // Maquina del Tiempo

    @Transactional(readOnly = true)
    public String getNextCajaTicket() {
        Optional<TPagosEntity> ultimoPagoCaja = pagosRepository
                .findFirstByCanalPagoOrderByIdDesc(CanalReference.CAJA.getValue());

        int nuevoCorrelativo = 1;
        if (ultimoPagoCaja.isPresent()) {
            String ultimoTicket = ultimoPagoCaja.get().getNumeroTicket();
            try {
                int ultimoNumero = Integer.parseInt(ultimoTicket.substring(2)); // Extrae el número de "C-0001"
                nuevoCorrelativo = ultimoNumero + 1;
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // Si el formato del último ticket es inesperado, reinicia el conteo.
                log.warn("Formato de ticket de caja inesperado: " + ultimoTicket + ". Reiniciando correlativo.");
                nuevoCorrelativo = 1;
            }
        }
        return "C-" + String.format("%07d", nuevoCorrelativo);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public PagosDto add(PagosDto pagosDto) {
        TBancosEntity banco = null;
        if (pagosDto.getBanco() != null && !pagosDto.getBanco().isEmpty()) {
            banco = bancosRepository.findByIdentifier(pagosDto.getBanco()).orElse(null);
        }

        // Se obtiene el nombre de usuario de la sesión activa
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Se busca al usuario en la base de datos
        TUsuariosEntity usuario = usuariosRepository.findByUsuario(username)
                .orElseThrow(() -> new AppException("El usuario actual no se encuentra en la base de datos."));

        // El resto del método continúa igual
        pagosDto.setUsuario(usuario.getIdentifier());

        // --- LÓGICA DE GENERACIÓN DE TICKET (INCLUIDA) ---
        if (pagosDto.getCanalPago() == CanalReference.CAJA) {
            // Se llama al método para obtener el siguiente ticket y se asigna al DTO
            pagosDto.setNumeroTicket(getNextCajaTicket());
        }

        TPagosEntity entity = new TPagosEntity(pagosDto, usuario, banco);

        // --- LÓGICA DE FECHAS CENTRALIZADA ---
        Instant ahora = timeTravelService.getNow();
        entity.setFechaPago(ahora);
        entity.setFechaCreacion(ahora);
        entity.setFechaActualizacion(ahora);

        Set<TPagoDetallesEntity> detalles = pagosDto.getDetalles().stream().map(detalleDto -> {
            TCronogramaPagosEntity deuda = cronogramaRepository.findByIdentifier(detalleDto.getCronograma())
                    .orElseThrow(
                            () -> new AppException("La deuda con ID " + detalleDto.getCronograma() + " no existe."));

            // --- LÓGICA DE MORA CORREGIDA Y CONSISTENTE ---
            // Solo evaluamos la mora si la deuda estaba pendiente antes de este pago.
            if (deuda.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()) {
                Instant fechaVencimiento = deuda.getFechaVencimiento();
                Instant inicioDiaSiguienteAlVencimiento = fechaVencimiento.plus(1, ChronoUnit.DAYS);

                // Si se está pagando después del día de vencimiento...
                if (ahora.isAfter(inicioDiaSiguienteAlVencimiento)) {
                    Instant fechaLimiteTolerancia = inicioDiaSiguienteAlVencimiento.plus(7, ChronoUnit.DAYS);
                    
                    // ...y además ha pasado la semana de tolerancia, se guarda la mora.
                    if (ahora.isAfter(fechaLimiteTolerancia)) {
                        deuda.setMora(10.00);
                    }
                }
            }

            deuda.setEstadoDeuda(EstadoDeudaReference.PAGADO.getValue());
            deuda.setFechaActualizacion(ahora); // También se actualiza la fecha de la deuda
            cronogramaRepository.save(deuda);

            return new TPagoDetallesEntity(detalleDto, deuda, entity);
        }).collect(Collectors.toSet());

        entity.setDetalles(detalles);
        TPagosEntity result = pagosRepository.save(entity);
        return enriquecerPagoDto(result); // Usamos el enriquecedor para devolver el DTO completo
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

        return pagosRepository.findByIdentifier(identifier) // 1. Busca la entidad
                .map(this::enriquecerPagoDto);              // 2. La enriquece (esto ya convierte a DTO adentro)
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<PagosDto> getSearch(int page, int size, EstadoPagoReference estado, CanalReference canalPago,
            String descripcion, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TPagosEntity> pageList = pagosRepository.searchPagos(
                estado == null ? null : estado.getValue(),
                canalPago == null ? null : canalPago.getValue(),
                descripcion,
                fechaDesde,
                fechaHasta,
                pageable);

        // Mapeamos la página de entidades a una página de DTOs enriquecidos
        return pageList.map(this::enriquecerPagoDto);
    }

    // --- MÉTODO AUXILIAR PARA ENRIQUECER EL DTO ---
    private PagosDto enriquecerPagoDto(TPagosEntity entity) {
        PagosDto dto = entity.toDto();

        // 1. Añadir nombre completo del usuario que registró
        if (entity.getUsuariosEntity() != null) {
            dto.setNombreUsuario(
                    entity.getUsuariosEntity().getNombres() + " " + entity.getUsuariosEntity().getApellidos());
        } else {
            dto.setNombreUsuario("N/A");
        }

        if (entity.getBancosEntity() != null) {
            dto.setNombreBanco(entity.getBancosEntity().getDescripcion());
        }

        // 2. Añadir nombre completo del estudiante
        if (entity.getDetalles() != null && !entity.getDetalles().isEmpty()) {
            entity.getDetalles().stream().findFirst().ifPresent(detalle -> {
                TEstudiantesEntity estudiante = detalle.getCronogramaPagosEntity().getMatriculasEntity()
                        .getEstudiantesEntity();
                if (estudiante != null) {
                    // CORRECCIÓN: Se añaden ambos apellidos
                    dto.setNombreEstudiante(estudiante.getNombre() + " " + estudiante.getApellidoPaterno() + " "
                            + estudiante.getApellidoMaterno());
                }
            });
        }
        if (dto.getNombreEstudiante() == null) {
            dto.setNombreEstudiante("No asociado");
        }

        return dto;
    }

    // --- NUEVO MÉTODO PARA LA LÓGICA DE ANULACIÓN ---
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void anular(String identifier) {
        TPagosEntity pago = pagosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El pago que intenta anular no existe."));

        Instant ahora = timeTravelService.getNow();

        // 1. Cambiar el estado del pago a ANULADO
        pago.setEstado(EstadoPagoReference.ANULADO.getValue());
        pago.setFechaActualizacion(ahora); // Se actualiza la fecha del pago anulado

        // 2. Revertir el estado de las deudas asociadas
        for (TPagoDetallesEntity detalle : pago.getDetalles()) {
            TCronogramaPagosEntity deuda = detalle.getCronogramaPagosEntity();
            if (deuda != null) {
                // Se devuelve la deuda a PENDIENTE
                deuda.setEstadoDeuda(EstadoDeudaReference.PENDIENTE.getValue());
                // También reseteamos la mora guardada, ya que el pago se anuló
                deuda.setMora(null);
                deuda.setFechaActualizacion(ahora); // Se actualiza la fecha de la deuda revertida
                cronogramaRepository.save(deuda);
            }
        }

        pagosRepository.save(pago);
    }
}
