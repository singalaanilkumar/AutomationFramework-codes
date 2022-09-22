Meta:
@issue DC2OP-10
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@Testcase written by BH04632
 

Narrative:
In order to Process single PO with single receipt in OSC area for 1 PID and two line items ,distro to one existing store and one new store and packaway
As a E2E Quality assurance person
I want to process PO through Dc four walls


Scenario: Process single PO with single receipt in OSC area for 2 line items and distro to 1 existing store and 1 new store and packaway
Meta:
@id DC2OP-10_001
@tag Type:E2E, Module:SingleReceipt
@Testcase written by BH04632
!-- Given PO is available for the existing store for end to end processing
!-- When User adds the dept to the attribute list of PO  if missing
!-- Then PO detail UI displays attributes from attribute screen for dept
!-- When User inquires about PO in PO inquiry screen through various ways
!-- |SearchParam             |
!-- |PO Number/ ReceiptNumber|
!-- Then PO inquiry displays the details of the scanned PO
!-- When PO distro UI is provided with receipt number
!-- Then System fetches orignal distro details for PO recipt combination and quantities are validated against threshold
!-- When PO number is passed as input in PO receiving report UI
!-- Then System displays reportId and its details for the corresponding PO

Given Get PO line details are fetched from test data
|PO_NBR|RCPT_NBR|
|4708373|4514301|

When ProcessArea is performed and totes are created and staged to CA01A039 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
When User releases receipt for a PO
!-- Distro should happen for this PO to 1 existing stores and 1 new store and packaway
Then System sends STOREALLOC message with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
!-- And WSM tasks are created for the VAS and Release Lane(RF)
!-- When User releases the lane for the totes using RF Release Lane option
!-- Then WSM tasks are completed to  release the lane
When Print ticket is done for the totes coming to printing station(s)
Then System updates the printed status for the totes
!-- And validate number of tickets printed
!-- -------------------------------------------------------------------------------------------
!-- And CONTROUTE Message with V (VAS) is sent to pyramid and Pyramid in turn sends a CONDIVERT message to V location EOC to acknowledge (above step and this step are same)
!-- --------------------------------------------------------------------------------------------

When VAS/PREP is performed for totes in PREP area

!-- And Split/Move will be performed for New store totes and pack away bin boxes

Then WSM activities for the preping are completed
Then CONROUTE message with P (Packaway) is sent to pyramid and Pyramid in turn sends a CONDIVERT message with P location to acknowledge
When RF Sort pallet and put away is performed for bin boxes

!-- Then PO inquiry UI will be updated for completed and Rcvd units (packaway)
!-- When TOTECONT message will be sent to Pyramid
!-- Then totes (existing & new) are routed to P2S area
!-- And user moves the inventory from tote to carton based on the distro info
!-- And  UNITPUT message is sent by Pyramid
!-- And Inventory is created for outbound cartons and decreased from original totes
!-- And TOTECOMP message is sent by pyramid after totes are emptied
And CONTCLOSED message is sent after the carton is closed and status of the container is updated to packed
!-- When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
!-- When SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area
!-- Then call SHIP VIA labels are printed for new store cartons
!-- And Shipping labels are printed for $existing store cartons
!-- And SHIPCONFIRM message is sent by pyramid after existing store cartons are shipped
!-- And CONTDIVERT message is sent by pyramid and New store cartons wil be diverted to BackHaul area
!-- When New store cartons are nearing the open date for that particular stores
!-- Then SCANWEIGH is done, SHIPINFO is sent to print labels for new store cartons
!-- Then SHIP CONFIRM message is sent by pyramid for new store cartons
!-- And User validates the PO details in PO inquiry UI to confirm recieved and completed units
!-- When Receipt is closed and start/push receiving is done
!-- And ERS validates the receipt closure with G status and books the respective units to the store
!-- And validates CEB and RCV for the closed PO/receipt


Examples:

|USECASE_ID      |

|OSC_PO1_RCPT1_14|