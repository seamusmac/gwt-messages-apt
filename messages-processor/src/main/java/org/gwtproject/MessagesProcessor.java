package org.gwtproject;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.gwtproject.messages.CouldNotLoadMessagesException;
import org.gwtproject.messages.Messages;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@AutoService(Processor.class)
public class MessagesProcessor extends AbstractProcessor {

	private Messager messager;
	private Filer filer;
	private Elements elementUtils;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(Messages.class).map(Class::getCanonicalName).collect(Collectors.toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.messager = processingEnv.getMessager();
		this.filer = processingEnv.getFiler();
		this.elementUtils = processingEnv.getElementUtils();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			Set<? extends Element> messageElements = roundEnv.getElementsAnnotatedWith(Messages.class);
			messageElements.stream().forEach(this::generateMessages);
		} catch (Exception e) {
			handleError(e);
		}
		return true;
	}

	public void generateMessages(Element element) {

		Messages annotation = element.getAnnotation(Messages.class);
		String[] locales = annotation.locales();
		String filename = element.getSimpleName().toString();

		Map<String, Properties> localePropertiesMap = new HashMap<>();

		Arrays.stream(locales).forEach(l -> {
			Properties properties = new Properties();
			try {
				FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, "",
						elementUtils.getPackageOf(element).toString().replace('.', '/') + "/" + filename + "_" + l
								+ ".properties");

				if (nonNull(fileObject))
					properties.load(fileObject.openInputStream());
				localePropertiesMap.put(l, properties);
			} catch (IOException e) {
				messager.printMessage(Diagnostic.Kind.WARNING, "could not load properties file for locale [" + l + "]",
						element);
			}
		});

		if (!localePropertiesMap.isEmpty()) {
			generateMessagesFiles(element, localePropertiesMap);
		}

	}

	private void generateMessagesFiles(Element element, Map<String, Properties> localePropertiesMap) {
		localePropertiesMap.keySet().forEach(locale -> {
			String className = element.getSimpleName().toString() + "_" + locale;
			TypeSpec.Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)
					.addSuperinterface(TypeName.get(element.asType()));

			element.getEnclosedElements().stream().filter(e -> ElementKind.METHOD.equals(e.getKind())).forEach(m -> {
				ExecutableElement superMethod = (ExecutableElement) m;
				Set<Modifier> modifiers = superMethod.getModifiers().stream()
						.filter(modifier -> !(Modifier.ABSTRACT.equals(modifier) || Modifier.STATIC.equals(modifier)))
						.collect(Collectors.toSet());
				MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(superMethod.getSimpleName().toString())
						.addAnnotation(Override.class).addModifiers(modifiers)

						.returns(TypeName.get(superMethod.getReturnType()));
				if ("java.lang.String[]".equals(superMethod.getReturnType().toString())) {
					builder.addField(FieldSpec.builder(TypeName.get(Map.class), "cache", Modifier.PRIVATE)
							.initializer(CodeBlock.builder().addStatement("new $T()", HashMap.class).build()).build());
				}

				superMethod.getParameters().forEach(e -> {
					methodBuilder.addParameter(TypeName.get(e.asType()), e.getSimpleName().toString());

				});
				String template = localePropertiesMap.get(locale).getProperty(superMethod.getSimpleName().toString());

				methodBuilder.addCode(getValueLiteral(template, superMethod));
				builder.addMethod(methodBuilder.build());
			});

			try {
				JavaFile.builder(elementUtils.getPackageOf(element).toString(), builder.build()).build().writeTo(filer);
			} catch (IOException e) {
				handleError(e);
			}
		});

		generateStaticFactory(localePropertiesMap, element);
	}

	private void generateStaticFactory(Map<String, Properties> localePropertiesMap, Element element) {

		MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create").addModifiers(Modifier.STATIC)
				.returns(TypeName.get(element.asType()));
		CodeBlock.Builder codeBuilder = CodeBlock.builder();

		localePropertiesMap.keySet().forEach(locale -> {
			codeBuilder.beginControlFlow("if(\"" + locale + "\".equals(System.getProperty(\"locale\")))")
					.addStatement("return new $T()", ClassName.bestGuess(elementUtils.getPackageOf(element).toString()
							+ "." + element.getSimpleName().toString() + "_" + locale))
					.endControlFlow();
		});
		Messages messagesAnnotation = element.getAnnotation(Messages.class);
		if (nonNull(messagesAnnotation.defaultLocale()) && !messagesAnnotation.defaultLocale().isEmpty()) {
			codeBuilder.beginControlFlow("if(\"default\".equals(System.getProperty(\"locale\")))")
					.addStatement("return new $T()",
							ClassName.bestGuess(elementUtils.getPackageOf(element).toString() + "."
									+ element.getSimpleName().toString() + "_" + messagesAnnotation.defaultLocale()))
					.endControlFlow();
		}

		codeBuilder.addStatement("throw new $T($L)", CouldNotLoadMessagesException.class,
				"\"No matching implementation for [\"+System.getProperty(\"locale\")+\"] found\"");

		createMethod.addCode(codeBuilder.build());

		TypeSpec.Builder builder = TypeSpec.classBuilder(element.getSimpleName().toString() + "_factory")
				.addMethod(createMethod.build());
		try {
			JavaFile.builder(elementUtils.getPackageOf(element).toString(), builder.build()).build().writeTo(filer);
		} catch (IOException e) {
			handleError(e);
		}

	}

	private CodeBlock getValueLiteral(String propertyValue, ExecutableElement element) {

		String returnTypeString = element.getReturnType().toString();
		if ("boolean".equals(returnTypeString)) {
			return CodeBlock.builder().addStatement("return $L", Boolean.valueOf(propertyValue).toString()).build();
		} else if ("int".equals(returnTypeString) || "float".equals(returnTypeString)
				|| "double".equals(returnTypeString)) {
			return CodeBlock.builder().addStatement("return $L", propertyValue).build();
		} else if ("java.lang.String".equals(returnTypeString)) {
			return populateString(propertyValue, element);
		} else if ("org.gwtproject.messages.MessageKeyArgs".equals(returnTypeString)) {
			return populateMessageKeyArgs(propertyValue, element);
		} else if ("java.lang.String[]".equals(returnTypeString)) {
			return CodeBlock.builder()
					.addStatement("$T args[] = ($T[]) cache.get(\"$L\")", String.class, String.class,
							element.getSimpleName().toString())
					.beginControlFlow("if (args == null)")
					.addStatement("$T[] writer = {$L}", String.class, asStringArrayElements(propertyValue))
					.addStatement("cache.put(\"stringArray\", writer)").addStatement("return writer")
					.nextControlFlow("else").addStatement("return args").endControlFlow().build();
		} else {
			return CodeBlock.builder().addStatement("return $L", propertyValue).build();
		}
	}

	private CodeBlock populateString(String propertyValue, ExecutableElement element) {
		String[] split = propertyValue.split("((?<=(\\{\\d\\}))|(?=(\\{\\d\\})))");

		StringBuilder returnValue = new StringBuilder();

		for (int i = 0; i < split.length; i++) {
			String m = split[i];
			boolean isLast = i == split.length - 1;
			boolean isArg = m.startsWith("{");

			if (isArg) {
				int index = Integer.valueOf(m.substring(1, m.indexOf("}")));
				if (i == 0 && isLast)
					returnValue.append(element.getParameters().get(index));
				else if (i == 0 && !isLast)
					returnValue.append(element.getParameters().get(index)).append(" + \"");
				else if (isArg && i != 0 && !isLast)
					returnValue.append("\" + ").append(element.getParameters().get(index)).append(" + \"");
				else if (i == 0)
					returnValue.append(element.getParameters().get(index)).append(" + \"");
				else if (isLast)
					returnValue.append("\" + ").append(element.getParameters().get(index));

			} else if (i == 0 && split.length == 1)
				returnValue.append("\"" + m).append("\"");
			else if (i == 0)
				returnValue.append("\"" + m);
			else if (isLast)
				returnValue.append(m + "\"");
			else
				returnValue.append(m);
		}
		return CodeBlock.builder().addStatement("return $L", returnValue.toString()).build();

	}

	private CodeBlock populateMessageKeyArgs(String propertyValue, ExecutableElement element) {

		Builder builder = CodeBlock.builder();

		if (element.getParameters().size() > 0) {
			StringBuilder params = new StringBuilder();
			element.getParameters().forEach(p -> params.append(p.getSimpleName().toString() + ","));
			params.deleteCharAt(params.length() - 1);
			builder.addStatement("return new MessageKeyArgs(\"" + element.getSimpleName().toString() + "\", $T.asList("
					+ params + ").toArray())", Arrays.class);
			return builder.build();
		}

		return builder.add("return new MessageKeyArgs(\"" + element.getSimpleName().toString() + "\", null );").build();
	}

	private String asStringArrayElements(String propertyValue) {
		return Arrays.stream(propertyValue.split(",")).map(s -> ("\"" + s + "\"")).collect(Collectors.joining(","));
	}

	private void handleError(Exception e) {
		StringWriter out = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		messager.printMessage(Diagnostic.Kind.ERROR, "error while creating source file " + out.getBuffer().toString());
	}
}
