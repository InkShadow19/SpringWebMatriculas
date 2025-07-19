package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;
import pe.villaesperanza.SpringWebMatriculas.service.PagosService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pagos")
public class PagosController {

    private final PagosService pagosService;

    @PostMapping
    public PagosDto add(@RequestBody PagosDto pagosDto) {
        return pagosService.add(pagosDto);
    }

    @PatchMapping("/{identifier}")
    public PagosDto update(@PathVariable(value = "identifier") String identifier,
                            @RequestBody PagosDto pagosDto) {
        return pagosService.update(identifier, pagosDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<PagosDto> get(@PathVariable(value = "identifier") String identifier) {
        return pagosService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<PagosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EstadoPagoReference estado,
            @RequestParam(required = false) CanalReference canal,
            @RequestParam(required = false) String ticket,
            @RequestParam(required = false) Double monto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return pagosService.getSearch(page, size, estado, canal, ticket, monto, fechaDesde, fechaHasta);
    }
}
