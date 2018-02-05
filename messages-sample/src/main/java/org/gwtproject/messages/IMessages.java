package org.gwtproject.messages;

import java.math.BigDecimal;

@Messages(locales = {"en", "fr"}, defaultLocale="en")
public interface IMessages extends com.google.gwt.i18n.client.Messages {
	
	IMessages INSTANCE= IMessages_factory.create();
	
	String SIMPLE();
	String ONE_STRING(String string);
	String TWO_INTS(int min, int max);
	String ONE_BIGDECIMAL(BigDecimal value);
	String THREE_STRINGS(String string1, String string2, String string3);
}
