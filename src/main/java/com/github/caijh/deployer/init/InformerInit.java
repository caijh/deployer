package com.github.caijh.deployer.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.caijh.commons.util.CollectionUtils;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.repository.ClusterRepository;
import com.github.caijh.deployer.service.ClusterService;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InformerInit {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, SharedInformerFactory> sharedInformerFactoryMap = new HashMap<>();
    @Inject
    private ClusterRepository clusterRepository;
    @Inject
    private ClusterService clusterService;

    @PostConstruct
    public void init() {
        List<Cluster> clusters = clusterRepository.findAll();

        if (CollectionUtils.isEmpty(clusters)) {
            return;
        }

        clusters.forEach(this::initClusterInformer);
    }


    public void initClusterInformer(Cluster cluster) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(cluster);
        SharedInformerFactory sharedInformerFactory = kubernetesClient.informers();
        sharedInformerFactoryMap.put(cluster.getId(), sharedInformerFactory);
        logger.info("Cluster {} Informer factory initialized.", cluster.getName());

        createNodeInformer(sharedInformerFactory);

        createPodInformer(sharedInformerFactory);

        sharedInformerFactory.startAllRegisteredInformers();
        logger.info("Cluster {} Starting all registered informers", cluster.getName());
    }

    private void createNodeInformer(SharedInformerFactory sharedInformerFactory) {
        SharedIndexInformer<Node> informer = sharedInformerFactory.sharedIndexInformerFor(Node.class, NodeList.class, 30 * 1000L);
        informer.addEventHandler(new ResourceEventHandler<Node>() {
            @Override
            public void onAdd(Node obj) {
                logger.info("node: {} added to cluster: {}", obj.getMetadata().getName(), obj.getMetadata().getClusterName());
            }

            @Override
            public void onUpdate(Node oldObj, Node newObj) {
                logger.info("node: {} in cluster: {} updated", oldObj.getMetadata().getName(), oldObj.getMetadata().getClusterName());
            }

            @Override
            public void onDelete(Node obj, boolean deletedFinalStateUnknown) {
                logger.info("node: {} in cluster: {} deleted", obj.getMetadata().getName(), obj.getMetadata().getClusterName());
            }
        });
    }

    private void createPodInformer(SharedInformerFactory sharedInformerFactory) {
        SharedIndexInformer<Pod> informer = sharedInformerFactory.sharedIndexInformerFor(Pod.class, PodList.class, 30 * 1000L);

        informer.addEventHandler(
            new ResourceEventHandler<Pod>() {
                @Override
                public void onAdd(Pod pod) {
                    logger.info("{} pod added", pod.getMetadata().getName());
                }

                @Override
                public void onUpdate(Pod oldPod, Pod newPod) {
                    logger.info("{} pod updated", oldPod.getMetadata().getName());
                }

                @Override
                public void onDelete(Pod pod, boolean deletedFinalStateUnknown) {
                    logger.info("{} pod deleted", pod.getMetadata().getName());
                }
            }
        );


    }

}
