package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.ConfigurarWorkflowRequest;

public interface IConfigurarWorkflow {

    void ejecutar(ConfigurarWorkflowRequest request);
}
