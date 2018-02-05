package org.gwtproject.messages;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class TestComponent extends Composite implements HasWidgets {

	private static TestComponentUiBinder uiBinder = GWT.create(TestComponentUiBinder.class);

	interface TestComponentUiBinder extends UiBinder<Widget, TestComponent> {
	}

	public TestComponent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void add(Widget w) {

	}

	@Override
	public void clear() {

	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

}
