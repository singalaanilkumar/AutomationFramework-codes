Meta:
@issue DC2OP-20_Overage
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
MessagingSupport is to provide L1/L2 support a UI to submit a bunch of different types of Messages

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for MessagingSupport

Scenario:1 Messaging Support validate login and navigate

Meta:
@acceptance
@id DC20-114
@tag Type:acceptance , Type:regression , Type:e2e , Module: Support UI
@automatedBy B006104

Given user logged in supplychain and selected MessagingSupport of MHE


Scenario:2 Messaging Support validate clear is working

Meta:
@acceptance
@id DC20-114
@tag Type:acceptance , Type:regression , Type:e2e , Module: Support UI
@automatedBy B005970
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#toteNum:50-D-18,#lineBarcod:812089310764,#dept:803}|
When user enters incidentNbr and containerBarcode and selects action
And user clicks Move button
And user clicks Clear button

Examples:
|action		|
|CONTROUTE	|
|TOTECONT	|
|STOREALLOC	|


Scenario:3 Messaging Support validate all messages are working

Meta:
@acceptance
@id DC20-114
@tag Type:acceptance , Type:regression , Type:e2e , Module: Support UI
@automatedBy B005970
Given Inventory Created
   |requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
   |InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#toteNum:50-D-18,#lineBarcod:812089310764,#dept:803}|
When user enters incidentNbr and containerBarcode and selects action
And user clicks Move button
And user clicks Run button
Then valid success message is displayed for MessagingSupport

Examples:
|action		|
|CONTROUTE	|
|TOTECONT	|

Scenario:4 Messaging Support validate error message for all actions

Meta:
@acceptance
@id DC20-114
@tag Type:acceptance , Type:regression , Type:e2e , Module: Support UI
@automatedBy B005970
Given Inventory Created
   |requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
   |InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#toteNum:50-D-18,#lineBarcod:812089310764,#dept:803}|
When user enters incidentNbr and containerBarcode and selects action
And user enters invalid payload
And user clicks Run button
Then valid error message is displayed for MessagingSupport

Examples:
|action		|
|CONTROUTE	|
|TOTECONT	|

Scenario:5 Messaging Support validate error message for invalid container barcode

Meta:
@acceptance
@id DC20-114
@tag Type:acceptance , Type:regression , Type:e2e , Module: Support UI
@automatedBy B005970
When user enters incidentNbr and  enters invalid containerBarcode and selects action
Then invalid error message is displayed for containerBarcode

Examples:
|action		|
|CONTROUTE	|
|TOTECONT	|
