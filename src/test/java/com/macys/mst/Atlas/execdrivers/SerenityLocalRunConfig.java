package com.macys.mst.Atlas.execdrivers;

import com.macys.mst.Atlas.stepdefinitions.*;
import com.macys.mst.artemis.serenityJbehaveLocal.SerenityLocalTestRunner;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.context.StepsContext;

public class SerenityLocalRunConfig extends SerenityLocalTestRunner {

    @Override
    public InjectableStepsFactory stepsFactory() {
        StepsContext stepsContext = new StepsContext();
        return new InstanceStepsFactory(configuration(),
                new HandheldSteps(stepsContext),
                new FetchOracledataSteps(stepsContext),
                new ApolloUISteps(stepsContext),
                new DivertShipSteps(stepsContext),
                new CommonSteps()
       );
    }
}

