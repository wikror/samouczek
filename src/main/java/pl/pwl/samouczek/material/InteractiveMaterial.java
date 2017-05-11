package pl.pwl.samouczek.material;

import pl.pwl.samouczek.orm.jpa.MaterialEntity;

import com.vaadin.ui.Component;

public interface InteractiveMaterial {
	
	public void init(MaterialEntity material, String configString);
	public Component getLessonComponent();
	
}
