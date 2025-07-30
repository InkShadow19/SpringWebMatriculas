package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;
import pe.villaesperanza.SpringWebMatriculas.service.PagosService;
import pe.villaesperanza.SpringWebMatriculas.service.PdfGenerationService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pagos")
public class PagosController {

    private final PagosService pagosService;
    private final PdfGenerationService pdfGenerationService; // <-- INYECTA EL NUEVO SERVICIO


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

    // --- ENDPOINT DE BÚSQUEDA ACTUALIZADO ---
    @GetMapping("/search")
    public Page<PagosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EstadoPagoReference estado,
            @RequestParam(required = false) CanalReference canal,
            @RequestParam(required = false) String descripcion, // Parámetro unificado para búsqueda
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaHasta
    ) {
        return pagosService.getSearch(page, size, estado, canal, descripcion, fechaDesde, fechaHasta);
    }

    // --- NUEVO ENDPOINT PARA ANULAR PAGO ---
    @PatchMapping("/{identifier}/anular")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anular(@PathVariable(value = "identifier") String identifier) {
        pagosService.anular(identifier);
    }

    // --- NUEVO ENDPOINT PARA CONSULTAR EL SIGUIENTE TICKET ---
    @GetMapping("/next-caja-ticket")
    public ResponseEntity<String> getNextCajaTicket() {
        return ResponseEntity.ok(pagosService.getNextCajaTicket());
    }

    // --- NUEVO ENDPOINT PARA DESCARGAR LA BOLETA EN PDF ---
    @GetMapping("/{identifier}/imprimir")
    public ResponseEntity<byte[]> downloadBoleta(@PathVariable String identifier) {
        // 1. Llamamos a nuestro servicio para generar el PDF en memoria
        byte[] pdfBytes = pdfGenerationService.generateBoletaPdf(identifier);

        // 2. Preparamos las cabeceras de la respuesta para el navegador
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // El nombre del archivo que se descargará (ej. Boleta-V-0001.pdf)
        headers.setContentDispositionFormData("attachment", "Boleta-" + identifier + ".pdf");
        headers.setContentLength(pdfBytes.length);

        // 3. Devolvemos el archivo PDF al frontend
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
