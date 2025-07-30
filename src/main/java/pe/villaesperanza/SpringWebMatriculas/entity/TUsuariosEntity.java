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
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

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

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private Instant fechaNacimiento;

    @Column(name = "genero")
    private Integer genero;

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
        // MODIFICADO: Ahora solo concede la autoridad si el rol existe,
        // tiene descripción y su estado es ACTIVO (valor 10).
        if (rolesEntity != null && rolesEntity.getDescripcion() != null && rolesEntity.getEstado() == 10) {
            return List.of(new SimpleGrantedAuthority(rolesEntity.getDescripcion()));
        }
        // Si el rol está inactivo o no existe, no se otorgan permisos.
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.contrasena;
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
        this.contrasena = usuarios.getContrasena();
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
        dto.setContrasena(contrasena);
        dto.setNombres(nombres);
        dto.setApellidos(apellidos);
        dto.setFechaNacimiento(fechaNacimiento.toString());
        dto.setDni(dni);
        if (this.genero != null) {
            dto.setGenero(GeneroReference.fromInt(this.genero));
        }
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (rolesEntity != null)
            dto.setRol(rolesEntity.getIdentifier());

        if (pagos != null)
            dto.setPagos(pagos.stream().map(TPagosEntity::toDto).toList());

        return dto;
    }
}
