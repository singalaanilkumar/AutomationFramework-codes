Meta:
@issue DC2OP-113
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy BH15496

Narrative:
In order to test Handheld-UAT screens
As a E2E Quality assurance person
I want to Validate Container Inquiry Screen with the service in Postman

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for ContainerInquiry

Scenario: Validate Container Inquiry Screen with services
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Container Inquiry, productName RF transaction
@automatedBy BH15496


Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_SingleSKU with WHM status as OPEN,NONASN
When Clean the inventory and activity of the CB03A030 location
When Clean all the Open Lane Activities for CB03A030 location
When totes are created without staging location
Then click on Locate Container
Then Enter container and assign CB03A030 staging location
Then click on Container Inquiry link to check totes details