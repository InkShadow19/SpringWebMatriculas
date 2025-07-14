package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.GradosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.GradosService;

import java.time.Instant;
@RestController
@RequiredArgsConstructor
@RequestMapping("/grados")
public class GradosController {

    private final GradosService gradosService;

    @PostMapping
    public GradosDto add(@RequestBody GradosDto gradosDto) {
        return gradosService.add(gradosDto);
    }

    @PatchMapping("/{identifier}")
    public GradosDto update(@PathVariable(value = "identifier") String identifier,
                            @RequestBody GradosDto gradosDto) {
        return gradosService.update(identifier, gradosDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<GradosDto> get(@PathVariable(value = "identifier") String identifier) {
        return gradosService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<GradosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return gradosService.getSearch(page, size, descripcion, estado, fechaDesde, fechaHasta);
    }
}
