package com.github.caijh.deployer.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.github.caijh.deployer.config.props.AppsProperties;
import com.github.caijh.deployer.config.props.ClustersProperties;
import com.github.caijh.deployer.exception.KubectlException;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.model.Cluster;

public class Kubectl {

    private Kubectl() {

    }

    public static ProcessResult apply(Cluster cluster, App app) {
        ProcessResult result = new ProcessResult();
        try {
            String[] cmdArray = new String[]{
                "kubectl",
                SubCommand.CMD_APPLY,
                Options.OPTION_FILENAME,
                AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator + app.getRevision(),
                Options.OPTION_NAMESPACE,
                app.getNamespace(),
                Options.OPTION_KUBECONFIG,
                ClustersProperties.getClustersDir().getPath() + File.separator + cluster.getId() + File.separator + "kubeconfig"
            };

            Process process = Runtime.getRuntime().exec(cmdArray);
            process.waitFor();
            int exitValue = process.exitValue();
            String consoleString = readConsoleString(process);
            result.setExitValue(exitValue);
            result.setConsoleString(consoleString);
        } catch (Exception e) {
            throw new KubectlException(e);
        }
        return result;
    }

    private static String readConsoleString(Process process) throws IOException {
        StringBuilder console = new StringBuilder();
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader bufferedError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

        String line;
        while ((line = bufferIn.readLine()) != null || (line = bufferedError.readLine()) != null) {
            console.append(line).append('\n');
        }
        return console.toString();
    }

    private static class SubCommand {

        static final String CMD_APPLY = "apply";

    }

    private static class Options {

        static final String OPTION_FILENAME = "-f";
        static final String OPTION_NAMESPACE = "-n";
        static final String OPTION_KUBECONFIG = "--kubeconfig";

    }

}
