Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for one line item and distro to one existing store
As a E2E Quality assurance person
I want to process PO through Dc four walls


Scenario: E2E flow for load lane and pick to carton
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote to Pick To Carton, productName RF transaction
@automatedBy B006115


Given Reset scenario
Given Get PO line details are fetched from test data
|PO_NBR|RCPT_NBR|
|998999682|9171323|
When Clean the inventory and activity of the CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
!-- |492600424070|12,17,31,20|CA01A007
!-- |492600424087|7,6,37,30|CA01A008
!-- |492600424094|20,20|CA01A008
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
When Clean the inventory and activity of the KA01A009 location
And Clean the inventory and activity of the KA01A010 location
And Reset status to VSC of container LCN KA01A009
And Reset status to VSC of container LCN KA01A010
And user scans the tote and completes the load location with location(s) KA01A009,KA01A010
And User PTC releases receipt for the PO
Then WSM tasks are created for PTC release
And location status updated to PTCRLS for PTC release
When user scans the location and carton and completes the pick to carton