package com.github.caijh.deployer.exception;

public class KubectlException extends BizException {

    public KubectlException(Exception e) {
        super(e);
    }

    public KubectlException() {
        super();
    }

}
