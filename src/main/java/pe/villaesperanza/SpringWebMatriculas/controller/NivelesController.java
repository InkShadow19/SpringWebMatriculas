package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.NivelesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.NivelesService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/niveles")
public class NivelesController {

    private final NivelesService nivelesService;

    @PostMapping
    public NivelesDto add(@RequestBody NivelesDto nivel) {
        return nivelesService.add(nivel);
    }

    @PatchMapping("/{identifier}")
    public NivelesDto update(@PathVariable(value = "identifier") String identifier,
                             @RequestBody NivelesDto nivel) {
        return nivelesService.update(identifier, nivel);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<NivelesDto> get(@PathVariable(value = "identifier") String identifier) {
        return nivelesService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<NivelesDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return nivelesService.getSearch(page, size, descripcion, estado, fechaDesde, fechaHasta);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        nivelesService.delete(identifier);
    }
}
