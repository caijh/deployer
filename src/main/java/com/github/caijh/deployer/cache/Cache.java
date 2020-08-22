package com.github.caijh.deployer.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;

public class Cache {

    public static final String CLUSTER_ID = "cluster/id";

    private Cache() {

    }

    public static class Kubernetes {

        private static final Map<String, Map<String, Node>> CLUSTER_NODES = new ConcurrentHashMap<>();
        private static final Map<String, Map<String, Pod>> APP_PODS = new ConcurrentHashMap<>();

        private Kubernetes() {

        }

        public static void add(KubernetesResource obj) {

            if (obj instanceof Node) {
                Node node = (Node) obj;
                String clusterId = node.getMetadata().getLabels().get(CLUSTER_ID);
                if (clusterId != null) {
                    CLUSTER_NODES.computeIfAbsent(clusterId, s -> new ConcurrentHashMap<>()).put(node.getMetadata().getName(), node);
                }
            }

            if (obj instanceof Pod) {
                Pod pod = (Pod) obj;
                String appId = pod.getMetadata().getLabels().get("appId");
                if (appId != null) {
                    APP_PODS.computeIfAbsent(appId, s -> new ConcurrentHashMap<>()).put(pod.getMetadata().getName(), pod);
                }
            }

        }

        public static List<Node> getNodeList(String clusterId) {
            List<Node> nodes = new ArrayList<>();
            Map<String, Node> nodesMap = CLUSTER_NODES.get(clusterId);

            if (nodesMap != null && !nodesMap.isEmpty()) {
                nodesMap.forEach((key, value) -> nodes.add(value));
            }

            return nodes;
        }

        public static void update(KubernetesResource newObj) {
            if (newObj instanceof Node) {
                Node node = (Node) newObj;
                String clusterId = node.getMetadata().getLabels().get(CLUSTER_ID);
                CLUSTER_NODES.get(clusterId).put(node.getMetadata().getName(), node);
            }

            if (newObj instanceof Pod) {
                Pod pod = (Pod) newObj;
                String appId = pod.getMetadata().getLabels().get("app");
                if (appId != null) {
                    APP_PODS.get(appId).put(pod.getMetadata().getName(), pod);
                }
            }
        }

        public static void delete(KubernetesResource obj) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                String clusterId = node.getMetadata().getLabels().get(CLUSTER_ID);
                CLUSTER_NODES.get(clusterId).remove(node.getMetadata().getName());
            }

            if (obj instanceof Pod) {
                Pod pod = (Pod) obj;
                APP_PODS.get(pod.getMetadata().getLabels().get("app")).remove(pod.getMetadata().getName());
            }
        }

    }

}
