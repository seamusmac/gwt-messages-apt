package org.gwtproject.messages;

import java.util.ArrayList;

import com.google.gwt.i18n.client.Dictionary;

public class DictionaryLookup {

	public static String getMessage(MessageKeyArgs keyArgs) {
		Dictionary dictionaryForName = Dictionary.getDictionary(System.getProperty("locale"));
		String message = dictionaryForName.get(keyArgs.key);

		if (keyArgs.args != null)
			return populateStringWithArgs(message, keyArgs.args);

		return message;
	}

	private static String populateStringWithArgs(String propertyValue, Object[] args) {
		ArrayList<String> split = getMatches(propertyValue);
		StringBuilder returnValue = new StringBuilder();

		int j = 0;
		for (int i = 0; i < split.size(); i++) {
			String m = split.get(i);
			boolean isArg = m.startsWith("{");

			if (isArg) {
				returnValue.append(args[j]);
				j++;
			} else
				returnValue.append(m);

		}

		return returnValue.toString();
	}

	private static ArrayList<String> getMatches(String input) {

		ArrayList<String> returnValues = new ArrayList<>();
		int startBraceIndex = input.indexOf("{");
		int i = 0;

		do {
			returnValues.add(input.substring(i, startBraceIndex));
			int endBraceIndex = input.indexOf("}", startBraceIndex) + 1;
			returnValues.add(input.substring(startBraceIndex, endBraceIndex));
			i = endBraceIndex;
			startBraceIndex = input.indexOf("{", endBraceIndex);
			if (startBraceIndex == -1)
				returnValues.add(input.substring(endBraceIndex, input.length()));
		} while (startBraceIndex != -1);

		return returnValues;
	}
}
