package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import pe.villaesperanza.SpringWebMatriculas.dto.PagoDetallesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.util.NumberToWordsConverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine; // Inyectamos el motor de plantillas de Thymeleaf
    private final PagosService pagosService;     // Inyectamos el servicio de Pagos para obtener los datos
    private final NumberToWordsConverter numberToWordsConverter; // Inyectar la nueva herramienta

    public byte[] generateBoletaPdf(String pagoIdentifier) {
        PagosDto pago = pagosService.get(pagoIdentifier)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        // --- LÓGICA PARA CALCULAR LA SUMA DE MORAS ---
        double moraTotal = pago.getDetalles().stream()
                             .filter(detalle -> detalle.getMora() != null && detalle.getMora() > 0)
                             .mapToDouble(PagoDetallesDto::getMora)
                             .sum();
        pago.setTotalMora(moraTotal);

        // --- LÓGICA DE FORMATO DE FECHA EN EL BACKEND ---
        // 1. Convertimos el String de la fecha a un objeto Instant
        Instant fechaPagoInstant = Instant.parse(pago.getFechaPago());

        // 2. Definimos el formato y la zona horaria deseada.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a")
                .withLocale(Locale.of("es", "PE"))
                .withZone(ZoneId.of("America/Lima"));
        
        // 3. Formateamos el objeto Instant y lo guardamos en el campo del DTO.
        pago.setFechaPagoFormateada(formatter.format(fechaPagoInstant));
        
        pago.setMontoTotalEnPalabras(numberToWordsConverter.convertToWords(pago.getMontoTotalPagado()));

        // --- INICIO DE LA SOLUCIÓN ---
        // 1. Obtenemos la ruta absoluta de la imagen.
        File logoFile = new File("src/main/resources/static/images/logo.png");
        String logoPath = logoFile.toURI().toString();

        // 2. Pasamos la ruta al contexto de Thymeleaf.
        Context context = new Context();
        context.setVariable("pago", pago);
        context.setVariable("logoUrl", logoPath); // <-- AÑADIMOS LA RUTA AL CONTEXTO
        // --- FIN DE LA SOLUCIÓN ---
        
        String htmlContent = templateEngine.process("boleta_template.html", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}