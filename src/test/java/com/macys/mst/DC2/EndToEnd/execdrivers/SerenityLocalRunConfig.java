package com.macys.mst.DC2.EndToEnd.execdrivers;

import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.*;
import com.macys.mst.artemis.serenityJbehaveLocal.SerenityLocalTestRunner;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.context.StepsContext;

public class SerenityLocalRunConfig extends SerenityLocalTestRunner {

    @Override
    public InjectableStepsFactory stepsFactory() {
        StepsContext stepsContext = new StepsContext();
        return new InstanceStepsFactory(configuration(),
                new RestSample(),
                new ShipInfoSteps(stepsContext),
                new CreateToteSteps(stepsContext),
                new PrepOptionSteps(stepsContext),
                new ReleaseLaneSteps(stepsContext),
                new PrintTicketSteps(stepsContext),
                new DataCreateModule(),
                new POReleaseReceiptSteps(stepsContext),
                new POSteps(stepsContext),
                new PutToStoreSteps(stepsContext),
                new CommonSteps(),
                new PackAwaySortSteps(stepsContext),
                new POInquiryUISteps(stepsContext),
                new MHESteps(stepsContext),
                new POReleaseUISteps(stepsContext),
                new MHESteps(stepsContext),
                new SortToStoreSteps(stepsContext),
                new PODRRSteps(stepsContext),
                new PODistroSteps(stepsContext),
                new PostDeployValidationSteps(),
                new HAFSteps(),
                new LoadLaneSteps(stepsContext),
                new PickToCartonSteps(stepsContext),
                new HandheldBasicSteps(),
                new PackawayPullSteps(stepsContext),
                new WorkloadPlanningUISteps(),
                new CycleCountSteps(),
                new SupplychainBasicSteps(),
                new InboundPalletizationSteps(stepsContext),
                new WSMManageActivitesSteps(stepsContext),
                new POInquirySteps(stepsContext),
                new AdjustContainerSteps(stepsContext),
                new LocationViewSteps(),
                new ManifestSteps(stepsContext),
                new ASNReceiptsSteps(stepsContext),
                new InventoryAdjustmentInquirySteps(stepsContext),
                new PODashboardSteps(stepsContext),
                new InventoryInquirySteps(stepsContext),
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
                new SmokeTestServicesSteps(),
                new CloseReceiptSteps(stepsContext),
                new DockScanSteps(stepsContext),
                new OBThroughMergeSteps(stepsContext),
                new NetworkMapUISteps(stepsContext),
                new CreateBinSteps(stepsContext),
                new PackawayPullSteps(stepsContext),
                new PackAwaySortSteps(stepsContext),
                new ServicesVersionVerifier()
       );
    }
}

