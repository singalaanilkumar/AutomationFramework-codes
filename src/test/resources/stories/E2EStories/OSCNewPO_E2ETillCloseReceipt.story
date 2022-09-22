Meta:
@issue DC2OP-156
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy Rajanesh Kuruppath

Narrative:
In order to Process single PO with single receipt in OSC area for one line item and distro to more than one existing store
As a E2E Quality assurance person
I want to process a brand new PO through DC four walls

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for OSCNewPO_E2ETillCloseReceipt


Scenario: OSC E2E with New PO and Receipt
Meta:
@acceptance
@id DC2OP-156
@tag Type:acceptance , Type:regression , Type:e2e , Module: E2E, productName RF transaction
@automatedBy B006110
Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for OSC_SINGLESKU with WHM status as OPEN
When Clean the inventory and activity of the CC03A012 location
When ProcessArea is performed and totes are created for full release and staged to CC03A012 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
When disabled the receipt in QA TransLog table to prevent reuse
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
When MMS CLSCTN message pull-whm-orderfulfillment-mms-shipcntr-newPO-east4 subscriber is available
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then validate MMS CLSCTN messages for PO are published and valid
And SHIPCONFIRM message is sent by Pyramid after store package is shipped
Then Cartons are moved to Shipped status after publishing LOGD via ENROUTE
Then receipt is closed from PODashboardUI
Then validate CloseReceipt from ERS