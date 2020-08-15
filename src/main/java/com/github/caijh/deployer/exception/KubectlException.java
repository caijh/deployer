package com.github.caijh.deployer.exception;

import com.github.caijh.commons.base.exception.BizRuntimeException;

public class KubectlException extends BizRuntimeException {

    public KubectlException(String message) {
        super(message);
    }

    public KubectlException(Exception e) {
        super(e);
    }

    public KubectlException() {
        super();
    }

}
