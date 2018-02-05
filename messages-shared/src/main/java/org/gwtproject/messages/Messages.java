package org.gwtproject.messages;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Messages {
    String[] locales() default {};
    String defaultLocale() default "";
}
