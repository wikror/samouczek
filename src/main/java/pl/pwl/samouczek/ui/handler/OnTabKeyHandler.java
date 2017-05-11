package pl.pwl.samouczek.ui.handler;

import com.vaadin.event.ShortcutAction;

public abstract class OnTabKeyHandler extends OnKeyHandler {

	@Override
	protected int getKeyCode() {
		return ShortcutAction.KeyCode.TAB;
	}
	
	@Override
	public void onKeyPressed() {
		onTabKeyPressed();
	}
	
	public abstract void onTabKeyPressed();

}
