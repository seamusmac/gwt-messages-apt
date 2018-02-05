package org.gwtproject.messages;

import java.math.BigDecimal;

@Messages(locales = {"en", "fr"}, defaultLocale="en")
public interface IMessages extends com.google.gwt.i18n.client.Messages {
	
	String SIMPLE();
	MessageKeyArgs ONE_STRING(String string);
	MessageKeyArgs DOUBLE_STRING(String arg1, String arg2);
	String TWO_INTS(int min, int max);
	MessageKeyArgs ONE_BIGDECIMAL(BigDecimal value);
	MessageKeyArgs THREE_STRINGS(String val1, String val2, String val3);
}
