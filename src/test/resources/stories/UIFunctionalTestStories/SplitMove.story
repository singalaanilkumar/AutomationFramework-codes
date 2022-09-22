Meta:
@issue Split Move Inventory Validation
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy BH13181_Mahalakshmi_Subbaiah

Narrative:
In order to validate Split Move Inventory Validation
As a E2E Quality assurance person
I want to split the quantity from source container to target container

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for SplitMove Inventory Validation

Scenario: SplitMove Inventory Validation
Meta:
@acceptance
@id SplitMove Inventory Validation
@tag Type:endToend , Module:SplitMoveInventory
@automatedBy BH13181_Mahalakshmi_Subbaiah

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
Then inventory is created for Split Move
Given User logs in to RF application, selects DC2.0 RF Options
Then source container quantity is split and moved to target container