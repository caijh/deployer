package com.github.caijh.deployer.cmd.kubectl;

import com.github.caijh.commons.util.process.Command;
import com.github.caijh.commons.util.process.ProcessResult;
import com.github.caijh.deployer.cmd.Invoker;

public class Kubectl implements Invoker {

    public static final String CMD_NAME = "kubectl";

    private static final Kubectl INSTANCE = new Kubectl();

    public static Kubectl getInstance() {
        return INSTANCE;
    }

    private Kubectl() {
    }

    @Override
    public String name() {
        return CMD_NAME;
    }

    @Override
    public ProcessResult exec(Command cmd) {
        return cmd.exec();
    }

}
