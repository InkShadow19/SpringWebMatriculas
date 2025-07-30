package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine; // Inyectamos el motor de plantillas de Thymeleaf
    private final PagosService pagosService;     // Inyectamos el servicio de Pagos para obtener los datos

    public byte[] generateBoletaPdf(String pagoIdentifier) {
        // 1. Obtener los datos completos del pago
        PagosDto pago = pagosService.get(pagoIdentifier)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        // 2. Crear el contexto de Thymeleaf
        // Esto es como un "mapa" que le dice a Thymeleaf qué variables usar en el HTML.
        Context context = new Context();
        context.setVariable("pago", pago);

        // 3. Procesar la plantilla HTML con los datos
        // Thymeleaf tomará "boleta_template.html", leerá las variables th:*
        // y las reemplazará con los datos del objeto "pago".
        String htmlContent = templateEngine.process("boleta_template.html", context);

        // 4. Usar Flying Saucer para convertir el HTML procesado a PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            // Manejo de errores en caso de que la conversión falle
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}