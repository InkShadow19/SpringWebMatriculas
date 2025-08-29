package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.EstadoCuentaEstudianteDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.MorosidadAgrupadaDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.CronogramaRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.MatriculasRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.PagosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final MatriculasRepository matriculasRepository;
    private final CronogramaRepository cronogramaRepository;
    private final PagosRepository pagosRepository;
    private final TimeTravelService timeTravelService; // Maquina del Tiempo

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<List<EstadoCuentaEstudianteDto>> getEstadoEstudiante(String estudiante, Integer anio) {

        List<TCronogramaPagosEntity> result = cronogramaRepository.estadoCuenta(estudiante, anio);

        // --- LÓGICA MODIFICADA ---
        if (result != null && !result.isEmpty()) {
            // Si se encontraron deudas para el año solicitado, se procesan y devuelven como antes.
            Instant fechaActual = timeTravelService.getNow();
            double montoMora = 10.00;

            List<EstadoCuentaEstudianteDto> dtos = result.stream()
                    .map(deuda -> {
                        EstadoCuentaEstudianteDto dto = deuda.estadoCuentaEstudante();
                        double montoOriginalAPagar = (dto.getMontoOriginal() != null ? dto.getMontoOriginal() : 0) - (dto.getDescuento() != null ? dto.getDescuento() : 0);

                        if (dto.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE) {
                            Instant fechaVencimiento = Instant.parse(dto.getFechaVencimiento());
                            Instant inicioDiaSiguienteAlVencimiento = fechaVencimiento.plus(1, ChronoUnit.DAYS);

                            if (fechaActual.isAfter(inicioDiaSiguienteAlVencimiento)) {
                                dto.setEstadoDeuda(EstadoDeudaReference.VENCIDO);
                                Instant fechaLimiteTolerancia = inicioDiaSiguienteAlVencimiento.plus(7, ChronoUnit.DAYS);
                                if (fechaActual.isAfter(fechaLimiteTolerancia)) {
                                    dto.setMora(montoMora);
                                    dto.setMontoAPagar(montoOriginalAPagar + montoMora);
                                }
                            }
                        } else if (dto.getEstadoDeuda() == EstadoDeudaReference.PAGADO && dto.getMora() != null && dto.getMora() > 0) {
                            dto.setMontoAPagar(montoOriginalAPagar + dto.getMora());
                        }
                        
                        return dto;
                    }).collect(Collectors.toList());

            return Optional.of(dtos);

        } else {
            // Si no se encontraron deudas, se realiza la verificación en otros años.
            List<Integer> aniosConDeuda = cronogramaRepository.findAniosConDeudasPendientes(estudiante);
            
            if (!aniosConDeuda.isEmpty()) {
                // Si hay deudas, se formatea el mensaje y se lanza una excepción que el frontend capturará.
                String aniosStr = aniosConDeuda.stream().map(String::valueOf).collect(Collectors.joining(", "));
                throw new AppException("El estudiante tiene deudas pendientes del año/s: " + aniosStr + ". Por favor, active el año correspondiente para poder registrar el pago.");
            }
        }

        // Si no se encontraron deudas en el año activo NI en otros años, se retorna un opcional vacío.
        return Optional.empty();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<List<AlumnoPorGradoDto>> alumnoPorGrado(Integer anio, String nivel, String grado) {
        List<AlumnoPorGradoDto> result = matriculasRepository.alumnoPorGrado(anio, nivel, grado);
        return Optional.of(result);
    }

    // --- MÉTODO ACTUALIZADO CON AÑO ---
    public Optional<List<MorosidadAgrupadaDto>> porMorosidad(Integer anio, String nivel, String grado) {
        Instant ahora = timeTravelService.getNow();
        Instant fechaLimite = ahora.minus(7, ChronoUnit.DAYS);
        
        List<MorosidadAgrupadaDto> result = cronogramaRepository.porMorosidadAgrupada(fechaLimite, anio, nivel, grado);
        return Optional.of(result);
    }

    public Optional<List<PagosPorPeriodosDto>> pagosPorPeriodos(Instant fechaDesde, Instant fechaHasta) {
        List<PagosPorPeriodosDto> result = pagosRepository.pagosPorPeriodos(fechaDesde, fechaHasta);
        return Optional.of(result);
    }
}