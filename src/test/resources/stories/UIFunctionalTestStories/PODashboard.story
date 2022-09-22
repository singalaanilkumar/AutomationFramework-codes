Meta:
@issue Sample
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Process single PO with single receipt in OSC area for 1 PID and one line item and distro to one existing stores, one new store and packaway
As a E2E Quality assurance person
I want to process PO through Dc four walls

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for PODashboard

Scenario: PO Dashboard validation for a single PO
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH04542

Given Get PO line details are fetched from test data
|PO_NBR |RCPT_NBR|
|993817494|9122388|
Given User logs in SCM application
Then User searches and validates PO Dashboard Results for PO