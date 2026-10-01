package es.um.pds.trello.application.ports.out;

import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Email email);
}