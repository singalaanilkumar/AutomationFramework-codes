Meta:
@issue Preview, Run, Release,Undo,Close Wave,MHE Messages Validation and WSM Activities Validation on SCMUI
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy BH13181_Mahalakshmi_Subbaiah

Narrative:
In order to validate Wave Planning , In Progress Page, functionalities
As a E2E Quality assurance person
I want to process PO through Dc four walls

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for PackawayPull_UI
Scenario: PackawayPull_UI
Meta:
@acceptance
@id Preview, Run, Release,Undo,Close Wave,MHE Messages Validation and WSM Activities Validation on SCMUI
@tag Type:endToend , Module:PackawayPull
@automatedBy BH13181_Mahalakshmi_Subbaiah
Given RTF Details are cleared for SKUs
|getRequestUrl    				|getUpdateRequestUrl			   |GETQueryParams                                        																																						  |
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+7,shipOutEndDate:DATE+8,effectiveStartDate:DATE+6,effectiveEndDate:DATE+7,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:801;831|
Then Publish Multiple RTF message
|topic                   |templateName    		 	|orderParams                           																     |lineItemTemplate    |lineParams                   												  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6389,#expShpDt:DATE+7,#holdDt:DATE+6}|RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:6,#SKU:492607865197}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6390,#expShpDt:DATE+8,#holdDt:DATE+7}		     |RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:6,#SKU:492607865197}							  |
Then validate Published RTF before wave
|requestUrl                     |queryParams                                      																																							   |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+7,shipOutEndDate:DATE+8,effectiveStartDate:DATE+6,effectiveEndDate:DATE+7,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:801;831|ENR     	  |
Given inventory is cleared for the given SKUs
|getRequestUrl                     |deleteRequestUrl                 |GETQueryParams                                                   |DELETEQueryParams|
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492607538329;492607865197,status:AVL,containerType:BINBOX|reasonCode:RR    |
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492607538329;492607865197,status:RSV,containerType:BINBOX|reasonCode:RR    |
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:7025735,#Receipt:167662032,#barcode:95-D-18,#item:492607538329,#qty:20,#itmStat:AVL,#lineBarcod:240571542865,#dept:801,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:5134092,#Receipt:166089822,#barcode:95-D-18,#item:492607865197,#qty:16,#itmStat:AVL,#lineBarcod:9260786519722,#dept:831,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
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
Then Preview Wave,Cancel Preview and Run for wave1 wave on SCMUI
|WaveDetails																									  							  	    	       |
|#startShpDt:DATE+7,#endShpDt:DATE+8,#efctStartDt:DATE+6,#efctEndDt:DATE+7,#dept:801;831,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000|
Then Undo wave1 wave on SCMUI
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+7,#endShpDt:DATE+8,#efctStartDt:DATE+6,#efctEndDt:DATE+7,#dept:801;831,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then User navigates to MHE and validates STOREALLOC messages for Run wave1 Wave
|GETQueryParams                                                                           |																								  							  	    	       |
|#startTRXRangeDt:DATE-1,#endTRXRangeDt:DATE,#messageType:STOREALLOC,#textFilter:#waveNumber|
Then message is updated using Edit and Reprocessing is done
Then Validate WSM Activities for wave1 wave in READY Status
|SKU	     |ActivityType|Count|
|492607538329|BINPULLSPLIT|1    |
|492607538329|SPLIT		  |2    |
|492607865197|BINPULLSPLIT|1    |
|492607865197|SPLIT		  |2    |
Then validate the inventory for wave1 wave using GET service
|invRequestUrl                     |invQueryParams              									 |
|InventoryServices.GetSnapshotItems|barcode:492607538329;492607865197,status:RSV,containerType:BINBOX|
Then validate RTF order status for wave1 wave
|requestUrl                     |queryParams                                      																																		     |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+7,shipOutEndDate:DATE+8,effectiveStartDate:DATE+6,effectiveEndDate:DATE+7,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:801;831|ALC			|
Given SupplyChain home page
Then Release wave1 wave
Then On SCMUI Validate WSM Activities for wave1 wave in OPEN Status
Then Cancel WSM Activity in UI and Create activity for deleted activity for wave1
Then Unassign WSM Activity in UI for wave1 wave
Given User logs in to RF application, selects DC2.0 RF Options
When PackawayPull activities are completed for wave1 wave
|SKU    	 |ACTION|LOCATION|
|492607538329|ALT  	|PA43M041|
|492607865197|SUB  	|  		 |
When Runner activities are completed for wave1 Wave
|getRequestUrl    |getQueryParams                  						  |
|WSM.getActivities|waveNumber:#waveNumber,type:BINSPLITPULLRUN,status:OPEN|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                                	   |
|WSM.getActivities|WSM.deleteActivities|upc:492607538329,type:PACKAWAY,status:OPEN;ASSIGNED|
|WSM.getActivities|WSM.deleteActivities|upc:492607865197,type:PACKAWAY,status:OPEN;ASSIGNED|
When VAS/PREP is performed for wave wave1 in PREP area
When Pack away Sort is done for the wave1 Wave Bins
Then System sends TOTECONT message for wave1 Wave
|getRequestUrl    	|GETQueryParams        						|
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:TOTECONT|
When UNITPUT message for wave1 Wave is published by Pyramid for moving inventory to carton on 107AA004
Then Inventory is created for outbound cartons and decreased from original Containers for wave1 wave
When CONTCLOSE message is simulated to Pyramid for wave1 wave
And SCANWEIGH is generated by pyramid SHIPREQUEST event is sent to shipping service for wave1 wave
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
And SHIPCONFIRM message is sent by Pyramid after store package is shipped
Given SupplyChain home page
Then close wave1 wave on SCMUI