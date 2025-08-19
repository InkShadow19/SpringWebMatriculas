package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.villaesperanza.SpringWebMatriculas.dto.AnalisisAnualDto;
import pe.villaesperanza.SpringWebMatriculas.dto.DashboardDataDto;
import pe.villaesperanza.SpringWebMatriculas.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Endpoint para los KPIs fijos
    @GetMapping("/kpis-fijos")
    public ResponseEntity<DashboardDataDto> getKpiFijosData() {
        return ResponseEntity.ok(dashboardService.getKpiFijosData());
    }

    // --- NUEVO ENDPOINT PARA LA SECCIÓN 2: ANÁLISIS HISTÓRICO ---
    @GetMapping("/analisis-anual")
    public ResponseEntity<AnalisisAnualDto> getAnalisisAnualData(
            @RequestParam String anioId,
            @RequestParam(required = false) String nivelId) {
        return ResponseEntity.ok(dashboardService.getAnalisisAnualData(anioId, nivelId));
    }
}