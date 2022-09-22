Meta:
@issue Sample
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy
Narrative:
From Wave dashboard default result search validating a random wave dashboard detail with DB detail

Scenario: Wave Dashboard Default search 
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH14352

Given user logged in supplychain and selected WavesinProgress of Wave
Then User searches and validates WAVE Dashboard Results for DEFAULT
When User click on WaveNo