package com.github.caijh.deployer.cmd;

import com.github.caijh.commons.util.process.Command;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.model.Cluster;

public abstract class KubectlCommand implements Command {

    private final Cluster cluster;
    private final App app;

    public KubectlCommand(Cluster cluster, App app) {
        this.cluster = cluster;
        this.app = app;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public App getApp() {
        return app;
    }

    @Override
    public String getCmdName() {
        return Kubectl.CMD_NAME;
    }

}
