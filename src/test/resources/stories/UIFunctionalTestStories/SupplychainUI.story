Meta:
@issue HAF-1
@tags Project: E2E Integration testing, Product:Supplychain,Program: Off-Price
@automatedBy Jyothsna Gogireddy

Scenario: Clicking POInquiry in Supplychain UI

Meta:
@acceptance
@id HAF-SC-01
@tag Type:acceptance , Type:regression , Type:e2e , Module: receiving to shipping, productName HAF
@automatedBy BH14352

Given SupplyChain home page
When user click on supplychain main menu POInquiry
Scenario: Clicking Container Inquiry in ResearchInventory
Meta:
@acceptance
@id HAF-SC-01
@tag Type:acceptance , Type:regression , Type:e2e , Module: receiving to shipping, productName HAF
@automatedBy BH14352

When user click on supplychain main menu ResearchInventory
When user click on supplychain sub menu ContainerInquiry
Scenario: Clicking Location Components in Location of DCConfig
Meta:
@acceptance
@id HAF-SC-01
@tag Type:acceptance , Type:regression , Type:e2e , Module: receiving to shipping, productName HAF
@automatedBy BH14352

When user click on supplychain main menu DCConfig
When user click on supplychain sub menu Location
When user click on supplychain child menu LocationComponents