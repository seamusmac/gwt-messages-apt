package org.gwtproject.messages;

import java.math.BigDecimal;

import com.google.gwt.core.client.GWT;

public interface OldGwtMessages extends com.google.gwt.i18n.client.Messages {

	OldGwtMessages INSTANCE= GWT.create(OldGwtMessages.class);
	
	String ONE_STRING(String string);
	String TWO_INTS(int min, int max);
	String ONE_BIGDECIMAL(BigDecimal value);
	String THREE_STRINGS(String serialNumber, String cashBoxType, String cashBoxName);
}
