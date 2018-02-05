package org.gwtproject.messages;

import java.math.BigDecimal;

@Messages(locales = {"en"})
public interface IMessages extends com.google.gwt.i18n.client.Messages {
	
	String REVERSE_WITH_CASH_DEBIT_SUCCESS(String transactionId);
	String INVALID_PASSWORD_LENGTH(int min, int max);
	String MAX_DEDICATED_SAVINGS_INITIAL_DEPOSIT_ERROR(BigDecimal value);
	String DUPLICATE_BAITS_NOT_ADDED(String serialNumber, String cashBoxType, String cashBoxName);
}
