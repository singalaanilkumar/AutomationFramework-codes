Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process POInquiry through Supplychain UI

Scenario: POInquiry page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: POInquiry
@automatedBy BH14352

Given user signed in supplychain and selected POInquiry
When user enters fieldvalue into fieldname from examples
Then User validates POInquiry screen with DB
When user click on PO link
Then User validates PODetails screen with DB
When user click on InHouseUPC link
Then User validates PODistro screen with DB
!-- When user click on RetrievePO link
Then User clicks ReportId and update and save POLine Editable attributes for Single SKU

Examples:
|fieldname  |fieldvalue |skuNumber      |lineItemAttributes     |prepAttributes|
|PO         |4723740    |492608031584   |Process Area Conf=OSC  |Prep1=Hanger,Prep2=*Hanger- New Store,Prep3=HAZ MAT,Prep4=Kitting|


