package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.EstadoCuentaEstudianteDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PorMorosidadDto;
import pe.villaesperanza.SpringWebMatriculas.service.ReportService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService service;

    @GetMapping("/estudiante/{identifier}/estado/cuenta")
    public ResponseEntity<List<EstadoCuentaEstudianteDto>> getEstadoEstudiante(@PathVariable(value = "identifier") String identifier) {
        return service.getEstadoEstudiante(identifier).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alumnos/por/grado")
    public Optional<List<AlumnoPorGradoDto>> alumnoPorGrado(
            @RequestParam Integer anio, @RequestParam(required = false) String nivel, @RequestParam(required = false) String grado
    ) {
        return service.alumnoPorGrado(anio, nivel, grado);
    }

    @GetMapping("/alumnos/por/grado")
    public Optional<List<PorMorosidadDto>> porMorosidad(
             @RequestParam(required = false) String nivel, @RequestParam(required = false) String grado
    ) {
        return service.porMorosidad(nivel, grado);
    }

    @GetMapping("/pagos/por/periodos")
    public Optional<List<PagosPorPeriodosDto>> pagosPorPeriodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return service.pagosPorPeriodos(fechaDesde, fechaHasta);
    }
}
