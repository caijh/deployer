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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kubectl {

    public static final String CMD_NAME = "kubectl";
    private static final Logger LOGGER = LoggerFactory.getLogger(Kubectl.class);

    private Kubectl() {

    }

    /**
     * 调用kubectl的命令进行部署.
     *
     * @param subCommand kubectl子命令
     * @param cluster    目标集群
     * @param app        要安装app
     * @return ProcessResult
     */
    public static ProcessResult process(SubCommand subCommand, Cluster cluster, App app) {
        ProcessResult result = new ProcessResult();
        try {
            // TODO: 使用命令模式重写
            String[] cmdArray = new String[]{
                CMD_NAME,
                subCommand.name,
                Options.OPTION_FILENAME,
                AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator + app.getRevision(),
                Options.OPTION_NAMESPACE,
                app.getNamespace(),
                Options.OPTION_KUBECONFIG,
                ClustersProperties.getClustersDir().getPath() + File.separator + cluster.getId() + File.separator + "kubeconfig",
                subCommand == SubCommand.DELETE ? (Options.OPTION_IGNORE_NOT_FOUND + "=true") : "",
            };

            Process process = Runtime.getRuntime().exec(cmdArray);
            process.waitFor();
            int exitValue = process.exitValue();
            String consoleString = readConsoleString(process);
            result.setExitValue(exitValue);
            result.setConsoleString(consoleString);
            LOGGER.info(consoleString);
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

    public enum SubCommand {
        APPLY("apply"),
        DELETE("delete");

        private final String name;

        SubCommand(String name) {
            this.name = name;
        }
    }


    private static class Options {

        static final String OPTION_FILENAME = "-f";
        static final String OPTION_NAMESPACE = "-n";
        static final String OPTION_KUBECONFIG = "--kubeconfig";
        static final String OPTION_IGNORE_NOT_FOUND = "--ignore-not-found";

    }

}
