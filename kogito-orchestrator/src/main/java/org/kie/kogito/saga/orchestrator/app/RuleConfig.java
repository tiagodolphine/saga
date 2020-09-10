package org.kie.kogito.saga.orchestrator.app;

import javax.enterprise.inject.Instance;

import org.drools.core.config.AbstractRuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.rules.RuleEventListenerConfig;

@javax.inject.Singleton
class RuleConfig extends AbstractRuleConfig {

    @javax.inject.Inject
    public RuleConfig(Instance<RuleEventListenerConfig> ruleEventListenerConfigs, Instance<AgendaEventListener> agendaEventListeners, Instance<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        super(ruleEventListenerConfigs, agendaEventListeners, ruleRuntimeEventListeners);
    }
}
