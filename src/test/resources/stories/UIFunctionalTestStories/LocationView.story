Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process Location View through Supplychain UI

Scenario: Location View page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: LocationView
@automatedBy BH14352

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
Given user logged in supplychain and selected LocationView of ResearchInventory
Then user validate LocationView tab for Given location
When user edits 1 in attributes named scheduled-area
When user updates attributes named scheduled-area with original values





