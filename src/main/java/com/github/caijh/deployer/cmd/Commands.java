package com.github.caijh.deployer.cmd;

import java.util.HashMap;
import java.util.Map;

public class Commands {

    private static final Map<String, Invoker> INVOKERS = new HashMap<>();

    static {
        INVOKERS.put(Kubectl.CMD_NAME, Kubectl.getInstance());
    }

    private Commands() {

    }

    public static Invoker getInvoker(String invoker) {
        return INVOKERS.get(invoker);
    }

}
