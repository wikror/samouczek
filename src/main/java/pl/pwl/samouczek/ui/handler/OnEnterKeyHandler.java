package pl.pwl.samouczek.ui.handler;

import com.vaadin.event.ShortcutAction;

public abstract class OnEnterKeyHandler extends OnKeyHandler {

	@Override
	protected int getKeyCode() {
		return ShortcutAction.KeyCode.ENTER;
	}
	
	@Override
	public void onKeyPressed() {
		onEnterKeyPressed();
	}
	
	public abstract void onEnterKeyPressed();

}
