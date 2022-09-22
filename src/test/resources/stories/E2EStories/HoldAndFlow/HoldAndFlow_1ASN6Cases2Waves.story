Meta:
@issue HAF-1
@tags Project: E2E Integration testing, Product:HAF,Program: Off-Price
@automatedBy Rajanesh_Kuruppath

Narrative:
In order to close an ASN receipt
As an E2E Quality assurance person
I want to process HoldAndFlow PO through DC20 application

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for HAF

Scenario: HAF From Receiving to shipconfirm

Meta:
@acceptance
@id HAF-SC-01
@tag Type:acceptance , Type:regression , Type:e2e , Module: receiving to shipping, productName HAF
@automatedBy B006110

Given HAF PO details from HAF_2SKU
And Clean up HAF data
Then pyramid publishes CONTRECEIVE messages for all cases and validate
Given user loged in and selected Putaway_Pallet of DC2.0
Then perform putaway as PA09A043:4,PA09A045:2 and validate
When received RTFs for the cases and validated
|templateName             |requestParams                                                                                                                                                       |
|SingleShipmentRTF.json|{#orderID:D-7,#shipNbr:D-7,#shipToStr:6521,#expShpDt:DATE,#holdDt:DATE,#OrdConfTS:CAL-2,#evntTS:CAL,#batchIdGroup:299,#batchId:D-5,#batchOrderCount:1,NumberOfcases:4},{#orderID:D-7,#shipNbr:D-7,#shipToStr:6363,#expShpDt:DATE+2,#holdDt:DATE+2,#OrdConfTS:CAL-2,#evntTS:CAL,#batchIdGroup:300,#batchId:D-5,#batchOrderCount:1,NumberOfcases:2}|
Then preview and Run the wave and validate
|requestParams|
|{#startShpDt:DATE,#endShpDt:DATE+1,#efctStartDt:DATE-2,#efctEndDt:DATE,#dept:299},{#startShpDt:DATE+2,#endShpDt:DATE+4,#efctStartDt:DATE,#efctEndDt:DATE+2,#dept:299}|
When waves are releaseToPick and validated
When user click on main menu HAF
Then pick and stage all cases
And validate activities and case after picking
When waves are releaseToPresort and validated
Then presort cases to HF17A031 and validate
And validate cases and totes after Presort
When waves are releaseToPTS and validated
Then release all the totes
When totealloc messages and totes are validated
Then publish UNITPUT for all cartons
And close all cartons
And weigh and manifest all cartons
When closecarton message subscriber is available
Then make all cartons shipReady
Then validate closecarton messages are published and valid