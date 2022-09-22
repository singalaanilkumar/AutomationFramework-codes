Meta:
@issue Multi Wave Scenario
@tags Project: Backstage, Product:PackawayPull, Program: Off-Price
@automatedBy BC90965_UmaShankar_Paulvannan

Narrative:
In order to fullfill 3 Multiline item RTFs
As a Backstage application user
I want to run Multiple waves and Complete the Wave Activities successfully

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for MultiWave

Scenario:1 Positive scenario - successfully run
+
ning Multiple waves
Meta:
@id Multi Wave Scenario
@tag Type:endToend , Module:PackawayPull
@automatedBy BC90965_UmaShankar_Paulvannan

Given RTF Details are cleared for SKUs
|getRequestUrl    				|getUpdateRequestUrl			   |GETQueryParams                                        																																									|
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+2,shipOutEndDate:DATE+6,effectiveStartDate:DATE,effectiveEndDate:DATE+4,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:803;830;808;829;809|
Then Publish Multiple RTF message
|topic                   |templateName    		 	|orderParams                           																     |lineItemTemplate    |lineParams                   												  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6389,#expShpDt:DATE+2,#holdDt:DATE}  |RTFLineItem_PCK.json|{#QTY:5,#SKU:492600478547}{#QTY:6,#SKU:492608017083}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6390,#expShpDt:DATE+3,#holdDt:DATE+1}		     |RTFLineItem_PCK.json|{#QTY:5,#SKU:492600478547}{#QTY:6,#SKU:492608017083}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6322,#expShpDt:DATE+4,#holdDt:DATE+2}|RTFLineItem_PCK.json|{#QTY:5,#SKU:492600478547}{#QTY:5,#SKU:492607612241}{#QTY:4,#SKU:492607643412} |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6324,#expShpDt:DATE+5,#holdDt:DATE+3}    		 |RTFLineItem_PCK.json|{#QTY:5,#SKU:492600478547}{#QTY:4,#SKU:492608017083}{#QTY:10,#SKU:492607284134}|
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6325,#expShpDt:DATE+6,#holdDt:DATE+4}|RTFLineItem_PCK.json|{#QTY:10,#SKU:492607284134}               									  |
Then validate Published RTF before wave
|requestUrl                     |queryParams                                      																																										 |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+2,shipOutEndDate:DATE+6,effectiveStartDate:DATE,effectiveEndDate:DATE+4,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:803;830;808;829;809|ENR		    |
Given inventory is cleared for the given SKUs
|getRequestUrl                     |deleteRequestUrl                 |GETQueryParams                                                         								  |DELETEQueryParams|
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492600478547;492608017083;492607612241;492607643412;492607284134,status:AVL,containerType:BINBOX|reasonCode:RR    |
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492600478547;492608017083;492607612241;492607643412;492607284134,status:RSV,containerType:BINBOX|reasonCode:RR    |
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:995969476,#Receipt:9168552,#barcode:95-D-18,#item:492600478547,#qty:20,#itmStat:AVL,#lineBarcod:1903621638146,#dept:803,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:994899017,#Receipt:9162500,#barcode:95-D-18,#item:492608017083,#qty:16,#itmStat:AVL,#lineBarcod:350645110977,#dept:830,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:2085626,#Receipt:167176188,#barcode:95-D-18,#item:492607612241,#qty:5,#itmStat:AVL,#lineBarcod:26199928541,#dept:808,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}  |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:6795606,#Receipt:166100037,#barcode:95-D-18,#item:492607643412,#qty:9,#itmStat:AVL,#lineBarcod:254283425100,#dept:829,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT} |
|InventoryServices.CreateInventory|CreateBINBOXwithoutPALLET_PP.json|{#contTyp:BINBOX,#contStat:PTW,#PO:6855809,#Receipt:166225704,#barcode:95-D-18,#item:492607284134,#qty:10,#itmStat:AVL,#lineBarcod:9260728413404,#dept:809,#ProcessArea:BTY}|
|InventoryServices.CreateInventory|CreateBINBOXwithoutPALLET_PP.json|{#contTyp:BINBOX,#contStat:PTW,#PO:6855809,#Receipt:166225704,#barcode:95-D-18,#item:492607284134,#qty:10,#itmStat:AVL,#lineBarcod:9260728413404,#dept:809,#ProcessArea:BTY}|
Then create parent containers
|requestUrl                       |templateName     |requestParams                   				 |
|InventoryServices.CreateContainer|CreatePallet.json|{#contStatus:PTW,#contTyp:PLT,#barCode:PREVSTEP}|
And locate pallet to packaway location
|requestUrl                       |templateName        |requestParams                              |
|InventoryServices.LocateContainer|LocateContainer.json|{#parentConTyp:LCN,#parentBarcode:PA46H019}|
And locate BinBox to packaway location
|requestUrl                       |templateName        |requestParams                              |
|InventoryServices.LocateContainer|LocateContainer.json|{#parentConTyp:LCN,#parentBarcode:PA46H019}|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:001  	  |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:001|
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:005  	  |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:005|
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor:B0$WHM2SUPERUSER    |
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+2,#endShpDt:DATE+3,#efctStartDt:DATE,#efctEndDt:DATE+1,#dept:803;830,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then STOREALLOC message Validated for Run wave1 Wave
|getRequestUrl    	|GETQueryParams        						  |
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:STOREALLOC|
Then Validate WSM Activities for wave1 wave in READY Status
|SKU	     |ActivityType|Count|
|492600478547|BINPULLSPLIT|1    |
|492600478547|SPLIT		  |2    |
|492608017083|BINPULLSPLIT|1    |
|492608017083|SPLIT		  |2    |
Then validate the inventory for wave1 wave using GET service
|invRequestUrl                     |invQueryParams              									 |
|InventoryServices.GetSnapshotItems|barcode:492600478547;492608017083,status:RSV,containerType:BINBOX|
Then validate RTF order status for wave1 wave
|requestUrl                     |queryParams                                      																																		   |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+2,shipOutEndDate:DATE+3,effectiveStartDate:DATE,effectiveEndDate:DATE+1,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:803;830|ALC			  |
Then Release wave1 wave
Then Validate WSM Activities for wave1 wave in OPEN Status
|SKU	     |ActivityType|Count|
|492600478547|BINPULLSPLIT|1    |
|492600478547|SPLIT		  |2    |
|492608017083|BINPULLSPLIT|1    |
|492608017083|SPLIT		  |2    |
Given User logs in to RF application, selects DC2.0 RF Options
When PackawayPull activities are completed for wave1 wave
|SKU    	 |ACTION|LOCATION|
|492600478547|ALT  	|PA43M041|
|492608017083|SUB  	|  		 |
When Runner activities are completed for wave1 Wave
|getRequestUrl    |getQueryParams                  						  |
|WSM.getActivities|waveNumber:#waveNumber,type:BINSPLITPULLRUN,status:OPEN|
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                                	   |
|WSM.getActivities|WSM.deleteActivities|upc:492600478547,type:PACKAWAY,status:OPEN;ASSIGNED|
|WSM.getActivities|WSM.deleteActivities|upc:492608017083,type:PACKAWAY,status:OPEN;ASSIGNED|
When VAS/PREP is performed for wave wave1 in PREP area
When Pack away Sort is done for the wave1 Wave Bins
Then System sends TOTECONT message for wave1 Wave
|getRequestUrl    	|GETQueryParams        						|
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:TOTECONT|
When UNITPUT message for wave1 Wave is published by Pyramid for moving inventory to carton on 107AA004
Then Inventory is created for outbound cartons and decreased from original Containers for wave1 wave
When MMS CLSCTN message pull-whm-orderfulfillment-mms-shipcntr-multiWave-east4 subscriber is available
When CONTCLOSE message is simulated to Pyramid for wave1 wave
And SCANWEIGH is generated by pyramid SHIPREQUEST event is sent to shipping service for wave1 wave
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then validate MMS CLSCTN messages for Wave are published and valid
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                            		|
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:007 	|
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:007|
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor:B0$WHM2SUPERUSER    		|
Then preview and Run the PCKPULL wave2 wave and validate
|requestParams   																																								 |
|{#startShpDt:DATE+4,#endShpDt:DATE+5,#efctStartDt:DATE+2,#efctEndDt:DATE+3,#dept:803;830;808;829;809,#waveType:OSC_BTY,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then STOREALLOC message Validated for Run wave2 Wave
|getRequestUrl    	|GETQueryParams        						  |
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:STOREALLOC|
Then validate RTF order status for wave2 wave
|requestUrl                     |queryParams                                      																																		  				 |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+4,shipOutEndDate:DATE+5,effectiveStartDate:DATE+2,effectiveEndDate:DATE+3,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:803;830;808;829;809|ALC			|
Then Validate WSM Activities for wave2 wave in READY Status
|SKU	     |ActivityType|Count|
|492607284134|BINPULL	  |1    |
|492600478547|BINPULL	  |1    |
|492607612241|BINPULL	  |1    |
|492608017083|BINPULL	  |1    |
|492607643412|BINPULLSPLIT|1    |
|492607643412|SPLIT		  |2    |
Then validate the inventory for wave2 wave using GET service
|invRequestUrl                     |invQueryParams              										 									|
|InventoryServices.GetSnapshotItems|barcode:492600478547;492608017083;492607612241;492607643412;492607284134,status:RSV,containerType:BINBOX|
Then Release wave2 wave
Then Validate WSM Activities for wave2 wave in OPEN Status
|SKU	     |ActivityType|Count|
|492607284134|BINPULL	  |1    |
|492600478547|BINPULL	  |1    |
|492607612241|BINPULL	  |1    |
|492608017083|BINPULL	  |1    |
|492607643412|BINPULLSPLIT|1    |
|492607643412|SPLIT		  |2    |
When PackawayPull activities are completed for wave2 wave
|SKU|ACTION|LOCATION|
When Runner activities are completed for wave2 Wave
|getRequestUrl    |getQueryParams                  						  |
|WSM.getActivities|waveNumber:#waveNumber,type:BINRUNNER,status:OPEN	  |
|WSM.getActivities|waveNumber:#waveNumber,type:BINSPLITPULLRUN,status:OPEN|
When VAS/PREP is performed for wave wave2 in PREP area
Then System sends TOTECONT message for wave2 Wave
|getRequestUrl    	|GETQueryParams        						|
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:TOTECONT|
When UNITPUT message for wave2 Wave is published by Pyramid for moving inventory to carton on 107AA004
Then Inventory is created for outbound cartons and decreased from original Containers for wave2 wave
Then preview and Run the PCKPULL wave3 wave and validate
|requestParams   																																			 |
|{#startShpDt:DATE+6,#endShpDt:DATE+6,#efctStartDt:DATE+4,#efctEndDt:DATE+4,#dept:809,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then STOREALLOC message Validated for Run wave3 Wave
|getRequestUrl    	|GETQueryParams        						  |
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:STOREALLOC|
Then validate RTF order status for wave3 wave
|requestUrl                     |queryParams                                      																																		 |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+6,shipOutEndDate:DATE+6,effectiveStartDate:DATE+4,effectiveEndDate:DATE+4,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,orderLimit:1000,divNbr:77,deptNbrs:809|ALC			|
Then Validate WSM Activities for wave3 wave in READY Status
|SKU	     |ActivityType|Count|
|492607284134|BINPULL	  |1    |
Then validate the inventory for wave3 wave using GET service
|invRequestUrl                     |invQueryParams           							|
|InventoryServices.GetSnapshotItems|barcode:492607284134,status:RSV,containerType:BINBOX|
Then Release wave3 wave
Then Validate WSM Activities for wave3 wave in OPEN Status
|SKU	     |ActivityType|Count|
|492607284134|BINPULL	  |1    |
When PackawayPull activities are completed for wave3 wave
|SKU|ACTION|LOCATION|
When Runner activities are completed for wave3 Wave
|getRequestUrl    |getQueryParams                  					|
|WSM.getActivities|waveNumber:#waveNumber,type:BINRUNNER,status:OPEN|
Then System sends TOTECONT message for wave3 Wave
|getRequestUrl    	|GETQueryParams        						|
|Messaging.getMsgURL|textFilter:#waveNumber,messageType:TOTECONT|
When UNITPUT message for wave3 Wave is published by Pyramid for moving inventory to carton on 107AA004
Then Inventory is created for outbound cartons and decreased from original Containers for wave3 wave
When CONTCLOSE message is simulated to Pyramid for wave3 wave
And SCANWEIGH is generated by pyramid SHIPREQUEST event is sent to shipping service for wave3 wave
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
And SHIPCONFIRM message is sent by Pyramid after store package is shipped
And Cartons are moved to Shipped status after publishing LOGD via DC20_PUBSUB