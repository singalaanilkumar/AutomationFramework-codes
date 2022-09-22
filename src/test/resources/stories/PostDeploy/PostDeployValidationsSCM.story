Meta:
@issue DC2OP-84
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Validate SCM UI after new Release
As a E2E Quality assurance person
I want to Perform Post Deployment Validations

Scenario: Post Depolyment Validations
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BH13181
Given user logs into PROD SCM UI
Given Validate DRR Page
Then Validate PO Inquiry
Then Validate PO Detail
Then Validate PO Distro
Then Validate PO Dashboard
Examples:
|PO_NBR |
|4828151|