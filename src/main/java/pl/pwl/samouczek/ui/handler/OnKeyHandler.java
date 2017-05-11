package pl.pwl.samouczek.ui.handler;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.TextField;

public abstract class OnKeyHandler {
	
	protected abstract int getKeyCode();

	final ShortcutListener enterShortCut = new ShortcutListener("EnterOnTextAreaShorcut", getKeyCode(), null) {

		private static final long serialVersionUID = 1L;

		@Override
		public void handleAction(Object sender, Object target) {
			onKeyPressed();
		}
	};

	public void installOn(final TextField component) {
		component.addFocusListener(new FieldEvents.FocusListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void focus(FieldEvents.FocusEvent event) {
				component.addShortcutListener(enterShortCut);
			}

		});

		component.addBlurListener(new FieldEvents.BlurListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void blur(FieldEvents.BlurEvent event) {
				component.removeShortcutListener(enterShortCut);
			}

		});
	}

	public abstract void onKeyPressed();

}
