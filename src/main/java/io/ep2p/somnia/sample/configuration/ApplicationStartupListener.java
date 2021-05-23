package io.ep2p.somnia.sample.configuration;

import io.ep2p.somnia.annotation.SomniaDocument;
import io.ep2p.somnia.config.properties.SomniaBaseConfigProperties;
import io.ep2p.somnia.decentralized.SomniaEntityManager;
import io.ep2p.somnia.decentralized.SomniaKademliaSyncRepositoryNode;
import io.ep2p.somnia.model.SomniaEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ApplicationStartupListener {
    private final SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode;


    @Autowired
    public ApplicationStartupListener(SomniaKademliaSyncRepositoryNode somniaKademliaSyncRepositoryNode) {
        this.somniaKademliaSyncRepositoryNode = somniaKademliaSyncRepositoryNode;
    }

    @SneakyThrows
    @EventListener
    public void handleApplicationStartup(ApplicationStartedEvent applicationStartedEvent){
        this.somniaKademliaSyncRepositoryNode.start();
    }
}
