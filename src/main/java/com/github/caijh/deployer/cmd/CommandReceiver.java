package com.github.caijh.deployer.cmd;

import com.github.caijh.commons.util.process.Command;
import com.github.caijh.commons.util.process.ProcessResult;

public class CommandReceiver implements Receiver {

    private static final Receiver receiver = new CommandReceiver();

    public static Receiver getInstance() {
        return receiver;
    }

    @Override
    public ProcessResult dispatch(Command cmd) {
        Invoker invoker = Commands.getInvoker(cmd.getCmdName());
        return invoker.exec(cmd);
    }

}
