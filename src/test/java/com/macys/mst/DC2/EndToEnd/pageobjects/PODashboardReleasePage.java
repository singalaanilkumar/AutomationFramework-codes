package com.macys.mst.DC2.EndToEnd.pageobjects;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class PODashboardReleasePage extends BasePage {

    public PODashboardReleasePage(WebDriver driver) {
        super(driver);
    }

    public static LinkedList<Long> UPCsForRelease = new LinkedList<Long>();

    @FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
    private WebElement poDashboard;

    @FindBy(xpath = "//span[contains(text(),'Logout')]")
    private WebElement logout;

    @FindBy(xpath = "//span[contains(text(),'CLEAR')]")
    private WebElement clearButton;

    @FindBy(xpath = "//*[@id='searchBox']")
    private WebElement searchBox;

    @FindBy(xpath = "//*[contains(@id,'select')]")
    private WebElement selectDropdown;

    @FindBy(xpath = "div[(@col-id='oscUnits')]//div[@class='div-percent-bar']")
    private WebElement oscUnits;

    @FindBy(xpath = "//button[(@ref='btNext')]")
    private WebElement btNextButton;

    private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

    @FindBy(xpath = "//button[(@id = 'searchButton')]")
    private WebElement searchButton;

    @FindBy(xpath = "//span[(text()='Release')]")
    private WebElement releaseButton;

    @FindBy(xpath = "//button[@id='gridViewRelationships']")
    private WebElement innerReleaseButton;

    @FindBy(xpath = "//span[contains(text(),'UNDO RELEASE')]")
    private WebElement undoReleaseButton;

    @FindBy(xpath = "//span[contains(text(),'PO NBR')]")
    private WebElement poNbr;

    @FindBy(xpath = "//span[contains(text(),'PO NBR')]")
    private WebElement firstUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first ag-row-last']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement OneRecordFirstElementCheckBox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first ag-row-last']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    private WebElement OneRecordFirstElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement TwoRecordsFirstElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first']/div[@role='gridcell'][@col-id='skuUpc']")
    private WebElement TwoRecordsFirstElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute ag-row-last']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement TwoRecordsSecondElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute ag-row-last']/div[@role='gridcell'][@col-id='skuUpc']")
    private WebElement TwoRecordsSecondElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement ThreeRecordsFirstElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-first']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    private WebElement ThreeRecordsFirstElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement ThreeRecordsSecondElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    private WebElement ThreeRecordsSecondElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-last']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    private WebElement ThreeRecordsLastElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute ag-row-last']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    private WebElement ThreeRecordsLastElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    List<WebElement> ThreeRecordsOddElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-even ag-row-level-0 ag-row-position-absolute']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    List<WebElement> ThreeRecordsOddElementUpc;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute']//span[@class='ag-icon ag-icon-checkbox-unchecked']")
    List<WebElement> ThreeRecordsEvenElementCheckbox;

    @FindBy(xpath = "//div[@class='ag-row ag-row-no-focus ag-row-odd ag-row-level-0 ag-row-position-absolute']//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-with-height ag-cell-value'][@col-id='skuUpc']")
    List<WebElement> ThreeRecordsEvenElementUpc;

    @FindBy(xpath = "//span[contains(text(),'RELEASE RECEIPT')]")
    private WebElement ReleaseReceipt;

    @FindBy(xpath = "//span[contains(text(),'OK')]")
    private WebElement ReleaseConfirmationOkButton;

    @FindBy(xpath = "(//*[@ref='cbSelectAll']//*[@class='ag-icon ag-icon-checkbox-unchecked'])[1]")
    private WebElement SelectAllCheckBox;

    @FindBy(xpath = "//div[@role='group']/button[2]/span[1]")
    private WebElement releasedropdown;

    @FindBy(xpath = "//ul[@role='menu']/li[contains(text(),'Release By Distro')]")
    private WebElement releasebyDistroOption;

    @FindBy(xpath = "//div[@role='group']/button[1]")
    private WebElement releaseBt;

    public void selectPODashboard() {
        getWait(10).ignoring(Exception.class).until(visibilityOf(poDashboard));
        poDashboard.click();
        log.info("PO Dashboard selected");
    }

    public void searchPONbrInDashboard(String option) throws InterruptedException {
        getWait(60).ignoring(Exception.class).until(visibilityOf(clearButton));
        log.info("search Dashboard");
        TimeUnit.SECONDS.sleep(5);
        typeIntoInputField("PO NBR", option);
        TimeUnit.SECONDS.sleep(5);
        getWait(60).ignoring(Exception.class).until(visibilityOf(searchButton));
        searchButton.click();
    }

    public void releasePOInDashboard() throws InterruptedException {
        getWait(35).ignoring(Exception.class).until(visibilityOf(poNbr));
        //clearButton.click();
        log.info("Release Operation to Begin");
        TimeUnit.SECONDS.sleep(20);
        releaseButton.click();
        TimeUnit.SECONDS.sleep(20);
    }

    public void CheckBoxRelease(String noOfUpcs, String indexOfCheckbox, String operation) throws InterruptedException {
        LinkedList<Long> UPCsForRelease = new LinkedList<Long>();
        PODashboardReleasePage.UPCsForRelease.clear();
        switch (noOfUpcs) {
            case "1": {

                if (indexOfCheckbox.equals("ALL") || indexOfCheckbox.equals("1")) {
                    UPCsForRelease.add(Long.parseLong(OneRecordFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        OneRecordFirstElementCheckBox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                }
                break;
            }

            case "2": {
                if (indexOfCheckbox.equals("ALL") || indexOfCheckbox.equals("2")) {

                    UPCsForRelease.add(Long.parseLong(TwoRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        TwoRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                    UPCsForRelease.add(Long.parseLong(TwoRecordsSecondElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        TwoRecordsSecondElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }

                }

                if (indexOfCheckbox.equals("1")) {
                    UPCsForRelease.add(Long.parseLong(TwoRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        TwoRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                }
                break;
            }

            case "3": {
                if (indexOfCheckbox.equals("ALL") || indexOfCheckbox.equals("3")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsSecondElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsSecondElementCheckbox.click();
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsLastElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsLastElementCheckbox.click();
                    }

                }
                if (indexOfCheckbox.equals("2")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsSecondElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsSecondElementCheckbox.click();
                        TimeUnit.SECONDS.sleep(20);
                    }
                }
                if (indexOfCheckbox.equals("1")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                        TimeUnit.SECONDS.sleep(20);
                    }
                }
                break;
            }

            case "4": {
                if (indexOfCheckbox.equals("ALL") || indexOfCheckbox.equals("3")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsEvenElementUpc.get(0).getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsEvenElementCheckbox.get(0).click();
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsOddElementUpc.get(0).getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsOddElementCheckbox.get(0).click();
                        TimeUnit.SECONDS.sleep(20);
                    }
                }
                if (indexOfCheckbox.equals("2")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsEvenElementUpc.get(0).getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsEvenElementCheckbox.get(0).click();
                    }
                }
                if (indexOfCheckbox.equals("1")) {
                    UPCsForRelease.add(Long.parseLong(ThreeRecordsFirstElementUpc.getText()));
                    if (operation.equals("releasing")) {
                        ThreeRecordsFirstElementCheckbox.click();
                        getWait(20).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                    }
                }

            }

            break;
        }
        log.info("UPCs ready for release : " + UPCsForRelease.toString());
        PODashboardReleasePage.UPCsForRelease = UPCsForRelease;
        if (operation.equals("releasing")) {
            getWait(60).ignoring(Exception.class).until(visibilityOf(releasedropdown));
            releasedropdown.click();
            getWait(60).ignoring(Exception.class).until(visibilityOf(releasebyDistroOption));
            releasebyDistroOption.click();
            releaseBt.click();
            TimeUnit.SECONDS.sleep(20);
        }
        TimeUnit.SECONDS.sleep(10);
        logout.click();
        TimeUnit.SECONDS.sleep(10);

    }

    public void CheckAllBoxRelease(String indexOfCheckbox, String operation) throws InterruptedException {
        PODashboardReleasePage.UPCsForRelease.clear();
        TimeUnit.SECONDS.sleep(20);
        if (indexOfCheckbox.equals("SelectAllCheckbox")) {
            SelectAllCheckBox.click();
            TimeUnit.SECONDS.sleep(5);
            if (operation.equals("releasing")) {
                getWait(60).ignoring(Exception.class).until(visibilityOf(releasedropdown));
                releasedropdown.click();
                getWait(60).ignoring(Exception.class).until(visibilityOf(releasebyDistroOption));
                releasebyDistroOption.click();
                releaseBt.click();
                TimeUnit.SECONDS.sleep(20);
            }
            TimeUnit.SECONDS.sleep(10);
            logout.click();
            TimeUnit.SECONDS.sleep(10);
        }

    }


    public void typeIntoInputField(String fieldName, String input) {
        log.info("Entering typeIntoInputField ");
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
        for (WebElement inputField : searchInputsLocators) {
            try {
                String pathToLabel;
                if (fieldName.equals("Department")) {
                    pathToLabel = "div/label";
                } else {
                    pathToLabel = "label";
                }
                WebElement inputLabel = inputField.findElement(By.xpath(pathToLabel));
                if (inputLabel.getText().equals(fieldName)) {
                    log.info("Entering data into field {} ", inputLabel.getText());
                    WebElement inputFieldLocator = inputField
                            .findElement(By.xpath(".//label[text()='" + fieldName + "']/following-sibling::div/input"));
                    inputFieldLocator.click();
                    inputFieldLocator.clear();
                    inputFieldLocator.sendKeys(input);
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

}
