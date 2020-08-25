package com.github.caijh.deployer.cmd.kubectl;

import java.io.File;

import com.github.caijh.deployer.config.props.AppsProperties;
import com.github.caijh.deployer.config.props.ClustersProperties;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.model.Cluster;

public class DeleteSubCommand extends KubectlCommand {

    private static final String NAME = "delete";

    public DeleteSubCommand(Cluster cluster, App app) {
        super(cluster, app);
    }

    @Override
    public String[] getCmdArray() {
        return new String[]{
            getCmdName(),
            NAME,
            KubectlOptions.OPTION_FILENAME,
            AppsProperties.appsDir.getPath() + File.separator + getApp().getName() + File.separator + getApp().getRevision(),
            KubectlOptions.OPTION_NAMESPACE,
            getApp().getNamespace(),
            KubectlOptions.OPTION_KUBECONFIG,
            ClustersProperties.getClustersDir().getPath() + File.separator + getCluster().getId() + File.separator + "kubeconfig",
            KubectlOptions.OPTION_IGNORE_NOT_FOUND + "=true"
        };
    }

}
