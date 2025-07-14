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
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;
import pe.villaesperanza.SpringWebMatriculas.entity.*;
import pe.villaesperanza.SpringWebMatriculas.repository.*;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto add(MatriculasDto matriculasDto) {

        TNivelesEntity nivel = null;
        TEstudiantesEntity estudiante = null;
        TApoderadosEntity apoderado = null;
        TAniosAcademicosEntity anio = null;
        TGradosEntity grado = null;

        try {
            nivel = nivelesRepository.findByIdentifier(matriculasDto.getNivel()).orElse(null);
            estudiante = estudiantesRepository.findByIdentifier(matriculasDto.getEstudiante()).orElse(null);
            apoderado = apoderadosRepository.findByIdentifier(matriculasDto.getApoderado()).orElse(null);
            anio = aniosAcademicosRepository.findByIdentifier(matriculasDto.getAnioAcademico()).orElse(null);
            grado = gradosRepository.findByIdentifier(matriculasDto.getGrado()).orElse(null);

        } catch (Exception e) {
            log.error("Error inesperado al validar los identificadores: " + e.getMessage());
        }

        TMatriculasEntity entity = new TMatriculasEntity(matriculasDto, nivel, grado, estudiante, apoderado, anio);
        TMatriculasEntity result = matriculasRepository.save(entity);

        return result.toDto();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public MatriculasDto update(String identifier, MatriculasDto nivelesDto) {

        TMatriculasEntity entity = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        entity.update(nivelesDto);
        TMatriculasEntity result = matriculasRepository.save(entity);
        matriculasRepository.save(result);

        return result.toDto();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<MatriculasDto> get(String identifier) {

        TMatriculasEntity result = matriculasRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de esta matricula no existe"));

        return Optional.ofNullable(result.toDto());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<MatriculasDto> getSearch(int page, int size, String codigo, String procedencia, SituacionReference situacion, EstadoReference estado, Instant fechaDesde, Instant fechaHasta) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TMatriculasEntity> pageList =  matriculasRepository.searchMatriculas(codigo, procedencia,
                situacion == null ? null : situacion.getValue(), situacion == null ? null : estado.getValue(), fechaDesde, fechaHasta, pageable);

        return pageList.map(TMatriculasEntity::toDto);
    }
}
