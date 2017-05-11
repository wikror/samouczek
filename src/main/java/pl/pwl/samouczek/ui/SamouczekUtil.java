package pl.pwl.samouczek.ui;

import pl.pwl.samouczek.SamouczekUI;
import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.orm.jpa.UserEntity;
import pl.pwl.samouczek.persistence.PersistenceServiceProvider;
import pl.pwl.samouczek.persistence.services.UserPersistence;
import pl.pwl.samouczek.security.AuthenticationService;

import com.vaadin.ui.UI;

public class SamouczekUtil {
	
	public static void refreshLessonsList() {
		SamouczekUI ui = (SamouczekUI) UI.getCurrent();
		MainWindow mainWindow = ui.getMainWindow();
		if (mainWindow != null) {
			mainWindow.initMenu();
		}
	}
	
	public static void markMaterialAsRealized(MaterialEntity material) {
		UserEntity currentLogin = AuthenticationService.getCurrentLoginInfo();
		if (currentLogin != null && material.getNext() != null) {
			MaterialEntity nextMaterial = material.getNext();
			if (!currentLogin.getMaterials().contains(nextMaterial)) {				
				currentLogin.getMaterials().add(nextMaterial);
			}
			UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
			userPersistence.save(currentLogin);
		}
		refreshLessonsList();
	}

}
