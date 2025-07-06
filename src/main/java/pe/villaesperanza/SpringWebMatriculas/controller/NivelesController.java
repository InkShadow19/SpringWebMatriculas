package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.NivelesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.NivelesService;
import pe.villaesperanza.SpringWebMatriculas.util.ApiResponse;
import pe.villaesperanza.SpringWebMatriculas.util.PagedResponse;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/niveles")
public class NivelesController {

    private final NivelesService nivelesService;

    @GetMapping("/{identifier}")
    public ApiResponse<NivelesDto> get(@PathVariable(value = "identifier") String identifier) {
        return new ApiResponse<NivelesDto>().toSuccess(nivelesService.get(identifier));
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<NivelesDto>> getSearch(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam(name = "descripcion", required = false) String descripcion,
            @RequestParam(name = "estado", required = false) EstadoReference estado,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta) {
        return new ApiResponse<PagedResponse<NivelesDto>>()
                .toSuccess(nivelesService.getSearch(page, size, descripcion, estado, fechaDesde, fechaHasta));
    }
}
