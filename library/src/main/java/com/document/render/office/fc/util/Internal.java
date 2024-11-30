

package com.document.render.office.fc.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal {
    String value() default "";
}
