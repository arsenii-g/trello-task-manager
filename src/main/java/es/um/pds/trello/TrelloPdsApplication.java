package es.um.pds.trello;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.um.pds.trello.infrastructure.javafx.JavafxApplication;
import javafx.application.Application;

@SpringBootApplication
public class TrelloPdsApplication {

	public static void main(String[] args) {
		Application.launch(JavafxApplication.class, args);
	}

}
