package de.vectordata.skynet.jobengine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    Mode value();

    enum Mode {
        NEVER,
        INSTANTLY,
        RECONNECT
    }

}
