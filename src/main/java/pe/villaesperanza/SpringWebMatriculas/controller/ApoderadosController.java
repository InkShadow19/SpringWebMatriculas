package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.ApoderadosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;
import pe.villaesperanza.SpringWebMatriculas.service.ApoderadosService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apoderados")
public class ApoderadosController {

    private final ApoderadosService apoderadosService;

    @PostMapping
    public ApoderadosDto add(@RequestBody ApoderadosDto apoderadosDto) {
        return apoderadosService.add(apoderadosDto);
    }

    @PatchMapping("/{identifier}")
    public ApoderadosDto update(@PathVariable(value = "identifier") String identifier,
                                 @RequestBody ApoderadosDto apoderadosDto) {
        return apoderadosService.update(identifier, apoderadosDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ApoderadosDto> get(@PathVariable(value = "identifier") String identifier) {
        return apoderadosService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<ApoderadosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) GeneroReference genero,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return apoderadosService.getSearch(page, size, descripcion, genero, estado, fechaDesde, fechaHasta);
    }
}
