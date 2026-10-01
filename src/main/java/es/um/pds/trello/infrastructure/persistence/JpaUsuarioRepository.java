package es.um.pds.trello.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.domain.model.Email;

public interface JpaUsuarioRepository extends JpaRepository<Usuario, Email> {

}