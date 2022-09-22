Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:SupplychainUI,Program: Off-Price
@automatedBy

Narrative:
As a E2E Quality assurance person
I want to process ContainerInquiry through Supplychain UI

Scenario: ContainerInquiry page validation
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerInquiry
@automatedBy BH14352

Given Reset scenario
Then Open a new tab
Given Get PO line details are fetched from test data
|PO_NBR|RCPT_NBR|
|4720088|4518151|
When Clean the inventory and activity of the CC04A093 location
When Clean all the Open Lane Activities for CC04A093 location
When ProcessArea is performed and totes are created for full release and staged to CC04A093 staging location
|SKUUPC|Quantity|StageLocation|
Then validate inventory is created and lane is associated with all these totes
Given user logged in supplychain and selected ContainerInquiry of ResearchInventory
Then user validate ContainerInquiry for ToTE
Then user validates ContainerInquiry DETAILS tab
Then user validates ContainerInquiry ATTRIBUTES tab
Then user validates ContainerInquiry ASSOCIATIONS tab


Scenario: ContainerInquiry page's Spinner validation
Meta:
@acceptance
@id DCUA-311
@tag Type:acceptance , Type:regression , Type:e2e , Module: ContainerInquiry
@automatedBy BH15935

Given user logged in supplychain and selected ContainerInquiry of ResearchInventory
When User Select container type with CRT
When User Select attribute type with ShipVia and enter attribute value as SHL and search result





