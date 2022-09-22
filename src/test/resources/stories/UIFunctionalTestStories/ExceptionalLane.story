Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy BH13650

Narrative:
As a E2E Quality assurance person
I want to validate Exceptional Lane Functionality

Scenario: ExceptionalLane page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: InventoryInquiry
@automatedBy BH13650

Given Reset scenario
Then Open a new tab
!-- Given PO details fetched from test data for E2E_MultiPleSKU_OSC with WHM status as OPEN,NONASN
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the IC01A001 location
When Clean all the Open Lane Activities for IC01A001 location
When ProcessArea is performed and totes are created for full release and staged to IC01A001 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Given user loged in and selected Exception_Lane of DC2.0
When user enter IC01A001 Exception Lane Ids
Then validated Exception Lane used screen details with API
And Enter the Actual qty and validate the activity for used lane






