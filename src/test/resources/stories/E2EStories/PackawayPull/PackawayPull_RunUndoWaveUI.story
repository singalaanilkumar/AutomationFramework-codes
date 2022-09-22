Meta:
@issue Preview, Run, Release and Undo Wave on SCMUI
@tags Project: Backstage, Product:PackawayPull, Program: Off-Price
@automatedBy BC90965_UmaShankar_Paulvannan

Narrative:
In order to validate Wave Planning and In Progress Page functionalities
As a Backstage application user
I want to Preview, Run, Release and Undo Wave on SCMUI sucessfully

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for RunUndoWaveUI

Scenario:1 Positive scenario - Preview, Run, Release and Undo a Wave successfully on SCMUI
Meta:
@id Preview, Run, Release and Undo Wave on SCMUI
@tag Type:endToend , Module:PackawayPull
@automatedBy BC90965_UmaShankar_Paulvannan

Given RTF Details are cleared for SKUs
|getRequestUrl    				|getUpdateRequestUrl			   |GETQueryParams                                        																																						  |
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+6,shipOutEndDate:DATE+7,effectiveStartDate:DATE+5,effectiveEndDate:DATE+6,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:832;833|
Then Publish Multiple RTF message
|topic                   |templateName    		 	|orderParams                           																     |lineItemTemplate    |lineParams                   												  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6389,#expShpDt:DATE+6,#holdDt:DATE+5}|RTFLineItem_PCK.json|{#QTY:5,#SKU:190468162713}{#QTY:6,#SKU:492607758505}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6390,#expShpDt:DATE+7,#holdDt:DATE+6}		     |RTFLineItem_PCK.json|{#QTY:5,#SKU:190468162713}{#QTY:6,#SKU:492607758505}							  |
Then validate Published RTF before wave
|requestUrl                     |queryParams                                      																																							   |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+6,shipOutEndDate:DATE+7,effectiveStartDate:DATE+5,effectiveEndDate:DATE+6,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:832;833|ENR     	  |
Given inventory is cleared for the given SKUs
|getRequestUrl                     |deleteRequestUrl                 |GETQueryParams                                                   |DELETEQueryParams|
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:190468162713;492607758505,status:AVL,containerType:BINBOX|reasonCode:RR    |
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:190468162713;492607758505,status:RSV,containerType:BINBOX|reasonCode:RR    |
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:7256520,#Receipt:166004917,#barcode:95-D-18,#item:190468162713,#qty:20,#itmStat:AVL,#lineBarcod:812089310764,#dept:832,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:2940911,#Receipt:166224189,#barcode:95-D-18,#item:492607758505,#qty:16,#itmStat:AVL,#lineBarcod:9260775850589,#dept:833,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
Then create parent containers
|requestUrl                       |templateName     |requestParams                   				 |
|InventoryServices.CreateContainer|CreatePallet.json|{#contStatus:PTW,#contTyp:PLT,#barCode:PREVSTEP}|
And locate pallet to packaway location
|requestUrl                       |templateName        |requestParams                              |
|InventoryServices.LocateContainer|LocateContainer.json|{#parentConTyp:LCN,#parentBarcode:PA11A042}|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN,pullZone:002  	  |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN,pullZone:002|
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor:B0$WHM2SUPERUSER    |
Given User logs in SCM application
Then Preview Run and Validate wave1 wave on SCMUI
|WaveDetails																									  							  	    	       |
|#startShpDt:DATE+6,#endShpDt:DATE+7,#efctStartDt:DATE+5,#efctEndDt:DATE+6,#dept:832;833,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000|
Then STOREALLOC message Validated for Run wave1 Wave
|getRequestUrl    	|GETQueryParams        						  |
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:STOREALLOC|
Then Validate WSM Activities for wave1 wave in READY Status
|SKU	     |ActivityType|Count|
|190468162713|BINPULLSPLIT|1    |
|190468162713|SPLIT		  |2    |
|492607758505|BINPULLSPLIT|1    |
|492607758505|SPLIT		  |2    |
Then validate the inventory for wave1 wave using GET service
|invRequestUrl                     |invQueryParams              									 |
|InventoryServices.GetSnapshotItems|barcode:190468162713;492607758505,status:RSV,containerType:BINBOX|
Then validate RTF order status for wave1 wave
|requestUrl                     |queryParams                                      																																		     |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+6,shipOutEndDate:DATE+7,effectiveStartDate:DATE+5,effectiveEndDate:DATE+6,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:832;833|ALC			|
Given SupplyChain home page
Then release wave1 wave on SCMUI
Then Validate WSM Activities for wave1 wave in OPEN Status
|SKU	     |ActivityType|Count|
|190468162713|BINPULLSPLIT|1    |
|190468162713|SPLIT		  |2    |
|492607758505|BINPULLSPLIT|1    |
|492607758505|SPLIT		  |2    |
Then Undo wave1 wave on SCMUI
Then STOREALLOC message Validated for Undo wave1 Wave
|getRequestUrl    	|GETQueryParams        						  |
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:STOREALLOC|
Then Validate WSM Activities after Undo wave1 wave
Then validate RTF order status for wave1 wave
|requestUrl                     |queryParams                                      																																		     |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+6,shipOutEndDate:DATE+7,effectiveStartDate:DATE+5,effectiveEndDate:DATE+6,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:832;833|ENR			|