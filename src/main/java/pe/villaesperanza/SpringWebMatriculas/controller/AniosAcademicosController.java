package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.AniosAcademicosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.AniosAcademicosService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/anios/academicos")
public class AniosAcademicosController {

    private final AniosAcademicosService academicosService;

    @PostMapping
    public AniosAcademicosDto add(@RequestBody AniosAcademicosDto aniosAcademicosDto) {
        return academicosService.add(aniosAcademicosDto);
    }

    @PatchMapping("/{identifier}")
    public AniosAcademicosDto update(@PathVariable(value = "identifier") String identifier,
                             @RequestBody AniosAcademicosDto aniosAcademicosDto) {
        return academicosService.update(identifier, aniosAcademicosDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<AniosAcademicosDto> get(@PathVariable(value = "identifier") String identifier) {
        return academicosService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<AniosAcademicosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) EstadoAcademicoReference estadoA,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return academicosService.getSearch(page, size, anio, estado, estadoA, fechaDesde, fechaHasta);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        academicosService.delete(identifier);
    }
}
