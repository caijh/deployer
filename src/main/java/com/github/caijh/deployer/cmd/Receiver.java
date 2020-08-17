package com.github.caijh.deployer.cmd;

import com.github.caijh.commons.util.process.Command;
import com.github.caijh.commons.util.process.ProcessResult;

public interface Receiver {

    ProcessResult dispatch(Command cmd);

}
