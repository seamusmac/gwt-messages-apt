package org.gwtproject.messages;

public class MaterialLabel extends gwt.material.design.client.ui.MaterialLabel {

	MessageKeyArgs messageKeyArgs;

	public MaterialLabel() {
		super();
	}

	public MaterialLabel(String text) {
		setText(text);
	}
	
	public MaterialLabel(MessageKeyArgs keyArgs) {
		setText(keyArgs);
	}

	public void setText(MessageKeyArgs keyArgs) {
		this.messageKeyArgs = keyArgs;
		super.setText(DictionaryLookup.getMessage(keyArgs));
	}

	public void translate() {
		if (messageKeyArgs != null)
			setText(messageKeyArgs);
	}

}
