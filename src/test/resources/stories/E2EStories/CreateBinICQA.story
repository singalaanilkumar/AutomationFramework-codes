Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy BH16643_Varun

Narrative:
In order to Validate Create Bin through ICQA
As a E2E Quality assurance person
I want to Create Bin using ICQA and validate CONTROUTE, WSM activity

Scenario: Create Bin Validations
Meta:
@acceptance
@id WMSKTLO-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Bin, productName RF transaction, ICQA
@automatedBy BH16643_Varun

Given Reset scenario
Then Open a new tab
When WSM activities are cleared
| getRequestUrl     | deleteRequestUrl     | GETQueryParams                                      |
| WSM.getActivities | WSM.deleteActivities | upc:492608017113,type:PACKAWAY,status:OPEN;ASSIGNED |
| WSM.getActivities | WSM.deleteActivities | upc:492608017120,type:PACKAWAY,status:OPEN;ASSIGNED |
Given inventory is cleared for the given SKUs
| getRequestUrl                      | deleteRequestUrl                  | GETQueryParams                                                    | DELETEQueryParams |
| InventoryServices.GetSnapshotItems | InventoryServices.DeleteInventory | barcode:492608017113;492608017120,status:AVL,containerType:BINBOX | reasonCode:RR     |
| InventoryServices.GetSnapshotItems | InventoryServices.DeleteInventory | barcode:492608017113;492608017120,status:RSV,containerType:BINBOX | reasonCode:RR     |
Given PO details fetched from test data for E2E_MultiPleSKU_OSC with WHM status as CLOSE
Given Clean Rediscache by PoRecptNum
When Clean the inventory and activity of the PS07A040 location
When User creates bins with ICQA CreateBin Transaction having QD ReasonCode
| SKU          | Quantity |
| 492608017113 | 100      |
| 492608017120 | 100      |
| 492608017113 | 100      |
Then CONTROUTE messages are validated for all the created bins sent to Pyramid for CreateBin Transaction
And Validate Inventory and PACKAWAY Activities for Created Bins with containerStatusCode VSC and statusCode NVL in OPEN Status
When Pack away Sort is done for the ICQA multiple Bins
Then locate BinBox to processing stage location for create bin ICQA
|requestUrl                       |templateName        |requestParams                              |
|InventoryServices.LocateContainer|LocateContainer.json|{#parentConTyp:LCN,#parentBarcode:PR01A001}|
Then perform putaway on created ICQA binboxes as PA09A043:3 and validate
Given RTF Details are cleared for SKUs
|getRequestUrl              |getUpdateRequestUrl            |GETQueryParams                                                                                                                                                     |
|OrderfulfillmentServices.GetRTF|OrderfulfillmentServices.UpdateRTF|shipOutStartDate:DATE+8,shipOutEndDate:DATE+9,effectiveStartDate:DATE+7,effectiveEndDate:DATE+8,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77|
Then Publish RTF message
|topic                   |templateName           |orderParams                                                                                 |lineItemTemplate    |lineParams                 |
|pubSub.topics.publishRTF|SingleShipmentRTF_PCK.json|{#orderID:D-7,#OrdConfTS:CAL-2,#evntTS:CAL,#shipNbr:D-7,#shipToStr:7278,#expShpDt:DATE+8,#holdDt:DATE+7}|RTFLineItem_PCK.json|{#QTY:100,#SKU:492608017113}{#QTY:50,#SKU:492608017120}|
Then validate Published RTF before wave
|requestUrl                     |queryParams                                                                                                                                                         |expectedStatus|
|OrderfulfillmentServices.GetRTF|shipOutStartDate:DATE+8,shipOutEndDate:DATE+9,effectiveStartDate:DATE+7,effectiveEndDate:DATE+8,fulfillmentLocnNbr:7222,flowType:PMR,reqQnty:1000,status:UNRELEASED,orderLimit:1000,divNbr:77,deptNbrs:856|ENR             |
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams                                       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:001            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:001       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:002            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:002       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:003            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:003       |
|WSM.getActivities|WSM.deleteActivities|type:BINPULL,status:OPEN;READY,pullZone:005            |
|WSM.getActivities|WSM.deleteActivities|type:BINPULLSPLIT,status:OPEN;READY,pullZone:005       |
|WSM.getActivities|WSM.deleteActivities|status:ASSIGNED,actor:B0$WHM2DEV                       |
|WSM.getActivities|WSM.deleteActivities|status:OPEN;ASSIGNED,type:STSCARTON,container:7278;7277|
Then preview and Run the PCKPULL wave1 wave and validate
|requestParams   |
|{#startShpDt:DATE+8,#endShpDt:DATE+9,#efctStartDt:DATE+7,#efctEndDt:DATE+8,#dept:820;830;856,#waveType:OSC,#storeType:REGULAR,#noOfUnits:1000,#noOfOrdersLimit:1000}|
Then Validate WSM Activities for wave1 wave in READY Status
|SKU        |ActivityType|Count|
|492608017113|BINPULLSPLIT|1   |
|492608017120|BINPULL     |1   |
|492608017120|SPLIT       |2   |
Then validate the inventory for wave1 wave using GET service
|invRequestUrl                     |invQueryParams                                          |
|InventoryServices.GetSnapshotItems|barcode:492608017113;492608017120,status:RSV,containerType:BINBOX|
Then Release wave1 wave
Given User logs in to RF application, selects DC2.0 RF Options
When packaway pull activities are completed for wave1 wave for given actions
|SKU        |ACTION|LOCATION|STATUS  |
|492608017113|NIL      |        |OPEN |
|492608017120|COMPLETE |         |OPEN |
Then validate the inventory attribute values for the cancelled bin
|requestUrl                       | queryParams               | targetCondCode |
|InventoryServices.CreateInventory| barcode:container.barCode | PTW,NVL,LW     |
Then Clean the cache
Then Open a new tab


