Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process Manifest through Supplychain UI

Scenario: Manifest page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Manifest
@automatedBy BH14352

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Then System sends STOREALLOC message for full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
And WSM tasks are created for the VAS and Release Lane(RF)
When User releases the lane for the totes using RF Release Lane option
Then WSM tasks are completed to  release the lane
When Print ticket is done for the totes coming to printing station(s)
Then System updates the printed status for the totes
And validate number of tickets printed
And CONTDIVERT Message route to printticket
When VAS/PREP is performed for totes in PREP area
Then TOTECONT message will be sent to Pyramid
Then WSM activities for the preping are completed
Then CONTDIVERT Message route to P2S
When Totes are diverted to PUT to store and UNITPUT message is sent by Pyramid for moving inventory to carton
Then Inventory is created for outbound cartons and decreased from original totes
And TOTECOMP message is sent by pyramid after totes are emptied
And CONTCLOSED message is sent after the carton is closed
Given user signed in supplychain and selected Manifest
Then User Validates Manifest





