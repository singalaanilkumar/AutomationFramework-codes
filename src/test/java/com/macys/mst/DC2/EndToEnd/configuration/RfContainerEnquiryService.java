package com.macys.mst.DC2.EndToEnd.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macys.mst.DC2.EndToEnd.model.Attribute;
import com.macys.mst.DC2.EndToEnd.model.ContainerRelation;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.InventorySnapshot;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.rest.RestUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RfContainerEnquiryService {

	private static HashMap<String, Object> serviceDataMap = new HashMap<String, Object>();

	public static final String Container_ID ="Container ID:";
	public static final String Type ="Type :";
	public static final String Status="Status :";
	public static final String Location= "Location :";
	public static final String Divert_Destination= "Divert Destination:";

	public static HashMap<String, Object> getContainerInquireDetails() {
		return serviceDataMap;
	}

	@SuppressWarnings("unchecked")
	private void buildContainerInquireDetails(String response) {
		InventoryContainer  inventoryContainer = CommonUtils.getClientResponse(response, new TypeReference<InventoryContainer>() {});
		serviceDataMap = new HashMap<String, Object>();

		List<ContainerRelation> containerRelationshipObjectList = inventoryContainer.getContainer().getContainerRelationshipList();
		List<Attribute> attributeList =  inventoryContainer.getContainer().getAttributeList() != null? inventoryContainer.getContainer().getAttributeList() : null;
		List<InventorySnapshot> inventorySnapshotList = inventoryContainer.getInventorySnapshotList() != null ? inventoryContainer.getInventorySnapshotList() : null;
		String containerID = inventoryContainer.getContainer().getBarCode()!=null? inventoryContainer.getContainer().getBarCode().toString():"";
		String type = inventoryContainer.getContainer().getContainerType()!=null? inventoryContainer.getContainer().getContainerType().toString():"";
		String status = inventoryContainer.getContainer().getContainerStatusCode()!=null? inventoryContainer.getContainer().getContainerStatusCode().toString():"";
		serviceDataMap.put(Container_ID, containerID);
		serviceDataMap.put(Type, type);
		serviceDataMap.put(Status, status);
		serviceDataMap.put("containerRealtionshipObjectList", containerRelationshipObjectList);
		serviceDataMap.put("attributeList", attributeList);
		serviceDataMap.put("inventorySnapshotList", inventorySnapshotList);
		log.info("containerID :: "+containerID);
		log.info("type :: "+type);
		log.info("status :: "+status);
		if(containerRelationshipObjectList != null) {
			containerRelationshipObjectList.forEach(value -> {
				String parentContainer = value.getParentContainer() != null ? value.getParentContainer() : "";
				String parentContainerType = value.getParentContainerType() != null ? value.getParentContainerType(): "";
				log.info(" Container relation ship object parentContainer :: " + parentContainer);
				log.info(" Container relation ship object parentContainerType:: " + parentContainerType);
				if ("LCN".equals(parentContainerType)) {
					log.info("Location"+  parentContainer);
					serviceDataMap.put(Location, parentContainer);
				}

			});
			HashMap<String, String>  childContainerMap = new HashMap<String, String>();
			containerRelationshipObjectList.forEach(value -> {
				String childContainer = value.getChildContainer()!=null?value.getChildContainer() : "";
				log.info(" Container relation ship object childContainer :: " + childContainer);
				if(!childContainer.equals(containerID))
					childContainerMap.put(childContainer, childContainer);

			});
			serviceDataMap.put("childContainerMap", childContainerMap);
		}
		if(attributeList != null) {
			attributeList.forEach(value -> {
				String attributeKey = value.getKey();
				//List<String> attributeValue = (List<String>)attributeObject.get("attributeList");
				log.info(" Container relation ship object attributeKey :: " + attributeKey);
				log.info(" Container relation ship object attributeValue :: " + value.getValues());
				if ("Divert".equals(attributeKey)) {
					log.info(" Container relation ship object attributeValue 123 :: " + value.getValues().get(0));
					serviceDataMap.put(Divert_Destination, value.getValues().get(0).toString());
				}
				if ("UnitsPerPack".equals(attributeKey)) {
					log.info(" Container relation ship object attributeValue 123 :: " + value.getValues().get(0));
					serviceDataMap.put("UnitsPerPack", value.getValues().get(0).toString());
				}
				if ("Pack".equals(attributeKey)) {
					log.info(" Container relation ship object attributeValue 123 :: " + value.getValues().get(0));
					serviceDataMap.put("Pack", value.getValues().get(0).toString());
				}

			});
		}

		log.info("serviceDataMap  :: "+serviceDataMap);
	}


	/**
	 * This method call the inventory services and receive the response jsone.
	 * 
	 * @param containerId
	 */
	public void fetchInventoryServiceDetails(String containerId) {
		try {
			log.info("fetchCartonInquireDetails inside ::");
			String fetchCartonUrl = ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", containerId);
			log.info("fetchCartonUrl {}", fetchCartonUrl);
			TimeUnit.SECONDS.sleep(15);
			String response = RestUtilities.getRequestResponse(fetchCartonUrl);
			log.info("fetchCartonInquireDetails  ::" + response);
			buildContainerInquireDetails(response);
		}catch (Exception e){
			log.info(e.getMessage());
		}
	}

}
