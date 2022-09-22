Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy BH13650

Narrative:
As a E2E Quality assurance person
I want to validate All UI components through Supplychain UI

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for SupplyChainUIE2Evalidation

Scenario: SupplyChain UI E2E validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerInquiry
@automatedBy BH14352_BC90965
Given Reset scenario
Then Open a new tab
Given Get PO line details are fetched from test data
|PO_NBR|RCPT_NBR|
|4873879|4577923|
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When cleanup exisiting Totes
When WSM activities are cleared
|getRequestUrl    |deleteRequestUrl    |GETQueryParams             |
|WSM.getActivities|WSM.deleteActivities|status:OPEN,poNbr:996995736|
Given Override tables are cleared for ReportID
Given user signed in supplychain and selected POInquiry
When required Inputs are provided
Then User validates POInquiry screen with DB
When user click on PO link
Then User validates PODetails screen with DB
When user click on InHouseUPC link
Then User validates PODistro screen with DB
When user click on RetrievePO link
Then User clicks ReportId and update and save POLine Editable attributes for Single SKU
And User updated the edited values to its original values
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
Given user logged in supplychain and selected InventoryInquiry of ResearchInventory
Then user validate InventoryInquiry
Then user validates InventoryInquiry ATTRIBUTES tab
Then user validates InventoryInquiry ASSOCIATION tab
Given user logged in supplychain and selected LocationView of ResearchInventory
Then user validate LocationView tab for Given location
When user edits 1 in attributes named scheduled-area
When user updates attributes named scheduled-area with original values
When Inventory Adjustment is done for the tote
Given user logged in supplychain and selected InventoryAdjustmentInquiry of ResearchInventory
Then user Validate InventoryAdjustmentInquiry
Then User searches and validates PO Dashboard Results for PO
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Then System sends STOREALLOC message for full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
And User navigates to MHE and validates STOREALLOC messages for given PO Wave
|GETQueryParams                                                                           |																								  							  	    	       |
|#startTRXRangeDt:DATE-1,#endTRXRangeDt:DATE,#messageType:STOREALLOC,#textFilter:#PONumber|
And WSM tasks are created for the VAS and Release Lane(RF)
When Print ticket is done for the totes coming to printing station(s)
Then System updates the printed status for the totes
And validate number of tickets printed
And CONTDIVERT Message route to printticket
When VAS/PREP is performed for totes in PREP area
Then TOTECONT message will be sent to Pyramid
Given SupplyChain home page
Then On SCMUI Validate WSM Activities for PO wave in OPEN Status
Then WSM activities for the preping are completed
Then CONTDIVERT Message route to P2S
When Totes are diverted to PUT to store and UNITPUT message is sent by Pyramid for moving inventory to carton
Then Inventory is created for outbound cartons and decreased from original totes
And TOTECOMP message is sent by pyramid after totes are emptied
And CONTCLOSED message is sent after the carton is closed
Given user signed in supplychain and selected Manifest
Then User Validates Manifest
Then Clean the cache
Then Open a new tab