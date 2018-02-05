package org.gwtproject.messages;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;

public class App implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(App.class.getName());

	public void onModuleLoad() {

		LOGGER.info(IMessages.INSTANCE.THREE_STRINGS("12345678", "OLD_CASHBOX", "CB_NAME"));
		
		LOGGER.info(IMessages.INSTANCE.ONE_BIGDECIMAL(BigDecimal.valueOf(1)));
		
		LOGGER.info("OLDDDDDDDDDDDD.... " + OldGwtMessages.INSTANCE.ONE_BIGDECIMAL(BigDecimal.ZERO));

	}
}
