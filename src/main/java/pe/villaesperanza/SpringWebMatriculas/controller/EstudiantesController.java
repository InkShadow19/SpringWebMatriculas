package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.EstudiantesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;
import pe.villaesperanza.SpringWebMatriculas.service.EstudiantesService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estudiantes")
public class EstudiantesController {

    private final EstudiantesService estudiantesService;

    @PostMapping
    public EstudiantesDto add(@RequestBody EstudiantesDto estudiantesDto) {
        return estudiantesService.add(estudiantesDto);
    }

    @PatchMapping("/{identifier}")
    public EstudiantesDto update(@PathVariable(value = "identifier") String identifier,
                             @RequestBody EstudiantesDto estudiantesDto) {
        return estudiantesService.update(identifier, estudiantesDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<EstudiantesDto> get(@PathVariable(value = "identifier") String identifier) {
        return estudiantesService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<EstudiantesDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) GeneroReference genero,
            @RequestParam(required = false) EstadoAcademicoReference estadoA,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return estudiantesService.getSearch(page, size, descripcion, genero, estadoA, fechaDesde, fechaHasta);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        estudiantesService.delete(identifier);
    }
}
