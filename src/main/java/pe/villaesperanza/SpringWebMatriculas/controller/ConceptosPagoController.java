package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.ConceptosPagoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.ConceptosPagoService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conceptos/pago")
public class ConceptosPagoController {

    private final ConceptosPagoService conceptosPagoService;

    @PostMapping
    public ConceptosPagoDto add(@RequestBody ConceptosPagoDto conceptosPagoDto) {
        return conceptosPagoService.add(conceptosPagoDto);
    }

    @PatchMapping("/{identifier}")
    public ConceptosPagoDto update(@PathVariable(value = "identifier") String identifier,
                            @RequestBody ConceptosPagoDto conceptosPagoDto) {
        return conceptosPagoService.update(identifier, conceptosPagoDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ConceptosPagoDto> get(@PathVariable(value = "identifier") String identifier) {
        return conceptosPagoService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<ConceptosPagoDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return conceptosPagoService.getSearch(page, size, codigo, descripcion, estado, fechaDesde, fechaHasta);
    }

    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        conceptosPagoService.delete(identifier);
    }
}
