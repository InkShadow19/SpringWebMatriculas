package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.villaesperanza.SpringWebMatriculas.dto.CronogramaPagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.MatriculasDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoMatriculaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.*;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final TimeTravelService timeTravelService; // Maquina del Tiempo

    private void validarMatriculaUnica(String estudianteId, String anioAcademicoId, String matriculaId) {
        // Le pasamos el estado ANULADA para que lo excluya de la búsqueda
        boolean yaExiste = matriculasRepository
                .existsByEstudiantesEntity_IdentifierAndAniosAcademicosEntity_IdentifierAndEstadoNotAndIdentifierNot(
                        estudianteId, anioAcademicoId, EstadoMatriculaReference.ANULADA.getValue(),
                        matriculaId != null ? matriculaId : "dummy-id");
        if (yaExiste) {
            throw new AppException("El estudiante ya tiene una matrícula vigente para este año académico.");
        }
    }

    // --- MÉTODO PRIVADO ACTUALIZADO CON LÓGICA DE TOLERANCIA ---
    private MatriculasDto enriquecerConMora(MatriculasDto dto) {
        Instant fechaActual = timeTravelService.getNow();
        //Instant fechaActual = Instant.now();
        double montoMoraFija = 10.00;

        List<CronogramaPagosDto> cronogramasActualizados = dto.getCronogramas().stream()
                .map(cuota -> {
                    double montoOriginalAPagar = (cuota.getMontoOriginal() != null ? cuota.getMontoOriginal() : 0) - (cuota.getDescuento() != null ? cuota.getDescuento() : 0);

                    // CASO 1: La deuda está PENDIENTE (verificamos si está vencida)
                    if (cuota.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE) {
                        Instant fechaVencimiento = Instant.parse(cuota.getFechaVencimiento());

                        // --- LÓGICA DE VENCIMIENTO CORREGIDA ---
                        // 1. Se calcula el inicio del día SIGUIENTE al vencimiento.
                        Instant inicioDiaSiguienteAlVencimiento = fechaVencimiento.plus(1, ChronoUnit.DAYS);

                        // 2. Se verifica si la fecha actual ya pasó el día de vencimiento.
                        if (fechaActual.isAfter(inicioDiaSiguienteAlVencimiento)) {
                            cuota.setEstadoDeuda(EstadoDeudaReference.VENCIDO);
                            
                            // 3. La tolerancia de 7 días empieza a contar desde el día siguiente al vencimiento.
                            Instant fechaLimiteTolerancia = inicioDiaSiguienteAlVencimiento.plus(7, ChronoUnit.DAYS);
                            
                            // Si la fecha actual ha pasado la semana de tolerancia, APLICAMOS LA MORA
                            if (fechaActual.isAfter(fechaLimiteTolerancia)) {
                                cuota.setMora(montoMoraFija);
                                cuota.setMontoAPagar(montoOriginalAPagar + montoMoraFija);
                            }
                        }
                    }
                    // CASO 2: La deuda ya está PAGADA y tiene una mora registrada
                    else if (cuota.getEstadoDeuda() == EstadoDeudaReference.PAGADO && cuota.getMora() != null && cuota.getMora() > 0) {
                        // Recalculamos el monto total pagado para asegurar que la vista sea correcta
                        cuota.setMontoAPagar(montoOriginalAPagar + cuota.getMora());
                    }

                    return cuota;
                }).collect(Collectors.toList());

        dto.setCronogramas(cronogramasActualizados);
        return dto;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto add(MatriculasDto matriculasDto) {
        // Obtenemos la fecha "actual" de nuestro reloj especial para saber el mes.
        Instant ahora = timeTravelService.getNow();
        Month mesDeInscripcion = LocalDate.ofInstant(ahora, ZoneOffset.UTC).getMonth();

        // Obtenemos el año académico seleccionado en el formulario (ej. 2023 ACTIVO).
        TAniosAcademicosEntity anioSeleccionadoEnFormulario = aniosAcademicosRepository
                .findByIdentifier(matriculasDto.getAnioAcademico())
                .orElseThrow(() -> new AppException("El año académico especificado no existe."));

        // Por defecto, el año efectivo de la matrícula es el seleccionado en el formulario.
        TAniosAcademicosEntity anioAcademicoEfectivo = anioSeleccionadoEnFormulario;
        Integer anioParaCalculos = anioAcademicoEfectivo.getAnio();

        // --- LÓGICA DE EXCEPCIÓN PARA DICIEMBRE ---
        // Si la matrícula se hace en Diciembre...
        if (mesDeInscripcion == Month.DECEMBER) {
            Integer anioSiguiente = anioSeleccionadoEnFormulario.getAnio() + 1;
            // ...buscamos el registro del AÑO SIGUIENTE en la base de datos.
            anioAcademicoEfectivo = aniosAcademicosRepository.findByAnio(anioSiguiente)
                    .orElseThrow(() -> new AppException("El año académico " + anioSiguiente + " no ha sido creado. Por favor, créelo antes de registrar matrículas de Diciembre."));
            anioParaCalculos = anioSiguiente;
        }

        // --- VALIDACIÓN DE UNICIDAD (ahora usa el año académico EFECTIVO) ---
        validarMatriculaUnica(matriculasDto.getEstudiante(), anioAcademicoEfectivo.getIdentifier(), null);

        // --- BÚSQUEDA DE OTRAS ENTIDADES (sin cambios) ---
        TEstudiantesEntity estudiante = estudiantesRepository.findByIdentifier(matriculasDto.getEstudiante()).orElseThrow(() -> new AppException("El estudiante especificado no existe."));
        TApoderadosEntity apoderado = apoderadosRepository.findByIdentifier(matriculasDto.getApoderado()).orElseThrow(() -> new AppException("El apoderado especificado no existe."));
        TNivelesEntity nivel = nivelesRepository.findByIdentifier(matriculasDto.getNivel()).orElseThrow(() -> new AppException("El nivel especificado no existe."));
        TGradosEntity grado = gradosRepository.findByIdentifier(matriculasDto.getGrado()).orElseThrow(() -> new AppException("El grado especificado no existe."));

        // --- LÓGICA DE CORRELATIVO (ahora usa el año para cálculos) ---
        Optional<TMatriculasEntity> ultimaMatricula = matriculasRepository.findTopByAniosAcademicosEntity_AnioOrderByIdDesc(anioParaCalculos);
        int correlativo = 1;
        if (ultimaMatricula.isPresent()) {
            String ultimoCodigo = ultimaMatricula.get().getCodigo();
            int ultimoCorrelativo = Integer.parseInt(ultimoCodigo.substring(7));
            correlativo = ultimoCorrelativo + 1;
        }
        String nuevoCodigo = "M-" + anioParaCalculos + "-" + String.format("%03d", correlativo);
        matriculasDto.setCodigo(nuevoCodigo);

        // --- CREACIÓN DE LA ENTIDAD (ahora usa el año académico EFECTIVO) ---
        TMatriculasEntity entity = new TMatriculasEntity(matriculasDto, nivel, grado, estudiante, apoderado, anioAcademicoEfectivo);
        
        entity.setFechaMatricula(ahora);
        entity.setFechaCreacion(ahora);
        entity.setFechaActualizacion(ahora);
        
        generarCronograma(entity, anioAcademicoEfectivo.getAnio(), matriculasDto);
        TMatriculasEntity result = matriculasRepository.save(entity);
        return result.toDto();
    }

    // --- MÉTODO ADAPTADO CON LÓGICA DE EXCEPCIÓN PARA DICIEMBRE ---
    private void generarCronograma(TMatriculasEntity matricula, Integer anioDelCronograma, MatriculasDto matriculasDto) {
        TConceptosPagoEntity conceptoMatricula = conceptosPagoRepository.findByCodigo("MATR")
                .orElseThrow(() -> new AppException("No se encontró el concepto de pago 'MATR'"));
        TConceptosPagoEntity conceptoPension = conceptosPagoRepository.findByCodigo("PENS")
                .orElseThrow(() -> new AppException("No se encontró el concepto de pago 'PENS'"));

        // Obtenemos la fecha "actual" de nuestro reloj especial para la lógica de meses.
        Instant ahora = timeTravelService.getNow();
        LocalDate fechaActualSimulada = LocalDate.ofInstant(ahora, ZoneOffset.UTC);
        Month mesDeInscripcion = fechaActualSimulada.getMonth();

        // --- LÓGICA DE VENCIMIENTO DE MATRÍCULA CORREGIDA ---
        LocalDate fechaVencimientoMatricula;
        // Si la inscripción es en Diciembre, el vencimiento es en Febrero del próximo año.
        if (mesDeInscripcion == Month.DECEMBER) {
            fechaVencimientoMatricula = LocalDate.of(anioDelCronograma, Month.FEBRUARY, 28);
        } 
        // Si es entre Marzo y Noviembre, vence a fin del mes de inscripción.
        else if (mesDeInscripcion.getValue() > 2) {
            fechaVencimientoMatricula = fechaActualSimulada.withDayOfMonth(fechaActualSimulada.lengthOfMonth());
        } 
        // Si es en Enero o Febrero, el vencimiento es el estándar.
        else {
            fechaVencimientoMatricula = LocalDate.of(anioDelCronograma, Month.FEBRUARY, 28);
        }
        
        // --- El resto del método ahora confía en 'anioDelCronograma' ---
        double descuentoMatriculaValor = Optional.ofNullable(matriculasDto.getDescuentoMatricula()).orElse(0.0);
        double montoFinalMatricula = conceptoMatricula.getMontoSugerido() - descuentoMatriculaValor;
        TCronogramaPagosEntity deudaMatricula = new TCronogramaPagosEntity();
        deudaMatricula.setIdentifier(UUID.randomUUID().toString());
        deudaMatricula.setDescripcion("Matrícula " + anioDelCronograma);
        deudaMatricula.setMontoOriginal(conceptoMatricula.getMontoSugerido());
        deudaMatricula.setDescuento(descuentoMatriculaValor);
        deudaMatricula.setMontoAPagar(montoFinalMatricula);
        deudaMatricula.setFechaVencimiento(fechaVencimientoMatricula.atStartOfDay().toInstant(ZoneOffset.UTC));
        deudaMatricula.setEstadoDeuda(10);
        deudaMatricula.setConceptosPagoEntity(conceptoMatricula);
        deudaMatricula.setMatriculasEntity(matricula);
        matricula.getCronogramas().add(deudaMatricula);

        double descuentoPensionValor = Optional.ofNullable(matriculasDto.getDescuentoPension()).orElse(0.0);
        double montoFinalPension = conceptoPension.getMontoSugerido() - descuentoPensionValor;
        String[] meses = { "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
        
        for (int i = 0; i < 10; i++) {
            int mesNumero = i + 3;
            if (mesDeInscripcion == Month.DECEMBER || mesNumero >= mesDeInscripcion.getValue()) {
                LocalDate fechaVencimiento = LocalDate.of(anioDelCronograma, mesNumero, 1).withDayOfMonth(LocalDate.of(anioDelCronograma, mesNumero, 1).lengthOfMonth());
                TCronogramaPagosEntity pension = new TCronogramaPagosEntity();
                pension.setIdentifier(UUID.randomUUID().toString());
                pension.setDescripcion("Pensión " + meses[i]);
                pension.setMontoOriginal(conceptoPension.getMontoSugerido());
                pension.setDescuento(descuentoPensionValor);
                pension.setMontoAPagar(montoFinalPension);
                pension.setFechaVencimiento(fechaVencimiento.atStartOfDay().toInstant(ZoneOffset.UTC));
                pension.setEstadoDeuda(10);
                pension.setConceptosPagoEntity(conceptoPension);
                pension.setMatriculasEntity(matricula);
                matricula.getCronogramas().add(pension);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto update(String identifier, MatriculasDto matriculasDto) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        boolean tienePagos = entity.getCronogramas().stream()
                .anyMatch(c -> c.getEstadoDeuda() == EstadoDeudaReference.PAGADO.getValue());

        if (tienePagos) {
            // Si hay pagos, solo permitimos actualizar campos no críticos
            if (matriculasDto.getSituacion() != null)
                entity.setSituacion(matriculasDto.getSituacion().getValue());
            if (matriculasDto.getProcedencia() != null)
                entity.setProcedencia(matriculasDto.getProcedencia());

            // Si la situación ya no es INGRESANTE, limpiamos el campo procedencia
            if (matriculasDto.getSituacion() != SituacionReference.INGRESANTE) {
                entity.setProcedencia(null);
            }
        } else {
            // Si NO hay pagos, permitimos la edición completa y regeneramos el cronograma
            validarMatriculaUnica(matriculasDto.getEstudiante(), matriculasDto.getAnioAcademico(), identifier);

            TEstudiantesEntity estudiante = estudiantesRepository.findByIdentifier(matriculasDto.getEstudiante())
                    .orElseThrow(() -> new AppException("El estudiante especificado no existe."));
            TApoderadosEntity apoderado = apoderadosRepository.findByIdentifier(matriculasDto.getApoderado())
                    .orElseThrow(() -> new AppException("El apoderado especificado no existe."));
            TNivelesEntity nivel = nivelesRepository.findByIdentifier(matriculasDto.getNivel())
                    .orElseThrow(() -> new AppException("El nivel especificado no existe."));
            TGradosEntity grado = gradosRepository.findByIdentifier(matriculasDto.getGrado())
                    .orElseThrow(() -> new AppException("El grado especificado no existe."));

            // --- CAMBIO CLAVE: Se limpia el cronograma existente ---
            entity.getCronogramas().clear();

            entity.setEstudiantesEntity(estudiante);
            entity.setApoderadosEntity(apoderado);
            entity.setNivelesEntity(nivel);
            entity.setGradosEntity(grado);
            entity.setSituacion(matriculasDto.getSituacion().getValue());

            if (matriculasDto.getSituacion() == SituacionReference.INGRESANTE) {
                entity.setProcedencia(matriculasDto.getProcedencia());
            } else {
                entity.setProcedencia(null);
            }

            generarCronograma(entity, entity.getAniosAcademicosEntity().getAnio(), matriculasDto);
        }

        // --- LÍNEA AÑADIDA ---
        // Se establece la fecha de actualización usando el reloj especial
        entity.setFechaActualizacion(timeTravelService.getNow());

        TMatriculasEntity result = matriculasRepository.save(entity);
        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<MatriculasDto> get(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        // Se convierte a DTO y luego se enriquece con la mora
        MatriculasDto dto = enriquecerConMora(entity.toDto());
        return Optional.of(dto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<MatriculasDto> getSearch(int page, int size, String descripcion,
            EstadoMatriculaReference estado, String anioId,
            String nivelId, String gradoId,
            Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado != null) ? estado.getValue() : null;

        Page<TMatriculasEntity> pageList = matriculasRepository.searchMatriculas(
                descripcion, estadoValue, anioId, nivelId, gradoId, fechaDesde, fechaHasta, pageable);

        // Se convierte la página de Entidades a DTOs y luego se enriquece cada uno con la mora
        return pageList.map(TMatriculasEntity::toDto).map(this::enriquecerConMora);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void anular(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("La matrícula a anular no existe."));

        // Validación existente: No anular si ya tiene pagos.
        boolean tienePagos = entity.getCronogramas().stream()
                .anyMatch(c -> c.getEstadoDeuda() == EstadoDeudaReference.PAGADO.getValue());

        if (tienePagos) {
            throw new AppException("No se puede anular una matrícula que ya tiene pagos registrados.");
        }
        
        // No anular si tiene deudas pendientes que ya están vencidas.
        Instant ahora = timeTravelService.getNow(); // Usamos nuestro reloj especial
        boolean tieneDeudasVencidas = entity.getCronogramas().stream()
                .anyMatch(c -> 
                    c.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue() && 
                    c.getFechaVencimiento().isBefore(ahora)
                );
        
        if (tieneDeudasVencidas) {
            throw new AppException("No se puede anular. La matrícula tiene deudas vencidas que deben ser canceladas.");
        }
        // --- FIN DE LA VALIDACIÓN ---

        // Si pasa ambas validaciones, se procede a anular.
        entity.setEstado(EstadoMatriculaReference.ANULADA.getValue());

        for (TCronogramaPagosEntity cronograma : entity.getCronogramas()) {
            if (cronograma.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()) {
                cronograma.setEstadoDeuda(EstadoDeudaReference.ANULADO.getValue());
            }
        }
        matriculasRepository.save(entity);
    }

    // --- NUEVO MÉTODO PARA LA LÓGICA DE 'COMPLETAR' ---
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void completar(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("La matrícula a completar no existe."));

        // Validar que la matrícula no esté anulada o completada
        if (entity.getEstado() == EstadoMatriculaReference.ANULADA.getValue() ||
                entity.getEstado() == EstadoMatriculaReference.COMPLETADA.getValue()) {
            throw new AppException("Esta matrícula ya se encuentra en un estado final.");
        }

        long deudasPendientes = entity.getCronogramas().stream()
                .filter(c -> c.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()).count();
        long deudasPagadas = entity.getCronogramas().stream()
                .filter(c -> c.getEstadoDeuda() == EstadoDeudaReference.PAGADO.getValue()).count();

        // VALIDACIÓN CLAVE: No se puede completar si no hay pagos (todas pendientes)
        if (deudasPagadas == 0 && deudasPendientes > 0) {
            throw new AppException(
                    "No se puede completar una matrícula sin pagos. Para este caso, utilice la opción 'Anular'.");
        }

        // Si hay deudas pendientes (caso de retiro), se anulan.
        if (deudasPendientes > 0) {
            for (TCronogramaPagosEntity cronograma : entity.getCronogramas()) {
                if (cronograma.getEstadoDeuda() == EstadoDeudaReference.PENDIENTE.getValue()) {
                    cronograma.setEstadoDeuda(EstadoDeudaReference.ANULADO.getValue());
                }
            }
        }

        // Se establece el estado final de la matrícula
        entity.setEstado(EstadoMatriculaReference.COMPLETADA.getValue());

        matriculasRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe para eliminar"));
        matriculasRepository.delete(entity);
    }
}