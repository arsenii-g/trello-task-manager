package es.um.pds.trello.infrastructure.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("classpath:/fxml/InicioView.fxml")
    private Resource inicioViewResource;

    private final String applicationTitle;
    private final ApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.name:Trello PDS}") String applicationTitle,
                            ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(inicioViewResource.getURL());

            fxmlLoader.setControllerFactory(applicationContext::getBean);

            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();

            stage.setScene(new Scene(parent, 700, 800));
            stage.setTitle(applicationTitle);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("No se ha podido cargar la vista inicial", e);
        }
    }
}