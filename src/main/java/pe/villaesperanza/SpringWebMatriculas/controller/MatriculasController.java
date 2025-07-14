package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.MatriculasDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;
import pe.villaesperanza.SpringWebMatriculas.service.MatriculasService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matriculas")
public class MatriculasController {

    private final MatriculasService matriculasService;

    @PostMapping
    public MatriculasDto add(@RequestBody MatriculasDto matriculasDto) {
        return matriculasService.add(matriculasDto);
    }

    @PatchMapping("/{identifier}")
    public MatriculasDto update(@PathVariable(value = "identifier") String identifier,
                                 @RequestBody MatriculasDto matriculasDto) {
        return matriculasService.update(identifier, matriculasDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<MatriculasDto> get(@PathVariable(value = "identifier") String identifier) {
        return matriculasService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<MatriculasDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String procedencia,
            @RequestParam(required = false) SituacionReference situacion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return matriculasService.getSearch(page, size, codigo, procedencia, situacion, estado, fechaDesde, fechaHasta);
    }
}
