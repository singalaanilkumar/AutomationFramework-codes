Meta:
@issue DC2OP-8
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO in BULK area for 1 PID and one line item
As a Bulk and Oversize area sortation associate,
I want to sort to stores based on store distro in Process Flow Bulk

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for BulkSort2Store

Scenario: Bulk sort count is performed and totes are created for the PO line and staged to a lane
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH13626

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_BulkSortToStore with WHM status as OPEN,NONASN,ACK
Given Clean Rediscache by PoRecptNum
When Clean the activity for the given stores
When Clean the inventory and activity of the BA01A112 location
When Clean the inventory and activity of the BA01A018 location
When Clean the inventory and activity of the BA01A114 location
When Clean inventory and activity
When ProcessArea is performed and totes are created for full release and staged to BA01A112 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Then Validate store alloc message in sorting db
And WSM tasks are created for the VAS and Release Lane(RF)
When Print ticket is done for the totes coming to printing station(s)
Then System updates the printed status for the totes
And validate number of tickets printed
And CONTDIVERT Message route to printticket
When VAS/PREP is performed for totes in PREP area
Then Validate the STS Carton activities for store with status OPEN
Then WSM activities for the preping are completed
When Sort To Store is performed in zone 001 and processing location BA01A018 and staged to BA01A114 location
Then Clean the cache
Then Open a new tab