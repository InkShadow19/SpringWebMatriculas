package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.villaesperanza.SpringWebMatriculas.dto.BancosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.BancosService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bancos")
public class BancosController {

    private final BancosService bancosService;

    @PostMapping
    public BancosDto add(@Valid @RequestBody BancosDto bancosDto) {
        return bancosService.add(bancosDto);
    }

    @PatchMapping("/{identifier}")
    public BancosDto update(@PathVariable(value = "identifier") String identifier,
                             @Valid @RequestBody BancosDto bancosDto) {
        return bancosService.update(identifier, bancosDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<BancosDto> get(@PathVariable(value = "identifier") String identifier) {
        return bancosService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<BancosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return bancosService.getSearch(page, size, descripcion, estado, fechaDesde, fechaHasta);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        bancosService.delete(identifier);
    }
}
