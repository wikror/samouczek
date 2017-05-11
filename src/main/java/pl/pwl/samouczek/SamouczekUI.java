package pl.pwl.samouczek;

import javax.servlet.annotation.WebServlet;

import pl.pwl.samouczek.security.AuthenticationService;
import pl.pwl.samouczek.ui.LoginWindow;
import pl.pwl.samouczek.ui.MainWindow;
import pl.pwl.samouczek.ui.PasswordRetrievalWindow;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

@Theme("samouczek")
@SuppressWarnings("serial")
@Push(transport=Transport.LONG_POLLING, value=PushMode.AUTOMATIC)
public class SamouczekUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = true, ui = SamouczekUI.class, widgetset = "pl.pwl.samouczek.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    	
    }

    @Override
	protected void init(VaadinRequest request) {
		/*UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
				
		Table table = new Table();
		BeanItemContainer<UserEntity> userContainer = new BeanItemContainer<UserEntity>(UserEntity.class, userPersistence.loadAll());
		table.setContainerDataSource(userContainer);*/
    	if (request.getPathInfo() != null && request.getPathInfo().startsWith("/password/")) {
    		String token = request.getParameter("token");
    		String user = request.getParameter("user");
    		setContent(new PasswordRetrievalWindow(user, token));
    	} else if (AuthenticationService.isAuthenticated()) {
    		setContent(new MainWindow());
    	} else {
    		setContent(new LoginWindow());
    	}
	}
    
    public MainWindow getMainWindow() {
    	if (getContent() instanceof MainWindow) {
    		return (MainWindow) getContent();
    	} else {
    		return null;
    	}
    }

}
