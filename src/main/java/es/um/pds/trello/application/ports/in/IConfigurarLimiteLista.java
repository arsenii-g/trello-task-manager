package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.ConfigurarLimiteListaRequest;

public interface IConfigurarLimiteLista {
    void ejecutar(ConfigurarLimiteListaRequest request);
}
