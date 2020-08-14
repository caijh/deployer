package com.github.caijh.deployer.exception;

public class KubectlException extends RuntimeException {

    public KubectlException(Exception e) {
        super(e);
    }

    public KubectlException() {
        super();
    }

}
