package pl.pwl.samouczek.ui;

import pl.pwl.samouczek.security.AuthenticationService;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginWindow extends CenteredWindow {

	private static final long serialVersionUID = 1L;
	
	private VerticalLayout middleLayer;
	private FormLayout inputBoxes;
	private HorizontalLayout buttonsLayout;

	private TextField userField;
	private PasswordField passwordField;

	private Button loginButton;
	private Button reminderButton;
	private Label welcomeLabel;
	private Button createAccount;

	public LoginWindow() {
		init();
	}

	private void init() {
		setSizeFull();
		
		middleLayer = new VerticalLayout();
		middleLayer.setSizeFull();
		middleLayer.addStyleName("login-panel");
		
		init(middleLayer);
				
		inputBoxes = new FormLayout();
		buttonsLayout = new HorizontalLayout();
		
		inputBoxes.addStyleName("form-centered");
		
		welcomeLabel = new Label("<p align='center'><b>Zaloguj się do Samouczka</b></p>");
		welcomeLabel.addStyleName("caption-label");
		welcomeLabel.setContentMode(ContentMode.HTML);
		
		middleLayer.addComponent(welcomeLabel);
		middleLayer.addComponent(inputBoxes);
		middleLayer.addComponent(buttonsLayout);
		
		middleLayer.setExpandRatio(welcomeLabel, 0.4f);
		middleLayer.setExpandRatio(buttonsLayout, 0.8f);
		
		middleLayer.setComponentAlignment(inputBoxes, Alignment.MIDDLE_CENTER);
		middleLayer.setComponentAlignment(buttonsLayout, Alignment.TOP_CENTER);
		
		middleLayer.setWidth("450px");
		middleLayer.setHeight("300px");
				
		userField = new TextField("Użytkownik: ");
		passwordField = new PasswordField("Hasło: ");

		userField.setImmediate(true);
		passwordField.setImmediate(true);
		
		inputBoxes.addComponent(userField);
		inputBoxes.addComponent(passwordField);
		
		loginButton = new Button("Zaloguj");
		reminderButton = new Button("Zapomniałem/am hasła");
		createAccount = new Button("Załóż konto");
		
		buttonsLayout.addComponent(loginButton);
		buttonsLayout.addComponent(reminderButton);
		buttonsLayout.addComponent(createAccount);
		buttonsLayout.setSpacing(true);
		
		reminderButton.addClickListener(event -> {
			getUI().setContent(new SendReminderWindow());
		});
		
		loginButton.addClickListener(event -> {
			if (AuthenticationService.validateLogin(userField.getValue(), passwordField.getValue())) {
				getUI().setContent(new MainWindow());
			} else {
				Notification.show("Niepoprawne dane logowania.", Type.ERROR_MESSAGE);
			}
		});

		createAccount.addClickListener(event -> {
			getUI().setContent(new CreateAccountWindow());
		});
	}
}
