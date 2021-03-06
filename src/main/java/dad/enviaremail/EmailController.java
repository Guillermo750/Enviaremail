package dad.enviaremail;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmailController {
	
	// model
	
	private StringProperty nombre = new SimpleStringProperty();
	private StringProperty puerto = new SimpleStringProperty();
	private BooleanProperty conexion = new SimpleBooleanProperty();
	private StringProperty emailfr = new SimpleStringProperty();
	private StringProperty contrasena = new SimpleStringProperty();
	private StringProperty emailrem = new SimpleStringProperty();
	private StringProperty asunto = new SimpleStringProperty();
	private StringProperty mensaje = new SimpleStringProperty();

	private Task<Void> tarea;

	// view
	
	@FXML
	private TextField asuntoText;

	@FXML
	private VBox botonBox;

	@FXML
	private Button cerrarButton;

	@FXML
	private PasswordField contrasenaText;

	@FXML
	private TextField destinatarioText;

	@FXML
	private Button enviarButton;

	@FXML
	private TextArea mensajeText;

	@FXML
	private TextField puertoText;

	@FXML
	private TextField remitenteText;

	@FXML
	private TextField servidorText;

	@FXML
	private CheckBox conexionCheck;

	@FXML
	private Button vaciadoButton;

	@FXML
	private GridPane view;

	public EmailController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/email.fxml"));
		loader.setController(this);
		loader.load();
	}

	public void initialize(URL location, ResourceBundle resources) {

		nombre.bind(servidorText.textProperty());
		puerto.bind(puertoText.textProperty());
		conexion.bind(conexionCheck.selectedProperty());
		emailfr.bind(remitenteText.textProperty());
		contrasena.bind(contrasenaText.textProperty());
		emailrem.bind(destinatarioText.textProperty());
		asunto.bind(asuntoText.textProperty());
		mensaje.bind(mensajeText.textProperty());

	}

	public GridPane getView() {
		return view;
	}

	@FXML
	void onCerrarButton(ActionEvent event) {

		EmailApp.getPrimaryStage().close();
	}

	@FXML
	void onEnviarButton(ActionEvent event) {

		tarea = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				int puertoS = Integer.parseInt(puerto.get());
				Email email = new SimpleEmail();
				email.setHostName(nombre.get());
				email.setSmtpPort(puertoS);
				email.setAuthenticator(new DefaultAuthenticator(emailfr.get(), contrasena.get()));
				email.setSSLOnConnect(conexion.get());
				email.setFrom(emailfr.get());
				email.setSubject(asunto.get());
				email.setMsg(mensaje.get());
				email.addTo(emailrem.get());

				email.send();
				return null;
			}
		};

		tarea.setOnSucceeded(xmen -> {
			enviarButton.disableProperty().bind(tarea.runningProperty());

			Alert alert1 = new Alert(AlertType.INFORMATION);
			alert1.setTitle("Mensaje enviado");
			alert1.setHeaderText("Mensaje enviado con ??xito a: '" + emailrem.get() + "'");
			alert1.initOwner(view.getScene().getWindow());
			alert1.showAndWait();

			asuntoText.clear();
			mensajeText.clear();
			destinatarioText.clear();
		});

		tarea.setOnFailed(xmen ->{
			Alert alert2 = new Alert(AlertType.ERROR);
			alert2.setContentText("Invalid message supplied");
			alert2.setTitle("Error");
			alert2.setHeaderText("No se pudo enviar el email");
			alert2.initOwner(view.getScene().getWindow());
			alert2.showAndWait();
		});

		new Thread(tarea).start();
	}

	@FXML
	void onVaciarButton(ActionEvent event) {
		
		servidorText.clear();
    	puertoText.clear();
    	conexionCheck.setSelected(false);
    	remitenteText.clear();
    	contrasenaText.clear();
    	destinatarioText.clear();
		asuntoText.clear();
		mensajeText.clear();
	}

}
