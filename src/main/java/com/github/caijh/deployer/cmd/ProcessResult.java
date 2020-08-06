package com.github.caijh.deployer.cmd;

import lombok.Data;

@Data
public class ProcessResult {

    private int exitValue;

    private String consoleString;

}
