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
|getRequestUrl    				|getUpdateRequestUrl			   |GETQueryParams                                        																																									|
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+2,shipOutEndDate:DATE+6,effectiveStartDate:DATE,effectiveEndDate:DATE+4,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:801;830;808;829;809|
Then Publish Multiple RTF message
|topic                   |templateName    		 	|orderParams                           																     |lineItemTemplate    |lineParams                   												  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6389,#expShpDt:DATE+2,#holdDt:DATE}  |RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:6,#SKU:492608017083}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6390,#expShpDt:DATE+3,#holdDt:DATE+1}		     |RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:6,#SKU:492608017083}							  |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6322,#expShpDt:DATE+4,#holdDt:DATE+2}|RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:5,#SKU:492607612241}{#QTY:4,#SKU:492607643412} |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6324,#expShpDt:DATE+5,#holdDt:DATE+3}    		 |RTFLineItem_PCK.json|{#QTY:5,#SKU:492607538329}{#QTY:4,#SKU:492608017083}{#QTY:10,#SKU:492607284134}|
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:6325,#expShpDt:DATE+6,#holdDt:DATE+4}|RTFLineItem_PCK.json|{#QTY:10,#SKU:492607284134}               									  |
Then validate Published RTF before wave
|requestUrl                     |queryParams                                      																																										 |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+2,shipOutEndDate:DATE+6,effectiveStartDate:DATE,effectiveEndDate:DATE+4,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:801;830;808;829;809|ENR		    |
Given inventory is cleared for the given SKUs
|getRequestUrl                     |deleteRequestUrl                 |GETQueryParams                                                         								  |DELETEQueryParams|
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492607538329;492608017083;492607612241;492607643412;492607284134,status:AVL,containerType:BINBOX|reasonCode:RR    |
|InventoryServices.GetSnapshotItems|InventoryServices.DeleteInventory|barcode:492607538329;492608017083;492607612241;492607643412;492607284134,status:RSV,containerType:BINBOX|reasonCode:RR    |
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateBINBOXForPackawayPull.json |{#contTyp:BINBOX,#contStat:PTW,#PO:7025735,#Receipt:167662032,#barcode:95-D-18,#item:492607538329,#qty:20,#itmStat:AVL,#lineBarcod:240571542865,#dept:801,#ProcessArea:OSC,#parentBarcode:PLT-D-17,#parentConTyp:PLT}|
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
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor=B0$WHM2SUPERUSER    |
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+2,#endShpDt:DATE+3,#efctStartDt:DATE,#efctEndDt:DATE+1,#dept:801;830,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Given User logs in SCM application
When Validate WSM Activities on UI for wave1
Then Cancel WSM Activity in UI and Recreate for wave1
And Unassign WSM Activity in UI for wave1 wave