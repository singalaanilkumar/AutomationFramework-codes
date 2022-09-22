Meta:
@issue Sample
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
From PO dashboard default result search validating a random PO dashboard detail with DB detail

Scenario: PO Dashboard Default search 
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH14352

Given User logs in SCM application
Then User searches and validates PO Dashboard Results for DEFAULT