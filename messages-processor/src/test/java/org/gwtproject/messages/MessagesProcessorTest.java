package org.gwtproject.messages;

import static com.google.common.truth.Truth.assertThat;
import static com.google.testing.compile.Compiler.javac;

import javax.tools.JavaFileObject;

import org.gwtproject.MessagesProcessor;
import org.junit.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compilation.Status;
import com.google.testing.compile.JavaFileObjects;

/**
 * Tests for Messages Processor
 */
public class MessagesProcessorTest {

	private static final JavaFileObject GENERATED_CLASS =
		      JavaFileObjects.forSourceLines(
		          "test.TestClass", //
		          "package test;",
		          "",
		          "public class TestClass {",
		          "  Object field;",
		          "}");
	
	@Test
	public void testGenerated() {
		//
		// Compilation compilation =
		// javac()
		// .withProcessors(new MessagesProcessor())
		//
		// .compile(JavaFileObjects.forResource("org/gwtproject/messages/IMessages.java"));
		//	  assertThat(compilation.status()).isEqualTo(Status.SUCCESS);
		
		
		//MessagesProcessor mp = new MessagesProcessor();
		//mp.process(annotations, roundEnv)generateMessages(messageInterface);
	}
}
