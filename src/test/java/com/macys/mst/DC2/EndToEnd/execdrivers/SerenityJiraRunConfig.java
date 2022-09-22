package com.macys.mst.DC2.EndToEnd.execdrivers;

import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;

import com.macys.mst.DC2.EndToEnd.stepdefinitions.*;

import com.macys.mst.artemis.serenityJbehaveJira.SerenityJiraTestRunner;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.context.StepsContext;


public class SerenityJiraRunConfig extends SerenityJiraTestRunner {

	@Override
	public InjectableStepsFactory stepsFactory(){
        StepsContext stepsContext = new StepsContext();
        return new InstanceStepsFactory(configuration(),
                new RestSample(),
                new ShipInfoSteps(stepsContext),
                new CreateToteSteps(stepsContext),
                new POReleaseReceiptSteps(stepsContext),
                new PrepOptionSteps(stepsContext),
                new ReleaseLaneSteps(stepsContext),
                new PrintTicketSteps(stepsContext),
                new DataCreateModule(),
                new PutToStoreSteps(stepsContext),
                new CommonSteps(),
                new PackAwaySortSteps(stepsContext),
                new POInquiryUISteps(stepsContext),
                new POReleaseUISteps(stepsContext),
                new MHESteps(stepsContext),
                new SortToStoreSteps(stepsContext),
                new PODRRSteps(stepsContext),
                new PODistroSteps(stepsContext),
                new LoadLaneSteps(stepsContext),
                new PickToCartonSteps(stepsContext),
                new HAFSteps(),
                new HandheldBasicSteps(),
                new PackawayPullSteps(stepsContext),
                new WorkloadPlanningUISteps(),
                new SupplychainBasicSteps(),
                new InboundPalletizationSteps(stepsContext),
                new WSMManageActivitesSteps(stepsContext),
                new POInquirySteps(stepsContext),
                new InventoryInquirySteps(stepsContext),
                new AdjustContainerSteps(stepsContext),
                new InventoryAdjustmentInquirySteps(stepsContext),
                new LocationViewSteps(),
                new ManifestSteps(stepsContext),
                new ASNReceiptsSteps(stepsContext),
                new ContainerInquirySteps(stepsContext),
                new WSMManageActivitesSteps(stepsContext),
                new SplitMoveSteps(stepsContext),
                new SplitAdjustPalletSteps(stepsContext),
                new ConsumeContainerSteps(stepsContext),
                new MovePalletSteps(stepsContext),
                new LocateContainerSteps(stepsContext),
                new ExceptionalLaneSteps(stepsContext),
                new MagicToteSteps(stepsContext),
                new WaveDashboardSteps(stepsContext),
                new SupportUISteps(stepsContext),
                new DockScanSteps(stepsContext),
                new CreateBinSteps(stepsContext),
                new PackawayPullSteps(stepsContext),
                new PackAwaySortSteps(stepsContext)

       );
    }


}
