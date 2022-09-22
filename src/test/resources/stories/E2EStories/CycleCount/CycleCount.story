Meta:
@issue cyclecount-1
@tags Project: E2E Integration testing, Product:cyclecount,Program: Off-Price
@automatedBy Rajanesh_Kuruppath and Raja Reddy

Narrative:
In order to do collect invetory details
As an employee at Dc20 warehouse
I want to perform cyclecount using handheld devices

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for CycleCount

Scenario:1 Successfully performing cyclecounts of all the available containers
Meta:
@acceptance
@id cyclecount-SC-01
@tag Type:acceptance , Type:regression , Type:e2e , Module: cyclecount, productName cyclecount
@automatedBy Rajanesh_Kuruppath and Raja Reddy

Given inventory is cleared for the given locations
|getRequestUrl                    |deleteRequestUrl                 |GETQueryParams  |DELETEQueryParams|
|InventoryServices.CreateInventory|InventoryServices.DeleteInventory|barcode:PA20C035|reasonCode:RR    |
|InventoryServices.CreateInventory|InventoryServices.DeleteInventory|barcode:PA20C036|reasonCode:RR    |
|InventoryServices.CreateInventory|InventoryServices.DeleteInventory|barcode:PA20C037|reasonCode:RR    |
Given Inventory created with valid location
|requestUrl                       |templateName                       |requestParams                                                                                                                                                                                 |
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:BINBOX,#contStat:VSC,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:BINBOX,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C035,#parentConTyp:LCN}  |
Given Inventory located at incorrect location
|requestUrl                       |templateName                       |requestParams                                                                                                                                                                                 |
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:BINBOX,#contStat:VSC,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:BINBOX,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C036,#parentConTyp:LCN}  |
Given Inventory created without location
|requestUrl                       |templateName                         |requestParams                                                                                                                                                         |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocationCC.json|{#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithNOLocationCC.json|{#contTyp:BINBOX,#contStat:VSC,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithNOLocation.json  |{#contTyp:BINBOX,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithNOLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocationCC.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocation.json  |{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithNOLocation.json  |{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentConTyp:LCN}  |
Given Inventory created for LWContainers Cycle Count
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                 |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:BINBOX,#contStat:VSC,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:BINBOX,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:95-D-18,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}|
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:000-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:PTW,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:SPK,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
|InventoryServices.CreateInventory|CreatecontainerswithLocation.json|{#contTyp:CSE,#contStat:CRE,#PO:D-7,#Receipt:D-9,#barcode:063-D-17,#item:D-12,#qty:D-2,#itmStat:AVL,#lineBarcod:D-12,#dept:8-D-2,#ProcessArea:OSC,#parentBarcode:PA20C037,#parentConTyp:LCN}  |
Given Random Inventory with Valid Barcode Created
Given Random Inventory with InValid Barcode Created
Given user signed in and selected DC2.0
Then perform cycle count for ValidInventorywithLocation
Then perform cycle count for ValidInventorywithIncorrectLocation
Then perform cycle count for ValidInventorywithNoLocation
Then perform cycle count for ValidSystemInventoryLW
Then perform cycle count for SystemicallyNotExistingInventory
Then perform cycle count for NotSupportedContainers