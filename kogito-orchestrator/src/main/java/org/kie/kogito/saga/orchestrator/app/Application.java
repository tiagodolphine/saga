package org.kie.kogito.saga.orchestrator.app;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.kie.kogito.Config;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.process.Processes;

@Singleton
public class Application extends StaticApplication {

    @Inject
    public Application(Config config, javax.enterprise.inject.Instance<Processes> processes) /*,
            javax.enterprise.inject.Instance<RuleUnits> ruleUnits,
            javax.enterprise.inject.Instance<DecisionModels> decisionModels,
            java.util.Collection<PredictionModels> predictionModels
            */
    {
        this.config = config;
        this.processes = orNull(processes);
        this.decisionModels = new DecisionModels(this);
        this.predictionModels = new PredictionModels(this);
        if (config().process() != null) {
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
    }

    private static <T> T orNull(javax.enterprise.inject.Instance<T> instance) {
        if (instance.isUnsatisfied()) {
            return null;
        } else {
            return instance.get();
        }
    }
}
