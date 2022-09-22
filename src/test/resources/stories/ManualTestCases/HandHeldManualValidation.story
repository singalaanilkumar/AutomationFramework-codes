Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Validate Handheld UI after new Release
As a E2E Quality assurance person
I want to Perform all HandHeld UI screen Validations

Scenario: Locate Container
Login to HH
Select DC2.0
Select Locate Container
Scan valid Container
Scan the staging location -- Need to add few more steps

Scenario: Exceptional Lane
Login to HH
Select DC2.0
Select create Tote
Select Processing area
Select Single SKU
Enter Tote Id - 50000000009876546798 (example)
Enter SKU barcode - 157321358295
Enter qty -10
Enter location ID - Here Enter Exceptional lane ID - IC01A001
Login to SCM UI
Click on POdashboard option
Search for the PO for which totes are created
CLick on Release Button
Select the Checkbox and click on releaesbyDistro button
Login to HH
Select DC2.0
Select Exceptional Lane
Scan Exceptional Lane ID - IC01A001
Validate Exceptional lane details
Enter Number of Totes and hit enter
An Alert pop up should be displayed with Exceptional lane Id is released successfully
Click on OK button in the displayed pop up

Scenario: Magic Tote
Login to HH
Select DC2.0
Select Magic Tote
Scan container barcode
-------- Need some info on this -----------------------

Scenario: Load Lane
Login to HH
Select DC2.0
Select Load Lane
Scan container ID -- This doesnt not accept OSC BTY and BLK processing area
-------- Need some info on this -----------------------

Scenario: Pick To Carton
Login to HH
Select DC2.0
Select Pick To Carton
Scan Drop location or Pallet
-------- Need some info on this -----------------------


