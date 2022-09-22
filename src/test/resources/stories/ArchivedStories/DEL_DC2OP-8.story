Meta:
@issue DC2OP-8
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for 1 PID and one line item and distro to one existing stores and one new store
As a E2E Quality assurance person
I want to process PO through Dc four walls

Scenario: Open sort count is performed and totes are created for the PO line and staged to a lane
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH04542


Given Get PO line details are fetched from test data
|PO_NBR |RCPT_NBR|
|997613350|9140126|
When ProcessArea is performed and totes are created and staged to CA01A036 staging location
|SKUUPC|Quantity|StageLocation|
!-- |492607034227|4|CA01A033|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
Then System sends STOREALLOC message with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
!-- And WSM tasks are created for the VAS and Release Lane(RF)
!-- When User releases the lane for the totes using RF Release Lane option
!-- Then WSM tasks are completed to  release the lane
When Print ticket is done for the totes coming to printing station(s)
Then System updates the printed status for the totes
And validate number of tickets printed
When VAS/PREP is performed for totes in PREP area
Then TOTECONT message will be sent to Pyramid
Then WSM activities for the preping are completed
When Totes are diverted to PUT to store and UNITPUT message is sent by Pyramid for moving inventory to carton
Then Inventory is created for outbound cartons and decreased from original totes
And TOTECOMP message is sent by pyramid after totes are emptied
And CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then SHIPCONFIRM message is sent by Pyramid after store package is shipped
!-- And User validates the PO details in PO inquiry UI to confirm recieved and completed units
!-- When Receipt is closed and start/push receiving is done
!-- And ERS validates the receipt closure with G status and books the respective units to the store
!-- And validates CEB and RCV for the closed PO/receipt
