package pl.pwl.samouczek.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public abstract class CenteredWindow extends VerticalLayout {
	
private static final long serialVersionUID = 1L;
	
	private VerticalLayout topSpacer;
	private VerticalLayout bottomSpacer;
	
	private Component middleLayer;

	protected void init(Component middleLayer) {
		setSizeFull();
		
		this.setMiddleLayer(middleLayer);
		middleLayer.setSizeFull();
		middleLayer.addStyleName("login-panel");
		
		topSpacer = new VerticalLayout();
		bottomSpacer = new VerticalLayout();
		
		topSpacer.setSizeFull();
		bottomSpacer.setSizeFull();
		
		addComponent(topSpacer);
		addComponent(middleLayer);
		addComponent(bottomSpacer);
		
		setExpandRatio(topSpacer, 0.5f);
		setExpandRatio(middleLayer, 1.0f);
		setExpandRatio(bottomSpacer, 0.5f);
		
		setComponentAlignment(middleLayer, Alignment.MIDDLE_CENTER);
	}

	public Component getMiddleLayer() {
		return middleLayer;
	}

	public void setMiddleLayer(Component middleLayer) {
		this.middleLayer = middleLayer;
	}

}
