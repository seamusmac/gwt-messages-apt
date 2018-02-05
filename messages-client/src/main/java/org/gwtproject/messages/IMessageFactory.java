package org.gwtproject.messages;

public class IMessageFactory {

	static IMessages INSTANCE = IMessages_factory.create();

	public IMessages getINSTANCE() {
		return INSTANCE;
	}
}
