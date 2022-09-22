Meta:
@issue DetailReceivingReport
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for one line item and distro to one existing store
As a E2E Quality assurance person
I want to process PO through Dc four walls

Scenario: Open sort count is performed and totes are created for the PO line and staged to a lane
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy B006115

Given Reset scenario
Given Get PO line details are fetched from test data
|PO_NBR|RCPT_NBR|
|996847141|9127784|
When PO number is passed as input in PO Inquiry UI
Then System displays reportId and its details for the corresponding PO





