package com.github.caijh.deployer.cmd;

import lombok.Data;

/**
 * 调用命令的执行结果.
 */
@Data
public class ProcessResult {

    private int exitValue = -1;

    private String consoleString;

    public boolean isSuccessful() {
        return exitValue == 0;
    }

}
