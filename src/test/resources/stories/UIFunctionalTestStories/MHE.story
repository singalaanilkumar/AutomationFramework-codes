Meta:
@issue DC2OP-103
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process POInquiry through Supplychain UI

Scenario: MHE Page
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH13181

!-- Given Reset scenario
!-- Then Open a new tab
!-- Given Get PO line details are fetched from test data
!-- |PO_NBR|RCPT_NBR|
!-- |4723740|4518754|
!-- When Clean the inventory and activity of the CC04A093 location
!-- When Clean all the Open Lane Activities for CC04A093 location
!-- When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
!-- |SKUUPC|Quantity|StageLocation|
!-- Then validate inventory is created and lane is associated with all these totes
!-- When User releases receipt for a PO
!-- Then System sends STOREALLOC message for full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
Given user logged in supplychain and selected MheSearch of MHE
Then User navigates to MHE and validates the messages
|MHEDetails                                                                                                  |																									  							  	    	       |
|#startTRXRangeDt:DATE-11,#endTRXRangeDt:DATE-11,#messageType:STOREALLOC,#Status:SENT_TO_DEST,#textFilter:9173297|
Then message is updated using Edit and Reprocessing is done
!-- When sucessful message is updated as PUBLISH_FAILED message
!-- When select 1 message(s) for reprocessing
!-- And click on button Details
!-- And click on button Edit
!-- And click on button Cancel
!-- And click on button Edit
!-- Then edit allowed payload
!-- And click button Save
!-- And click on button Reprocess save the updated payload
!-- And validate updated payload with payload from ui for that sequence
