Meta:
@issue DC2O-132
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy Mythili_Madhavaram

Narrative:
As a E2E Quality assurance person
I want to process Diagnostics page through Supplychain UI for L1/L2 support

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for Diagnostics


Scenario: Diagnostics page validation
Meta:
@acceptance
@id DC20-132
@tag Type:acceptance , Type:regression , Type:e2e , Module: Diagnostics
@automatedBy Mythili_Madhavaram

Given PO details fetched from test data for Sample with WHM status as OPEN,NONASN,ACK
Given user logged in supplychain and selected Diagnostics of SupportUI
When User validates Diagnostics screen with a valid rcptNbr
Then User now validates Diagnostics screen with an invalid rcptNbr
Then User clears the fields


