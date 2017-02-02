package gui;

import gui.MainServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("ServerView.fxml"));
			Scene scene = new Scene(root, 600, 400);
			scene.getStylesheets().add("application/style.css");
			primaryStage.setScene(scene);
			primaryStage.setTitle("4InARow - Server");

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					Platform.exit();
					System.exit(0);
				}
			});

			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Hello!");
		if (args.length > 0) {
			if (args[0].equals("-c")) {
				MainServerController msc = new MainServerController();
				msc.initializeConsole();
			} else {
				launch(args);
			}
		}
		else{
			launch(args);
		}
	}
}
