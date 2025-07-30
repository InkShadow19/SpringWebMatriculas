package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.MatriculasDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoMatriculaReference;
//import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;
import pe.villaesperanza.SpringWebMatriculas.service.MatriculasService;

//import java.time.Instant;

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

    /*@GetMapping("/search")
    public Page<MatriculasDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String procedencia,
            @RequestParam(required = false) SituacionReference situacion,
            @RequestParam(required = false) EstadoMatriculaReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return matriculasService.getSearch(page, size, codigo, procedencia, situacion, estado, fechaDesde, fechaHasta);
    }*/

    // --- ENDPOINT DE BÚSQUEDA MODIFICADO ---
    @GetMapping("/search")
    public Page<MatriculasDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoMatriculaReference estado,
            @RequestParam(required = false) String anioId,
            @RequestParam(required = false) String nivelId,
            @RequestParam(required = false) String gradoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return matriculasService.getSearch(page, size, descripcion, estado, anioId, nivelId, gradoId, fechaDesde, fechaHasta);
    }

    // --- NUEVO ENDPOINT PARA 'ANULAR' ---
    @PatchMapping("/{identifier}/anular")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anular(@PathVariable(value = "identifier") String identifier) {
        matriculasService.anular(identifier);
    }

    // --- NUEVO ENDPOINT PARA 'COMPLETAR' MATRÍCULA ---
    @PatchMapping("/{identifier}/completar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completar(@PathVariable(value = "identifier") String identifier) {
        matriculasService.completar(identifier);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        matriculasService.delete(identifier);
    }
}
