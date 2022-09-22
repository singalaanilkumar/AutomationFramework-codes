Meta:
@issue Sample
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for 1 PID and one line item and distro to one existing stores, one new store and packaway
As a E2E Quality assurance person
I want to process PO through Dc four walls

Scenario: PO Details Enquiry
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH13181


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
Then Preview Run,Cancel Preview Run for wave1 wave on SCMUI
|WaveDetails																									  							  	    	       |
|#startShpDt:DATE+6,#endShpDt:DATE+7,#efctStartDt:DATE+5,#efctEndDt:DATE+6,#dept:832;833,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000|
Then Undo wave1 wave on SCMUI
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+6,#endShpDt:DATE+7,#efctStartDt:DATE+5,#efctEndDt:DATE+6,#dept:832;833,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then User navigates to MHE and validates STOREALLOC messages for Run wave1 Wave
|GETQueryParams                                                                           |																								  							  	    	       |
|#startTRXRangeDt:DATE-1,#endTRXRangeDt:DATE,#messageType:STOREALLOC,#textFilter:#waveNumber|
Then message is updated using Edit and Reprocessing is done
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
Then release wave1 wave on SCMUI
Then On SCMUI Validate WSM Activities for wave1 wave in OPEN Status
|SKU	     |ActivityType|Count|
|190468162713|BINPULLSPLIT|1    |
|190468162713|SPLIT		  |2    |
|492607758505|BINPULLSPLIT|1    |
|492607758505|SPLIT		  |2    |
Then Cancel WSM Activity in UI and Create activity for deleted activity for wave1
Then Assigned activity is Unassigned in UI
Given User logs in to RF application, selects DC2.0 RF Options
When PackawayPull activities are completed for wave1 wave
|SKU    	 |ACTION|LOCATION|
|190468162713|ALT  	|PA43M041|
|492607758505|SUB  	|  		 |
When Runner activities are completed for wave1 Wave
|getRequestUrl    |getQueryParams                  						  |
|WSM.getActivities|waveNumber:#waveNumber,type:BINSPLITPULLRUN,status:OPEN|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                                	   |
|WSM.getActivities|WSM.deleteActivities|upc:190468162713,type:PACKAWAY,status:OPEN;ASSIGNED|
|WSM.getActivities|WSM.deleteActivities|upc:492607758505,type:PACKAWAY,status:OPEN;ASSIGNED|
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
And Cartons are moved to Shipped status after publishing LOGD via DC20_PUBSUB
