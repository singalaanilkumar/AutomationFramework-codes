package com.macys.mst.DC2.EndToEnd.configuration;



import java.util.List;

import com.macys.mst.DC2.EndToEnd.model.Attribute;
import com.macys.mst.DC2.EndToEnd.model.InventorySnapshot;

public class InventoryTestdata {

    String barcode;
    String parentContainer;
    String childContainer;
    String actualResponse;
    String parentContainerType;
    String childContainerType;
    List<InventorySnapshot> inventorySnapshot;
    String instruction;
    String containerStatus;
    String targetparentContainer;
    List<Attribute> expectedInventoryAttribute;
    List<Attribute> expectedContainerAttribute;


    public String getTargetParentContainer() {        return targetparentContainer;    }
    public String getParentContainerType() {
        return parentContainerType;
    }

    public void setParentContainerType(String parentContainerType) {
        this.parentContainerType = parentContainerType;
    }

    public String getChildContainerType() {
        return childContainerType;
    }

    public void setChildContainerType(String childContainerType) {
        this.childContainerType = childContainerType;
    }

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }

    public List<Attribute> getExpectedInventoryAttribute() {
        return expectedInventoryAttribute;
    }

    public void setExpectedInventoryAttribute(List<Attribute> expectedInventoryAttribute) {
        this.expectedInventoryAttribute = expectedInventoryAttribute;
    }

    public List<Attribute> getExpectedContainerAttribute() {
        return expectedContainerAttribute;
    }

    public void setExpectedContainerAttribute(List<Attribute> expectedContainerAttribute) {
        this.expectedContainerAttribute = expectedContainerAttribute;
    }

    public List<InventorySnapshot> getInventorySnapshot() {
        return inventorySnapshot;
    }

    public void setInventorySnapshot(List<InventorySnapshot> inventorySnapshot) {
        this.inventorySnapshot = inventorySnapshot;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getActualResponse() {
        return actualResponse;
    }

    public void setActualResponse(String actualResponse) {
        this.actualResponse = actualResponse;
    }

    public String getParentContainer() {
        return parentContainer;
    }
    public void setTargetParentContainer(String targetparentContainer) {
        this.targetparentContainer = targetparentContainer;
    }


    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    public String getChildContainer() {
        return childContainer;
    }

    public void setChildContainer(String childContainer) {
        this.childContainer = childContainer;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    static final class ContextLocal extends ThreadLocal<InventoryTestdata> {
        @Override
        protected InventoryTestdata initialValue() {
            return new InventoryTestdata();
        }
    }
    private static final ThreadLocal<InventoryTestdata> userThreadLocal = new ContextLocal();

    public static InventoryTestdata get() {
        return userThreadLocal.get();
    }
}


