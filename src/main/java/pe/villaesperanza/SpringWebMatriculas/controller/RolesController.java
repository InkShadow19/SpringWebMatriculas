package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.RolesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.RolesService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RolesController {

    private final RolesService rolesService;

    @PostMapping
    public RolesDto add(@RequestBody RolesDto rolesDto) {
        return rolesService.add(rolesDto);
    }

    @PatchMapping("/{identifier}")
    public RolesDto update(@PathVariable(value = "identifier") String identifier,
                             @RequestBody RolesDto rolesDto) {
        return rolesService.update(identifier, rolesDto);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<RolesDto> get(@PathVariable(value = "identifier") String identifier) {
        return rolesService.get(identifier).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<RolesDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return rolesService.getSearch(page, size, descripcion, estado, fechaDesde, fechaHasta);
    }
}
