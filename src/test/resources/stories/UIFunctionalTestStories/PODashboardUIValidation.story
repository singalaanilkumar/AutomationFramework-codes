Meta:
@issue Sample
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for 1 PID and one line item and distro to one existing stores, one new store and packaway
As a E2E Quality assurance person
I want to process PO through Dc four walls

Scenario: PO Details Enquiry
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH04542

Given PO is available for the existing store for end to end processing
|PONbr|ReceiptNbr|
|8991149|6050964|
Then Release Button is enabled and Close button is enabled after POSearch
|4695866|4497626|
When ProcessArea is performed and totes are created for full release and staged to CA01A039 staging location
|SKUUPC|Quantity|StageLocation|
Then Release Button is enabled and Close button is disabled after POSearch
!-- Then Release Button is enabled and Close button is disabled after CreateTote
!-- Then Release Button is enabled and Close button is disabled after Release
!-- Then Release Button is disabled and Close button is enabled after Print

