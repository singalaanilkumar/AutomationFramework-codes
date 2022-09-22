package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.ContainerInquirySteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContainerInquiry extends BasePage {
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    Logger log = Logger.getLogger(ContainerInquirySteps.class);

    @FindBy(xpath = "//input[@name='containerBarcode']")
    public WebElement containerBarcode;

    @FindBy(xpath = "//*[@id='select-containerType']")
    WebElement containerType;

    @FindBy(xpath = "//*[@id='menu-containerType']//*[@role='listbox']")
    WebElement containerTypeMenu;

    @FindBy(xpath = "//*[@id='select-attributeType']")
    WebElement AttributeType;

    @FindBy(xpath = "//*[@id='menu-attributeType']//*[@role='listbox']")
    WebElement AttributeTypeMenu;

    @FindBy(xpath = "//*[@name='attributeValue']")
    WebElement attributeValue;

    @FindBy(xpath = "//span[text()='SEARCH']")
    WebElement searchButton;

    @FindBy(xpath = "//*[@id=\"gridViewDetails\"]")
    WebElement details;

    @FindBy(xpath = "//*[@id=\"gridViewAttributes\"]")
    WebElement attributes;

    @FindBy(xpath = "//*[@id=\"gridViewRelationships\"]")
    WebElement associations;

    @FindBy(xpath = "//*[@col-id='barCode']")
    private List<WebElement> containerList;

    public void selectContainerType(String ContainerTypeName) {
        try {
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(containerType));
            containerType.click();
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(containerTypeMenu));
            getWait(60);
            jsClick(driver.findElement(
                    By.xpath("//*[@id='menu-containerType']//li[@data-value='" + ContainerTypeName + "']")));
            seUiContextBase.waitFor(2);
            log.info("Selected ContainerType: " + ContainerTypeName);
            StepDetail.addDetail("Selected ContainerType: " + ContainerTypeName, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select ContainerType", false);
            Assert.fail("Unable to select ContainerType");
        }

    }


    public void selectAttributeType(String AttributeTypeName) {

        try {
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(AttributeType));
            AttributeType.click();
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(AttributeTypeMenu));
            jsClick(driver.findElement(
                    By.xpath("//*[@id='menu-attributeType']//li[@data-value='" + AttributeTypeName + "']")));
            seUiContextBase.waitFor(2);
            log.info("Selected AttributeType: " + AttributeTypeName);
            StepDetail.addDetail("Selected AttributeType: " + AttributeTypeName, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select AttributeType", false);
            Assert.fail("Unable to select AttributeType");
        }

    }

    public void selectAttributeValue(String AttributeValueInput) {

        try {

            getWait(60).until(ExpectedConditions.visibilityOf(attributeValue));
            attributeValue.sendKeys(AttributeValueInput);
            log.info("Selected Attribute Value: " + AttributeValueInput);
            StepDetail.addDetail("Selected Attribute Value: " + AttributeValueInput, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select Attribute Value", false);
            Assert.fail("Unable to select attribute Value");
        }

    }

    public void clickSearchButton() {
        seUiContextBase.waitFor(2);
        getWait().until(ExpectedConditions.elementToBeClickable(searchButton)).click();
     }

    public void selectContainer()
    {
         String Container= (String)dataStorage.getStoredData().get("Containervalue");
        boolean containerFound = false;
        for (WebElement containerRow : containerList) {
            if (Container.equals(containerRow.getText())) {
                String rowID = containerRow.findElement(By.xpath("./..")).getAttribute("row-id");
                getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")));
                driver.findElement(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")).click();
                containerFound = true;
                break;
            }
        }
        org.junit.Assert.assertTrue("ContainerID not displayed: "+Container, containerFound);
    }

    public void validateContainerInquiryTabs(String selectedtab)
    {
        String Container= (String)dataStorage.getStoredData().get("Containervalue");
        switch(selectedtab)
        {
            case "DETAILS":
                selectContainer();
                details.click();
                seUiContextBase.waitFor(2);
                List<Map<String, String>> containerInquiryDetailsUI = getGridElementsMap(2,1);
                Map<String, Map<String, String>> detailsUIMap = containerInquiryDetailsUI.stream().collect(Collectors.toMap(map -> map.get("Item"), map -> map));
                log.info("UI Map is" + detailsUIMap.toString());
                List<Map<String, String>> containerInquiryDetailsDB = getDetailsDBData(Container);
                Map<String, Map<String, String>> attributesDBMap = containerInquiryDetailsDB.stream().collect(Collectors.toMap(map -> map.get("Item"), map -> map));
                log.info("DB Map is" + attributesDBMap.toString());
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Details Count validated",
                        "UI Item IDs: " + detailsUIMap.keySet() + " DB Item IDs: " + attributesDBMap.keySet(),
                        detailsUIMap.keySet().equals(attributesDBMap.keySet()));
                for (String ContainerDB : attributesDBMap.keySet()) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Details for Container " + ContainerDB,
                            " DB Details: " + attributesDBMap.get(ContainerDB) + " UI Details: " + detailsUIMap.get(ContainerDB),
                            attributesDBMap.get(ContainerDB).equals(detailsUIMap.get(ContainerDB)));

                }
                break;
            case  "ATTRIBUTES":
                attributes.click();
                List<Map<String, String>> containerInquiryAttributesUI = getGridElementsMap(2,1);
                Map<String, Map<String, String>> contattributesUIMap = containerInquiryAttributesUI.stream().collect(Collectors.toMap(map -> map.get("POReceipt"), map -> map));
                log.info("UI Map is" + contattributesUIMap.toString());
                List<Map<String, String>> containerInquiryattributesDB = getAttributesDBData(Container);
                Map<String, Map<String, String>> contattributesDBMap = containerInquiryattributesDB.stream().collect(Collectors.toMap(map -> map.get("POReceipt"), map -> map));
                log.info("DB Map is" + contattributesDBMap.toString());
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Attributes Count validated",
                        "UI Attribute IDs: " + contattributesUIMap.keySet() + " DB Attribute IDs: " + contattributesDBMap.keySet(),
                        contattributesUIMap.keySet().equals(contattributesDBMap.keySet()));
                for (String ContainerDB : contattributesDBMap.keySet()) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Attributes for Container " + ContainerDB,
                            " DB Details: " + contattributesDBMap.get(ContainerDB) + " UI Details: " + contattributesUIMap.get(ContainerDB),
                            contattributesDBMap.get(ContainerDB).equals(contattributesUIMap.get(ContainerDB)));

                }
                break;
            case "ASSOCIATIONS":
                associations.click();
                List<Map<String, String>> containerInquiryAssociationsUI = getGridElementsMap(2,1);
                Map<String, Map<String, String>> contassociationsUIMap = containerInquiryAssociationsUI.stream().collect(Collectors.toMap(map -> map.get("Child Container"), map -> map));
                log.info("UI Map is" + contassociationsUIMap.toString());
                List<Map<String, String>> containerInquiryassociationsDB = getAssociationsDBData(Container);
                Map<String, Map<String, String>> contassociationsDBMap = containerInquiryassociationsDB.stream().collect(Collectors.toMap(map -> map.get("Child Container"), map -> map));
                log.info("DB Map is" + contassociationsDBMap.toString());
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Associations Count validated",
                        "UI Association IDs: " + contassociationsUIMap.keySet() + " DB Association IDs: " + contassociationsDBMap.keySet(),
                        contassociationsUIMap.keySet().equals(contassociationsDBMap.keySet()));
                for (String ContainerDB : contassociationsDBMap.keySet()) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Container Inquiry Associations for Container " + ContainerDB,
                            " DB Details: " + contassociationsDBMap.get(ContainerDB) + " UI Details: " + contassociationsUIMap.get(ContainerDB),
                            contassociationsDBMap.get(ContainerDB).equals(contassociationsUIMap.get(ContainerDB)));

                }
                break;
        }

    }

    public List<Map<String, String>> getDetailsDBData(String Container)
    {
        List<Map<String, String>> containerInquiryDetails = containerInquiryDetailsMap(Container);
        log.info(containerInquiryDetails.toString());
        return containerInquiryDetails;
    }

    public List<Map<String, String>> containerInquiryDetailsMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.ContainerInquirydetails, Container);
            List<Map<String, String>> containerInquiryDetails = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return containerInquiryDetails;
        }
        catch (Exception e)
        {
            log.error("Exception in containerInquiry", e);
            Assert.fail("Exception in containerInquiry", e);
            return null;
        }
    }

    public List<Map<String, String>> getAttributesDBData(String Container)
    {
        List<Map<String, String>> containerInquiryAttributes = containerInquiryAttributesMap(Container);
        log.info(containerInquiryAttributes.toString());
        return containerInquiryAttributes;
    }

    public List<Map<String, String>> containerInquiryAttributesMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.ContainerInquiryattributes, Container);
            List<Map<String, String>> containerInquiryAttributes = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return containerInquiryAttributes;
        }
        catch (Exception e)
        {
            log.error("Exception in containerInquiryAttributes", e);
            Assert.fail("Exception in containerInquiryAttributes", e);
            return null;
        }
    }

    public List<Map<String, String>> getAssociationsDBData(String Container)
    {
        List<Map<String, String>> containerInquiryAssociations = containerInquiryAssociationsMap(Container);
        log.info(containerInquiryAssociations.toString());
        return containerInquiryAssociations;
    }

    public List<Map<String, String>> containerInquiryAssociationsMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.ContainerInquiryassociations, Container);
            List<Map<String, String>> containerInquiryAssociations = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return containerInquiryAssociations;
        }
        catch (Exception e)
        {
            log.error("Exception in containerInquiryAssociations", e);
            Assert.fail("Exception in containerInquiryAssociations", e);
            return null;
        }
    }

}
