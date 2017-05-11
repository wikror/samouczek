package pl.pwl.samouczek.ui;

import pl.pwl.samouczek.security.AuthenticationService;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;

public class PasswordRetrievalWindow extends CenteredWindow {

	private static final long serialVersionUID = 1L;
	private String user;

	public PasswordRetrievalWindow(String user, String token) {
		if (AuthenticationService.verifyHashedPassword(user, token)) {
			this.user = user;
			init();
		} else {
			Notification.show("Niepoprawne dane do przypomnienia hasła - wygeneruj link ponownie.", Type.ERROR_MESSAGE);
			System.err.print("Próba odzyskiwania hasła z IP " + Page.getCurrent().getWebBrowser().getAddress());
		}
	}
	
	private VerticalLayout middleLayer;
	private FormLayout inputBoxes;
	private HorizontalLayout buttonsLayout;

	private PasswordField passwordField;
	private PasswordField repeatPasswordField;

	private Button changePassButton;
	private Label welcomeLabel; 

	private void init() {
		setSizeFull();
		
		middleLayer = new VerticalLayout();
		middleLayer.setSizeFull();
		middleLayer.addStyleName("login-panel");
		
		init(middleLayer);
				
		inputBoxes = new FormLayout();
		buttonsLayout = new HorizontalLayout();
		
		inputBoxes.addStyleName("form-centered");
		
		welcomeLabel = new Label("<p align='center'><b>Wyślij maila do zmiany hasła</b></p>");
		welcomeLabel.addStyleName("caption-label");
		welcomeLabel.setContentMode(ContentMode.HTML);
		
		middleLayer.addComponent(welcomeLabel);
		middleLayer.addComponent(inputBoxes);
		middleLayer.addComponent(buttonsLayout);
		
		middleLayer.setExpandRatio(welcomeLabel, 0.4f);
		middleLayer.setExpandRatio(buttonsLayout, 0.8f);
		
		middleLayer.setComponentAlignment(inputBoxes, Alignment.MIDDLE_CENTER);
		middleLayer.setComponentAlignment(buttonsLayout, Alignment.TOP_CENTER);
		
		middleLayer.setWidth("400px");
		middleLayer.setHeight("250px");
				
		passwordField = new PasswordField("Wpisz nowe hasło: ");
		passwordField.setImmediate(true);
		
		repeatPasswordField = new PasswordField("Powtórz nowe hasło: ");
		repeatPasswordField.setImmediate(true);

		inputBoxes.addComponent(passwordField);
		inputBoxes.addComponent(repeatPasswordField);
		
		changePassButton = new Button("Zmień hasło");
		
		buttonsLayout.addComponent(changePassButton);


		buttonsLayout.setSpacing(true);
		
		changePassButton.addClickListener(event -> {
			if (passwordField.getValue() != null && passwordField.getValue().equals(repeatPasswordField.getValue()) && AuthenticationService.changePassword(user, passwordField.getValue())) {
				getUI().setContent(new LoginWindow());
				Notification.show("Zmieniono pomyślnie hasło do Samouczka.", Type.WARNING_MESSAGE);
			} else {
				Notification.show("Błąd: puste hasło bądź hasła nie zgadzają się.", Type.ERROR_MESSAGE);
			}
		});


	}


}
