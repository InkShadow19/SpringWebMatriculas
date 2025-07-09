package pe.villaesperanza.SpringWebMatriculas.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.UsuariosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuarios")
public class TUsuariosEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "usuario", length = 100, nullable = false)
    private String usuario;

    @Column(name = "contraseña", nullable = false)
    private String contraseña;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private Instant fechaNacimiento;

    @Column(name = "dni", length = 15)
    private String dni;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @ManyToOne
    @JoinColumn(name = "id_roles")
    private TRolesEntity rolesEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "usuariosEntity")
    private Set<TPagosEntity> pagos = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Esto le dice a Spring Security cuál es el rol del usuario.
        // Asegúrate de que 'rolesEntity' y su descripción no sean nulos.
        if (rolesEntity != null && rolesEntity.getDescripcion() != null) {
            return List.of(new SimpleGrantedAuthority(rolesEntity.getDescripcion()));
        }
        return List.of(); // Devuelve una lista vacía si no hay rol
    }

    @Override
    public String getPassword() {
        return this.contraseña;
    }

    @Override
    public String getUsername() {
        return this.usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // El usuario está habilitado si su estado es ACTIVO (valor 10)
        return this.estado == 10;
    }

    public TUsuariosEntity(UsuariosDto usuarios, TRolesEntity rolesEntity) {

        this.rolesEntity = rolesEntity;
        this.identifier = UUID.randomUUID().toString();
        this.usuario = usuarios.getUsuario();
        this.contraseña = usuarios.getContraseña();
        this.nombres = usuarios.getNombres();
        this.apellidos = usuarios.getApellidos();
        this.fechaNacimiento = Instant.parse(usuarios.getFechaNacimiento());
        this.dni = usuarios.getDni();

        if (usuarios.getPagos() != null)
            this.pagos.addAll(usuarios.getPagos().parallelStream().map(x -> new TPagosEntity(x, this, null)).toList());
    }

    public UsuariosDto toDto() {

        UsuariosDto dto = new UsuariosDto();

        dto.setIdentifier(identifier);
        dto.setUsuario(usuario);
        dto.setContraseña(contraseña);
        dto.setNombres(nombres);
        dto.setApellidos(apellidos);
        dto.setFechaNacimiento(fechaNacimiento.toString());
        dto.setDni(dni);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (rolesEntity != null) dto.setRol(rolesEntity.getIdentifier());

        if (pagos != null)
            dto.setPagos(pagos.stream().map(TPagosEntity::toDto).toList());

        return dto;
    }
}
