package es.um.pds.trello.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.Email;
import java.util.Optional;

@Repository
public class UsuarioRepositorio implements UsuarioRepository {

    private final JpaUsuarioRepository jpa;

    public UsuarioRepositorio(JpaUsuarioRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return jpa.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Email email) {
        return jpa.findById(email);
    }
}