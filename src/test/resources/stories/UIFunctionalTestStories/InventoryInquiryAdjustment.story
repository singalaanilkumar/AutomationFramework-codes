Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process Inventory Adjustment Inquiry through Supplychain UI

Scenario: Inventory Adjustment Inquiry page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: InventoryAdjustmentInquiry
@automatedBy BH14352

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When Inventory Adjustment is done for the tote
Given user logged in supplychain and selected InventoryAdjustmentInquiry of ResearchInventory
Then user Validate InventoryAdjustmentInquiry
!-- Then user validates InventoryAdjustmentInquiry ATTRIBUTES tab





