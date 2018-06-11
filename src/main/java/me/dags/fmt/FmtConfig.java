package me.dags.fmt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dags <dags@dags.me>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FmtConfig {

    String[] info() default {"white"};

    String[] subdued() default {"yellow", "italic"};

    String[] stress() default {"dark_aqua"};

    String[] error() default {"gray"};

    String[] warn() default {"red"};
}
