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
!-- |998576561|9173297|
!-- When Clean the inventory and activity of the CC04A093 location
!-- When Clean all the Open Lane Activities for CC04A093 location
!-- When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
!-- |SKUUPC|Quantity|StageLocation|
!-- Then validate inventory is created and lane is associated with all these totes
!-- When User releases receipt for a PO
!-- Then System sends STOREALLOC message for full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store
Given user signed in supplychain and selected ASN Receipts
Then User navigates to ASN Receipts and validates the messages
