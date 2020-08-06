package com.github.caijh.deployer.exception;

public class BizException extends RuntimeException {

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(Exception e) {
        super(e);
    }

}
