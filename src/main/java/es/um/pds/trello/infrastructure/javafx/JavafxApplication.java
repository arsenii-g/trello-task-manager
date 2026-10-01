package es.um.pds.trello.infrastructure.javafx;

import es.um.pds.trello.TrelloPdsApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavafxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {

        this.context = new SpringApplicationBuilder()
                .sources(TrelloPdsApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {

        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {

        this.context.close();
        Platform.exit();
    }
}