package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.villaesperanza.SpringWebMatriculas.dto.AnalisisAnualDto;
import pe.villaesperanza.SpringWebMatriculas.dto.DashboardDataDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionNivelDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.IngresosVsDeudaDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.IngresosVsDeudaProjection;
import pe.villaesperanza.SpringWebMatriculas.dto.report.SituacionAlumnoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.TendenciaMatriculaDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.TendenciaMatriculaProjection;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.AniosAcademicosRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.CronogramaRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.MatriculasRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.PagosRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MatriculasRepository matriculasRepository;
    private final PagosRepository pagosRepository;
    private final CronogramaRepository cronogramaRepository;
    private final TimeTravelService timeTravelService;
    private final AniosAcademicosRepository aniosAcademicosRepository;

    // --- MÉTODO PARA LA SECCIÓN 1: KPIs FIJOS ---
    public DashboardDataDto getKpiFijosData() {
        // KPI 1: Alumnos Matriculados (Año Activo)
        long totalMatriculados = matriculasRepository.countMatriculasActivasEnAnioActivo();

        // KPI 2: Ingresos del Mes Actual
        Instant ahora = timeTravelService.getNow();
        YearMonth mesActual = YearMonth.from(ahora.atZone(ZoneOffset.UTC));
        Instant inicioMes = mesActual.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant inicioMesSiguiente = mesActual.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        double ingresosMes = pagosRepository.sumIngresosDelMes(inicioMes, inicioMesSiguiente);

        // KPI 3: Deuda Pendiente Global (con mora calculada)
        List<TCronogramaPagosEntity> deudasPendientes = cronogramaRepository.findAllPendientesGlobal();
        double totalDeuda = deudasPendientes.stream().mapToDouble(deuda -> {
            double montoAPagar = deuda.getMontoAPagar();
            Instant fechaVencimiento = deuda.getFechaVencimiento();
            Instant inicioDiaSiguienteAlVencimiento = fechaVencimiento.plus(1, ChronoUnit.DAYS);
            if (ahora.isAfter(inicioDiaSiguienteAlVencimiento)) {
                Instant fechaLimiteTolerancia = inicioDiaSiguienteAlVencimiento.plus(7, ChronoUnit.DAYS);
                if (ahora.isAfter(fechaLimiteTolerancia)) {
                    montoAPagar += 10.00;
                }
            }
            return montoAPagar;
        }).sum();

        // KPI 4: Alumnos Morosos Global
        Instant fechaLimiteMorosidad = ahora.minus(7, ChronoUnit.DAYS);
        long totalMorosos = cronogramaRepository.countAlumnosMorososGlobal(fechaLimiteMorosidad);

        return DashboardDataDto.builder()
                .totalAlumnosMatriculadosAnioActivo(totalMatriculados)
                .ingresosDelMesActual(ingresosMes)
                .totalDeudaPendienteGlobal(totalDeuda)
                .totalAlumnosMorososGlobal(totalMorosos)
                .build();
    }

    // --- MÉTODO PARA LA SECCIÓN 2: ANÁLISIS HISTÓRICO ---
    public AnalisisAnualDto getAnalisisAnualData(String anioId, String nivelId) {
        if (anioId == null || anioId.isEmpty()) {
            return AnalisisAnualDto.builder().build();
        }

        Integer anio = aniosAcademicosRepository.findByIdentifier(anioId)
                .map(anioEntity -> anioEntity.getAnio())
                .orElseThrow(() -> new RuntimeException("Año académico no encontrado"));

        Instant inicioAnio = LocalDate.of(anio, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant finAnio = LocalDate.of(anio + 1, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
        double ingresosAnio = pagosRepository.sumIngresosDelAnioPorFechaPago(inicioAnio, finAnio);

        List<TendenciaMatriculaProjection> proyeccionTendencia = matriculasRepository.getTendenciaMatriculasPorAnioNativo(anioId);
        List<TendenciaMatriculaDto> tendencia = proyeccionTendencia.stream()
            .map(p -> {
                String nombreMes = Month.of(p.getMes()).getDisplayName(
                    java.time.format.TextStyle.FULL, 
                    Locale.of("es", "PE")
                );
                nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                return new TendenciaMatriculaDto(nombreMes, p.getTotal());
            })
            .collect(Collectors.toList());

        List<DistribucionNivelDto> distribucion = matriculasRepository.getDistribucionPorNivelPorAnio(anioId);

        Instant fechaLimite = timeTravelService.getNow();
        List<IngresosVsDeudaProjection> proyeccionIngresosVsDeuda = pagosRepository.getIngresosVsDeudaPorAnioNativo(anioId, fechaLimite);
        List<IngresosVsDeudaDto> ingresosVsDeuda = proyeccionIngresosVsDeuda.stream()
            .map(p -> {
                String nombreMes = Month.of(p.getMes()).getDisplayName(
                    java.time.format.TextStyle.FULL, 
                    Locale.of("es", "PE")
                );
                nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                return new IngresosVsDeudaDto(nombreMes, p.getIngresos(), p.getDeudaVencida());
            })
            .collect(Collectors.toList());

        List<DistribucionGradoDto> distribucionGrado = new ArrayList<>();
        if (nivelId != null && !nivelId.isEmpty()) {
            distribucionGrado = matriculasRepository.getDistribucionPorGradoPorAnioYNivel(anioId, nivelId);
        }

        List<SituacionAlumnoDto> distribucionSituacion = matriculasRepository.getDistribucionSituacionPorAnio(anioId);

        return AnalisisAnualDto.builder()
            .totalIngresosDelAnio(ingresosAnio)
            .tendenciaMatriculas(tendencia)
            .distribucionAlumnosPorNivel(distribucion)
            .ingresosVsDeuda(ingresosVsDeuda)
            .distribucionAlumnosPorGrado(distribucionGrado)
            .distribucionSituacion(distribucionSituacion)
            .build();
    }
}