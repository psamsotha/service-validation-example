package com.example;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;


public class ValidatedFilter implements Filter {

    @Override
    public boolean matches(Descriptor descriptor) {
        try {
            return Class.forName(descriptor.getImplementation())
                    .isAnnotationPresent(Validated.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
