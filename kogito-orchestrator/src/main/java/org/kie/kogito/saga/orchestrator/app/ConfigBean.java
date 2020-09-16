package org.kie.kogito.saga.orchestrator.app;

@javax.inject.Singleton
public class ConfigBean extends org.kie.kogito.conf.StaticConfigBean {

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.service.url")
    java.util.Optional<String> kogitoService;

    @javax.annotation.PostConstruct
    protected void init() {
        setServiceUrl(kogitoService.orElse(""));
    }
}
