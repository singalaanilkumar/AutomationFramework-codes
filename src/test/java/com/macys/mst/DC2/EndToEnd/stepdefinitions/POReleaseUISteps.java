package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardReleasePage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class POReleaseUISteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    PODashboardReleasePage poDashboardReleasePage = PageFactory.initElements(driver, PODashboardReleasePage.class);


    private StepsContext stepsContext;
    public POReleaseUISteps(StepsContext stepsContext) {

        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }
    public long TestNGThreadID = Thread.currentThread().getId();

    @Given("select Checkbox for $operation $numberOfCheckBox UPCs $before TOTE creation")
    public void determineUpcOrderForToteCreation(String operation, String numberOfCheckBox, String when) throws Exception {
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        TimeUnit.SECONDS.sleep(10);
        poDashboardReleasePage.loginSCM();
        TimeUnit.SECONDS.sleep(10);
        poDashboardReleasePage.selectPODashboard();
        log.info("PONUmber using java8: "+poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        poDashboardReleasePage.searchPONbrInDashboard(poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        poDashboardReleasePage.releasePOInDashboard();
        //Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
        log.info("PoLineBarcode size: "+poLinebarCode.size());
        String numberOfUpcs ="";
        if(poLinebarCode.size()==1) numberOfUpcs ="1";
        else if (poLinebarCode.size()==2) numberOfUpcs ="2";
        else if (poLinebarCode.size()==3) numberOfUpcs ="3";
        else numberOfUpcs ="4";
        if(poLinebarCode.size()>5) TimeUnit.SECONDS.sleep(60);
        else TimeUnit.SECONDS.sleep(30);
        log.info("no of UPCs : "+numberOfUpcs);
        poDashboardReleasePage.CheckBoxRelease(numberOfUpcs,numberOfCheckBox,operation);
    }
    
    @Given("select Checkbox for $operation using $SelectAllCheckbox OSCUPCs $after TOTE creation")
    public void releaseOSCPOafterToteCreation(String operation, String SelectAllCheckbox, String after) throws Exception {
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        TimeUnit.SECONDS.sleep(10);
        poDashboardReleasePage.loginSCM();
        TimeUnit.SECONDS.sleep(10);
        poDashboardReleasePage.selectPODashboard();
        log.info("PONUmber using java8: "+poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        poDashboardReleasePage.searchPONbrInDashboard(poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        poDashboardReleasePage.releasePOInDashboard();
        //Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
        log.info("PoLineBarcode size: "+poLinebarCode.size());
        poDashboardReleasePage.CheckAllBoxRelease(SelectAllCheckbox,operation);
    }

}
