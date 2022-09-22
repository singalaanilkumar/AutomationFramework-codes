package com.macys.mst.WMSLite.EndToEnd.execdrivers;

import com.macys.mst.WMSLite.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.WMSLite.EndToEnd.stepdefinitions.*;
import com.macys.mst.artemis.serenityJbehaveJira.SerenityJiraTestRunner;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.context.StepsContext;


public class SerenityJiraRunConfig extends SerenityJiraTestRunner {

	@Override
	public InjectableStepsFactory stepsFactory(){
        StepsContext stepsContext = new StepsContext();
        return new InstanceStepsFactory(configuration(),
                new DataCreateModule(),
                new CommonSteps(),
                new OrderCreationSteps(),
                new WmsLiteBasicSteps(),
                new OrderSelectionAndPrintingSteps(stepsContext),
                new BatchEnquarySteps(stepsContext),
                new BatchDetailEnquarySteps(stepsContext)




       );
    }


}
