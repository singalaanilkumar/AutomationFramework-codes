Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process a PO with more than 10 UPCs
As a E2E Quality assurance person
I want to process PO through Dc four walls

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for PutToStoreFlow_MoreThan10UPC

Scenario: Open sort count is performed and totes are created for the PO line and staged to a lane
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy B006110

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_10OrMoreUPC_PO with WHM status as OPEN,NONASN,ACK
When Clean the inventory and activity of the CC04A040 location
When ProcessArea is performed and totes are created for full release and staged to CC04A040 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Then System sends STOREALLOC message for full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
And WSM tasks are created for the VAS and Release Lane(RF)
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
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
And SHIPCONFIRM message is sent by Pyramid after store package is shipped
Then Clean the cache
Then Open a new tab