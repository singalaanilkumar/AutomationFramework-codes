package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.Manifest;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.Map;

public class ManifestSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private Manifest Manifestpage = PageFactory.initElements(driver, Manifest.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private StepsContext stepsContext;
    public ManifestSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @Then("User Validates $selectedtab")
    public void validateManifest() {
        String cartonInput="";
        Map<String, List<String>> cartonId = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());
            for (Map.Entry<String, List<String>> entry : cartonId.entrySet()) {
                cartonInput=  entry.getKey();
            }
        dataStorage.getStoredData().put("cartonInput",cartonInput);
        Manifestpage.selectPrinterType("ZM2-T01");
        Manifestpage.selectAttributeValue(cartonInput);
        Manifestpage.clickButton("Search");
        Manifestpage.enterActualWeight("1.5");
        Manifestpage.selectShipVia("UGC");
        Manifestpage.clickButton("MANIFEST");
        Manifestpage.validateManifestScreen();
        }
}

