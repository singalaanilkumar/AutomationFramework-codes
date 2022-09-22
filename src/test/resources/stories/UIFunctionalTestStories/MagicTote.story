Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy BH13650

Narrative:
As a E2E Quality assurance person
I want to validate Magic Tote Functionality

Scenario: Magic Tote page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: InventoryInquiry
@automatedBy BH13650

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Given the details of the Totes
Given wsm activities are created for Magic Tote
Given User logs in to RF application, selects DC2.0 RF Options
When select MagicTote
Then scan a bad TOte and validate the activity of the tote




