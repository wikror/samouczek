package pl.pwl.samouczek.ui;

import pl.pwl.samouczek.security.AuthenticationService;



import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

public class SendReminderWindow extends CenteredWindow {

	private static final long serialVersionUID = 1L;
	
	private VerticalLayout middleLayer;
	private FormLayout inputBoxes;
	private HorizontalLayout buttonsLayout;

	private TextField userField;

	private Button reminderButton;
	private Label welcomeLabel;
	private Button backButton;

	public SendReminderWindow() {
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
		
		middleLayer.setWidth("450px");
		middleLayer.setHeight("200px");
				
		userField = new TextField("Użytkownik: ");

		userField.setImmediate(true);
		
		inputBoxes.addComponent(userField);
		
		reminderButton = new Button("Wyślij link do zmiany hasła");
		
		buttonsLayout.addComponent(reminderButton);

		backButton = new Button("Powrót do logowania");

		buttonsLayout.addComponent(backButton);
		buttonsLayout.setSpacing(true);
		
		reminderButton.addClickListener(event -> {
			if (userField.getValue() != null && AuthenticationService.sendReminderEmail(userField.getValue())) {
				Notification.show("Pomyślnie wysłano e-mail do zmiany hasła.", Type.WARNING_MESSAGE);
			} else {
				Notification.show("Błąd: niepoprawna lub pusta nazwa użytkownika!", Type.ERROR_MESSAGE);
			}
		});

		backButton.addClickListener(event -> {
			getUI().setContent(new LoginWindow());
		});
	}
}
