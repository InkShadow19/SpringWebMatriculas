package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.MatriculasDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoMatriculaReference;
//import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.*;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.io.IOException;
//import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatriculasService {

    private final MatriculasRepository matriculasRepository;
    private final NivelesRepository nivelesRepository;
    private final EstudiantesRepository estudiantesRepository;
    private final ApoderadosRepository apoderadosRepository;
    private final AniosAcademicosRepository aniosAcademicosRepository;
    private final GradosRepository gradosRepository;
    private final ConceptosPagoRepository conceptosPagoRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto add(MatriculasDto matriculasDto) {

        // --- OBTENER ENTIDADES RELACIONADAS ---
        TAniosAcademicosEntity anioAcademico = aniosAcademicosRepository
                .findByIdentifier(matriculasDto.getAnioAcademico())
                .orElseThrow(() -> new AppException("El año académico especificado no existe."));

        TEstudiantesEntity estudiante = estudiantesRepository.findByIdentifier(matriculasDto.getEstudiante())
                .orElseThrow(() -> new AppException("El estudiante especificado no existe."));

        TApoderadosEntity apoderado = apoderadosRepository.findByIdentifier(matriculasDto.getApoderado())
                .orElseThrow(() -> new AppException("El apoderado especificado no existe."));

        TNivelesEntity nivel = nivelesRepository.findByIdentifier(matriculasDto.getNivel())
                .orElseThrow(() -> new AppException("El nivel especificado no existe."));

        TGradosEntity grado = gradosRepository.findByIdentifier(matriculasDto.getGrado())
                .orElseThrow(() -> new AppException("El grado especificado no existe."));

        // --- LÓGICA DE GENERACIÓN DE CÓDIGO MODIFICADA ---
        Integer anioActual = anioAcademico.getAnio();
        List<TMatriculasEntity> matriculasDelAnio = matriculasRepository
                .findByAniosAcademicosEntity_AnioOrderByCodigoDesc(anioActual);

        int correlativo = 1;
        if (!matriculasDelAnio.isEmpty()) {
            // Obtenemos el último código (ej: "M2025001")
            String ultimoCodigo = matriculasDelAnio.get(0).getCodigo();
            // Extraemos solo la parte numérica del final (ej: "001") y la convertimos a
            // número
            int ultimoCorrelativo = Integer.parseInt(ultimoCodigo.substring(5)); // De la posición 5 en adelante
            correlativo = ultimoCorrelativo + 1;
        }

        // Formateamos el nuevo código con el prefijo "M", el año y el correlativo con 3
        // dígitos (ej: 002)
        String nuevoCodigo = "M" + anioActual + String.format("%03d", correlativo);
        matriculasDto.setCodigo(nuevoCodigo);

        // ... (el resto del método para crear la entidad y generar el cronograma no
        // cambia) ...
        TMatriculasEntity entity = new TMatriculasEntity(matriculasDto, nivel, grado, estudiante, apoderado,
                anioAcademico);
        generarCronograma(entity, anioActual);
        TMatriculasEntity result = matriculasRepository.save(entity);
        return result.toDto();
    }

    // --- NUEVO MÉTODO PRIVADO PARA GENERAR EL CRONOGRAMA ---
    private void generarCronograma(TMatriculasEntity matricula, Integer anio) {
        // Asumimos que existen conceptos de pago con códigos 'MATR' y 'PENS'
        TConceptosPagoEntity conceptoMatricula = conceptosPagoRepository.findByCodigo("MATR")
                .orElseThrow(() -> new AppException("No se encontró el concepto de pago 'MATR'"));
        TConceptosPagoEntity conceptoPension = conceptosPagoRepository.findByCodigo("PENS")
                .orElseThrow(() -> new AppException("No se encontró el concepto de pago 'PENS'"));

        // Generar la deuda de la Matrícula
        LocalDate fechaVencimientoMatricula = LocalDate.of(anio, 2, 28);
        TCronogramaPagosEntity deudaMatricula = new TCronogramaPagosEntity();
        deudaMatricula.setIdentifier(UUID.randomUUID().toString());
        deudaMatricula.setDescripcion("Matrícula " + anio);
        deudaMatricula.setMontoOriginal(conceptoMatricula.getMontoSugerido());
        deudaMatricula.setMontoAPagar(conceptoMatricula.getMontoSugerido());
        deudaMatricula.setFechaVencimiento(fechaVencimientoMatricula.atStartOfDay().toInstant(ZoneOffset.UTC));
        deudaMatricula.setEstadoDeuda(10); // PENDIENTE
        deudaMatricula.setConceptosPagoEntity(conceptoMatricula);
        deudaMatricula.setMatriculasEntity(matricula);
        matricula.getCronogramas().add(deudaMatricula);

        // Generar las 10 pensiones (de Marzo a Diciembre)
        String[] meses = { "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
                "Diciembre" };
        for (int i = 0; i < 10; i++) {
            int mes = i + 3; // Marzo es el mes 3
            LocalDate fechaVencimiento = LocalDate.of(anio, mes, 1)
                    .withDayOfMonth(LocalDate.of(anio, mes, 1).lengthOfMonth());

            TCronogramaPagosEntity pension = new TCronogramaPagosEntity();
            pension.setIdentifier(UUID.randomUUID().toString());
            pension.setDescripcion("Pensión " + meses[i]);
            pension.setMontoOriginal(conceptoPension.getMontoSugerido());
            pension.setMontoAPagar(conceptoPension.getMontoSugerido());
            pension.setFechaVencimiento(fechaVencimiento.atStartOfDay().toInstant(ZoneOffset.UTC));
            pension.setEstadoDeuda(10); // PENDIENTE
            pension.setConceptosPagoEntity(conceptoPension);
            pension.setMatriculasEntity(matricula);
            matricula.getCronogramas().add(pension);
        }
    }

    // --- MÉTODO UPDATE CON LÓGICA DE NEGOCIO ---
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto update(String identifier, MatriculasDto matriculasDto) {

        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        // Verificamos si la matrícula tiene pagos registrados
        boolean tienePagos = entity.getCronogramas().stream()
                .anyMatch(c -> c.getEstadoDeuda() == EstadoDeudaReference.PAGADO.getValue());

        if (tienePagos) {
            // Si hay pagos, solo permitimos actualizar campos no críticos
            if (matriculasDto.getSituacion() != null) {
                entity.setSituacion(matriculasDto.getSituacion().getValue());
            }
            if (matriculasDto.getProcedencia() != null) {
                entity.setProcedencia(matriculasDto.getProcedencia());
            }
            // VALIDACIÓN ADICIONAL: Si hay pagos, y el DTO intenta cambiar el grado o nivel, lanzamos un error.
            if (matriculasDto.getGrado() != null || matriculasDto.getNivel() != null) {
                throw new AppException("No se puede cambiar el nivel o grado de una matrícula con pagos registrados.");
            }
            // Los campos como nivel, grado, estudiante, apoderado NO se actualizan.

            // Solo actualizamos los campos permitidos
            entity.setSituacion(matriculasDto.getSituacion().getValue());
            entity.setProcedencia(matriculasDto.getProcedencia());
        } else {
            // Si NO hay pagos, permitimos que se actualice todo usando el método de la
            // entidad
            entity.update(matriculasDto);
        }

        // Si se está anulando desde este método, también se actualizan las deudas
        if (matriculasDto.getEstado() == EstadoMatriculaReference.ANULADA) {
            entity.setEstado(EstadoMatriculaReference.ANULADA.getValue());
            for (TCronogramaPagosEntity cronograma : entity.getCronogramas()) {
                if (cronograma.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()) {
                    cronograma.setEstadoDeuda(EstadoDeudaReference.ANULADO.getValue());
                }
            }
        }

        TMatriculasEntity result = matriculasRepository.save(entity);
        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<MatriculasDto> get(String identifier) {

        TMatriculasEntity result = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<MatriculasDto> getSearch(int page, int size, String descripcion,
                                         EstadoMatriculaReference estado, String anioId,
                                         String nivelId, String gradoId) {

        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado != null) ? estado.getValue() : null;

        Page<TMatriculasEntity> pageList = matriculasRepository.searchMatriculas(
                descripcion, estadoValue, anioId, nivelId, gradoId, pageable);

        return pageList.map(TMatriculasEntity::toDto);
    }

    // --- NUEVO MÉTODO PARA ANULAR ---
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void anular(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("La matrícula a anular no existe."));

        // VALIDACIÓN CLAVE: No se puede anular si ya tiene pagos.
        boolean tienePagos = entity.getCronogramas().stream()
                .anyMatch(c -> c.getEstadoDeuda() == EstadoDeudaReference.PAGADO.getValue());

        if (tienePagos) {
            throw new AppException("No se puede anular una matrícula que ya tiene pagos registrados.");
        }

        // Cambiamos el estado de la matrícula
        entity.setEstado(EstadoMatriculaReference.ANULADA.getValue());

        // Cambiamos el estado de todas sus deudas pendientes a "Anulado"
        for (TCronogramaPagosEntity cronograma : entity.getCronogramas()) {
            if (cronograma.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()) {
                cronograma.setEstadoDeuda(EstadoDeudaReference.ANULADO.getValue());
            }
        }
        matriculasRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe para eliminar"));
        matriculasRepository.delete(entity);
    }
}
