package pl.pwl.samouczek.ui;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import pl.pwl.samouczek.security.AccountCreationException;
import pl.pwl.samouczek.security.AuthenticationService;

public class CreateAccountWindow extends CenteredWindow {

	private static final long serialVersionUID = 1L;

	private VerticalLayout middleLayer;
	private FormLayout inputBoxes;
	private HorizontalLayout buttonsLayout;

	private TextField userField;

	private Button addAccountButton;
	private Label welcomeLabel;
	private TextField emailField;
	private TextField fullNameField;
	private Button backButton;

	public CreateAccountWindow() {
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
		
		welcomeLabel = new Label("<p align='center'><b>Stwórz konto w systemie</b></p>");
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

		fullNameField = new TextField("Imię i nazwisko: ");
		fullNameField.setImmediate(true);

		inputBoxes.addComponent(fullNameField);
				
		userField = new TextField("Nazwa użytkownika: ");
		userField.setImmediate(true);
		
		inputBoxes.addComponent(userField);

		emailField = new TextField("Email: ");
		emailField.setImmediate(true);
		emailField.addValidator(new EmailValidator("Niepoprawny e-mail"));

		inputBoxes.addComponent(emailField);
		
		addAccountButton = new Button("Utwórz konto");
		
		buttonsLayout.addComponent(addAccountButton);

		backButton = new Button("Powrót do logowania");

		buttonsLayout.addComponent(backButton);

		buttonsLayout.setSpacing(true);
		
		addAccountButton.addClickListener(event -> {
			if (userField.getValue() != null && emailField.getValue() != null && fullNameField.getValue() != null) {
				try {
					AuthenticationService.createNewAccount(userField.getValue(), fullNameField.getValue(), emailField.getValue());
					if (AuthenticationService.sendReminderEmail(userField.getValue())) {
						Notification.show("Pomyślnie stworzono konto i wysłano e-mail do zmiany hasła.", Type.WARNING_MESSAGE);
					} else {
						Notification.show("Pomyślnie stworzono konto, ale nie udało się wysłać maila do zmiany hasła. Użyj opcji 'przypomnij hasło', aby spróbować wysłać mail powtórnie.", Type.ERROR_MESSAGE);
					}
				} catch (AccountCreationException e) {
					Notification.show("Błąd: " + e.getMessage(), Type.ERROR_MESSAGE);
				}
			} else {
				Notification.show("Błąd: musisz podać nazwę użytkownika, imię i nazwisko i poprawnego e-maila.", Type.ERROR_MESSAGE);
			}
		});

		backButton.addClickListener(event -> {
			getUI().setContent(new LoginWindow());
		});
	}
}
