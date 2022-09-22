Meta:
@issue Bulk Sort2Store Wave Scenario
@tags Project: Backstage, Product:PackawayPull, Program: Off-Price
@automatedBy BC90965_UmaShankar_Paulvannan

Narrative:
In order to fullfill Bulk RTFs
As a Backstage application user
I want to run a wave and Complete the Wave Sort2Store Activities successfully

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for WaveSort2Store

Scenario:1 Positive scenario - successfully running Wave for BLK SKUs
Meta:
@id Multi Wave Scenario
@tag Type:endToend , Module:PackawayPull
@automatedBy BC90965_UmaShankar_Paulvannan

Given RTF Details are cleared for SKUs
|getRequestUrl    				|getUpdateRequestUrl			   |GETQueryParams                                        																																					|
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+8,shipOutEndDate:DATE+9,effectiveStartDate:DATE+7,effectiveEndDate:DATE+8,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77|
Then Publish Multiple RTF message
|topic                   |templateName    		 	|orderParams                           																     |lineItemTemplate    |lineParams                 |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:7278,#expShpDt:DATE+8,#holdDt:DATE+7}|RTFLineItem_PCK.json|{#QTY:50,#SKU:492608031577}|
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:7277,#expShpDt:DATE+9,#holdDt:DATE+8}|RTFLineItem_PCK.json|{#QTY:50,#SKU:492608031577}|
Then validate Published RTF before wave
|requestUrl                     |queryParams                                      																																						   |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+8,shipOutEndDate:DATE+9,effectiveStartDate:DATE+7,effectiveEndDate:DATE+8,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:820|ENR		      |
Given inventory is cleared for the given SKUs
|getRequestUrl                     |deleteRequestUrl                 |GETQueryParams         							  |DELETEQueryParams|
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492608031577,status:AVL,containerType:BINBOX|reasonCode:RR    |
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492608031577,status:RSV,containerType:BINBOX|reasonCode:RR    |
When Clean the activity for the given stores for wave1 RTF
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		 |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:4747500,#Receipt:4521802,#barcode:95-D-18,#item:492608031577,#qty:100,#itmStat:AVL,#lineBarcod:514171179823,#dept:820,#ProcessArea:BLK,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
Then create parent containers
|requestUrl                       |templateName     |requestParams                   				 |
|InventoryServices.CreateContainer|CreatePallet.json|{#contStatus:PTW,#contTyp:PLT,#barCode:PREVSTEP}|
And locate pallet to packaway location
|requestUrl                       |templateName        |requestParams                              |
|InventoryServices.LocateContainer|LocateContainer.json|{#parentConTyp:LCN,#parentBarcode:PA11A039}|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                            		       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:003  	       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:003 	   |
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor:B0$WHM2SUPERUSER    			   |
|WSM.getActivities|WSM.deleteActivities|status:OPEN;ASSIGNED,type:STSCARTON,container:7278;7277|
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+8,#endShpDt:DATE+9,#efctStartDt:DATE+7,#efctEndDt:DATE+8,#dept:820,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then Validate store alloc message in sorting db for wave1 wave
Then Validate WSM Activities for wave1 wave in READY Status
|SKU	     |ActivityType|Count|
|492608031577|BINPULL	  |1    |
Then validate the inventory for wave1 wave using GET service
|invRequestUrl                     |invQueryParams              									 |
|InventoryServices.GetSnapshotItems|barcode:492608031577,status:RSV,containerType:BINBOX|
Then validate RTF order status for wave1 wave
|requestUrl                     |queryParams                                      																																	     |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+8,shipOutEndDate:DATE+9,effectiveStartDate:DATE+7,effectiveEndDate:DATE+8,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:820|ALC		    |
Then Release wave1 wave
Then Validate WSM Activities for wave1 wave in OPEN Status
|SKU	     |ActivityType|Count|
|492608031577|BINPULL	  |1    |
Given User logs in to RF application, selects DC2.0 RF Options
When PackawayPull activities are completed for wave1 wave
|SKU    	 |ACTION|LOCATION|
Then System sends TOTECONT message for wave1 Wave
|getRequestUrl    	|GETQueryParams        						|
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:TOTECONT|
When Sort To Store is performed in zone 001 and processing location BA01A018 and staged to BA01A114 location for wave1 wave