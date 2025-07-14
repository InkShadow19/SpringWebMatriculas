package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.EstadoCuentaEstudianteDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PorMorosidadDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.CronogramaRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.MatriculasRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.PagosRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final MatriculasRepository matriculasRepository;
    private final CronogramaRepository cronogramaRepository;
    private final PagosRepository pagosRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<List<EstadoCuentaEstudianteDto>> getEstadoEstudiante(String estudiante) {

        List<TCronogramaPagosEntity> result = cronogramaRepository.estadoCuenta(estudiante);

        if (result == null || result.isEmpty()) return Optional.empty();

        List<EstadoCuentaEstudianteDto> dtos = result.stream()
                .map(TCronogramaPagosEntity::estadoCuentaEstudante).toList();

        return Optional.of(dtos);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<List<AlumnoPorGradoDto>> alumnoPorGrado(Integer anio, String nivel, String grado) {

        List<AlumnoPorGradoDto> result = matriculasRepository.alumnoPorGrado(anio, nivel, grado);
        return Optional.of(result);
    }

    public Optional<List<PorMorosidadDto>> porMorosidad(String nivel, String grado) {

        List<PorMorosidadDto> result = cronogramaRepository.porMorosidad(nivel, grado);
        return Optional.of(result);
    }

    public Optional<List<PagosPorPeriodosDto>> pagosPorPeriodos(Instant fechaDesde, Instant fechaHasta) {

        List<PagosPorPeriodosDto> result = pagosRepository.pagosPorPeriodos(fechaDesde, fechaHasta);
        return Optional.of(result);
    }
}
