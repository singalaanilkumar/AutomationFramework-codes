Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process POInquiry through Supplychain UI

Scenario: Report Id and Quick Print Button Validation
Meta:
@acceptance
@id DCUA-205,DCUA-207
@tag Type:acceptance , Type:regression , Type:e2e , Module: POInquiry
@automatedBy BH15935

Given PO details from Sample
Given user signed in supplychain and selected POInquiry
When user enters PO Number from Sample and search PO
Then User validates Report ID for Po displayed in PO Inquiry page
Then User validates Quick Print Button displayed in Inquiry page

