Meta:
@issue DC2OP-118
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy BH15496

Narrative:
In order to test Handheld-UAT screens
As a E2E Quality assurance person
I want to Validate Build Pallet Screen with the service in Postman

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for BuildPalletScreens

Scenario: Validate Build Pallet Screen with services
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Build Pallet, productName RF transaction
@automatedBy BH15496

Given Reset scenario
Then Open a new tab
Given PO details fetched from test data for E2E_MultiPleSKU_OSC with WHM status as OPEN,NONASN,ACK
When Clean the inventory and activity of the CB03A030 location
When Clean all the Open Lane Activities for CB03A030 location
Given User logs in to RF application, selects DC2.0 RF Options
Then user start building pallets and locate to RRRR001 location
When user navigate to Homescreen
Then click on Container Inquiry link to check details after Build Pallet Operation
Then user start building pallets and click on end Pallet Btn
When user navigate to Homescreen
Then click on Container Inquiry link to check details after End Pallet Operation
When navigate to Split Adjust Pallet screen
Then scan pallet and validate Split Adjust screen
When user click on Split button
Then scan pallet and validate Split pallet screen
And locate the Pallet to staging location and validate the status of the pallet
When user navigate to Menuscreen
Then click on Container Inquiry link to check details after Split Pallet Operation
When navigate to Split Adjust Pallet screen
Then scan pallet and validate Split Adjust screen
When user click on Adjust button
Then edit containers and validate Adjust pallet screen
And locate the Pallet to staging location and validate the status of the pallet
When user navigate to Menuscreen
Then click on Container Inquiry link to check details after Adjust Pallet Operation
Then move pallet to staging CS01A001 location
Then click on Container Inquiry link to check details after Move Pallet Operation
Then consume pallet container
