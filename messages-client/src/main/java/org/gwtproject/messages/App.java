package org.gwtproject.messages;

import java.math.BigDecimal;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint {

	public void onModuleLoad() {

		RootPanel.get().add(new MaterialLabel(IMessageFactory.INSTANCE.ONE_BIGDECIMAL(BigDecimal.valueOf(1.00))));
		RootPanel.get().add(new TestComponent());
		RootPanel.get().add(new MaterialLabel(IMessageFactory.INSTANCE.THREE_STRINGS("Road", "cat", "dog")));
		RootPanel.get().add(new MaterialLabel(IMessageFactory.INSTANCE.ONE_STRING("very nice")));
		RootPanel.get().add(new MaterialLabel(IMessageFactory.INSTANCE.DOUBLE_STRING("very nice ", "&GRAND")));
	}
}
