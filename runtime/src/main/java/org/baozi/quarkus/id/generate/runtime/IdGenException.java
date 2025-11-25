package org.baozi.quarkus.id.generate.runtime;

import java.io.Serial;

public class IdGenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5501841590599021153L;

    public IdGenException(String message) {
        super(message);
    }
}
