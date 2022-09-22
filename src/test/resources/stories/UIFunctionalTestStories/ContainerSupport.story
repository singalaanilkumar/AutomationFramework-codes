Meta:
@issue DC2O-113
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy Mythili_Madhavaram

Narrative:
As a E2E Quality assurance person
I want to process Container Support page through Supplychain UI for L1/L2 support

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for ContainerSupport


Scenario:1 Consume Container - Single
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#toteNum:50-D-18,#lineBarcod:D-12,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Consume Container
Then User enters a single valid container barcode
Then User enters invalid container barcode
And User clears the form

Scenario:2 Consume Container - Multiple
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Consume Container
When User selects multiple and enters multiple container valid barcodes
Then validate error with one valid and one invalid barcode
And User clears the form

Scenario:3 Consume Container - Multiple with already consumed barcodes
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Consume Container
When User selects multiple and enters multiple container valid barcodes
Then validate error with two valid and consumed barcodes
And User clears the form


Scenario:4 ConsumeRecreate Container when target doesn't exist already
Meta:
@acceptance
@id DC20-113
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram
Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:TOTE,#contStat:CRE,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Consume & Recreate
Then User now enters a source,target container valid barcodes
Then validate the invalid barcodes
And User clears the form

Scenario:5 ConsumeRecreate Container when source is invalid
Meta:
@acceptance
@id DC20-113
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Consume & Recreate
Then User now enters an invalid source and valid target container
And User clears the form

!-- Update container
Scenario:6 Update Container - success scenario with generating system payload and successfully updating
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Update Container
Then User enters container barcode and clicks on retrieve
And User clicks Move button and then updates

Scenario:7 Update Container -No inventory for invalid container barcode
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Update Container
Then User clicks on retrieve after entering invalid container barcode

Scenario:8 Update Container - update failed with incorrect payload -schema validation
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Update Container
Then User enters container barcode and clicks on retrieve
Then User enters invalid payload and clicks update

Scenario:9 Update Container - update failed with wrong payload -update failure
Meta:
@acceptance
@id DC20-112
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerSupport
@automatedBy Mythili_Madhavaram

Given Inventory Created
|requestUrl                       |templateName                     |requestParams                                                                                                                                                                                              		  |
|InventoryServices.CreateInventory|CreateTote.json |{#contNum:50-D-18,#contTyp:BINBOX,#contStat:PTW,#PO:D-7,#Receipt:D-7,#item:D-11,#qty:D-2,#itmStat:AVL,#lineBarcod:812089310764,#dept:803}|
Given user logged in supplychain and selected ContainerSupport of ResearchInventory
When User enters incidentNbr and selects action as Update Container
Then User enters container barcode and clicks on retrieve
Then User enters wrong payload and clicks update