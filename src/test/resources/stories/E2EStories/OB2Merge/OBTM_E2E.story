Meta:
@issue DCOA-506
@tags Project: End to End, Product:OB2Merge,Program: Off-Price
@automatedBy BH16754

Narrative:
In order to Process cartons from CFC to merge
As a E2E Quality assurance person
I want to process all the cartons from CFC

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for CFC_PackagesTillLoadComplete
Scenario:1 from cartons from cfc till lastscan and tieing into an appointment
Meta:
@acceptance
@id DCOA-506
@tag Type:acceptance , Type:regression , Type:e2e , Module: OB through merge, productName: CFC OB through merge
@automatedBy BH16754
Given 2 HAF CFCcartons in manifested status
|requestUrl                    |topicName              |templateName  |requestParams                                                                                                                   |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|cfcCarton.json|{#barcode:150003-D-14,#locNbr:4420,#status:MFT,#destLoc:6329,#shipVia:6396,#tName:DivertShip,#divertshipTS:CAL,#laneId:E2E0101,#trkNbr:1Z-D-15}|
Then create an legacy appointment in Loading status
|requestUrl                    |topicName                      |templateName          |requestParams                                                                                                                                                                                                                             |
|TestingServices.publishMessage|pubSub.topics.createAppointment|createAppointment.json|{#apptNbr:D-8,#apptStat:2,#apptType:5,#dest:6396,#vndNbr:D-3,#trlNBR:D-6,#yard:D-1,#yrdArea:D-2,#carrietType:D-2,#noOfcartons:D-3,#origin:4420,#frgtTyp:D-2,#trlClass:D-2,#createTS:CAL,#orgArvlTS:CAL+2,#trlNbr:TRLE2E-D-2,#doorNbr:S424}|
Given user logs in and selected DockScan of Outbound for GOODYEAR
Then scan door S424 and select lastscan
When Do Last scan on last carton, on that door S424
And validate legacy cartons are tied to the appointment
When update the appointment as loadComplete

Scenario:2 from package from SDC and then divert till lastscan and tieing into an appointment
Meta:
@acceptance
@id DCOA-506
@tag Type:acceptance , Type:regression , Type:e2e , Module: OB through merge, productName: CFC OB through merge
@automatedBy BH16754
Given 2 BACKSTAGE package in PCK status
|requestUrl                  |templateName      |requestParams                                      |
|packageService.createPackage|CreatePackageForE2E.json|{#barcode:150003-D-14,#status:PCK,#processArea:OSC,#destLocNbr:5052,#upc:4900-D-12,#orderNo:414-D-7,#oDetail:844-D-12,#qty:D-2,#upc1:4900-D-12,#orderNo1:414-D-7,#oDetail1:844-D-12,#qty1:D-2}
Then weigh and manifest all cartons
Then validate carton Ship via details
Then validate the Ship info
|getRequestUrl    	|GETQueryParams                         |
|Messaging.getMsgURL|textFilter:#carton,messageType:SHIPINFO,fromDate:CAL,toDate:CAL+1|
Then divert the package through Pyramid to E2E0101
Then create an appointment in Loading status
|requestUrl                    |topicName                      |templateName          |requestParams                                                                                                                                                                                                                             |
|TestingServices.publishMessage|pubSub.topics.createAppointment|createAppointment.json|{#apptNbr:D-8,#apptStat:2,#apptType:5,#dest:3977,#vndNbr:D-3,#trlNBR:D-6,#yard:D-1,#yrdArea:D-2,#carrietType:D-2,#noOfcartons:D-3,#origin:7221,#frgtTyp:D-2,#trlClass:D-2,#createTS:CAL,#orgArvlTS:CAL+2,#trlNbr:TRLE2E-D-2,#doorNbr:S501}|
!-- Given update Package
!-- |requestUrl                       |updateParam                          |
!-- |packageService.updatePackageMulti|{divertshipSeq:true}|
Given user logs in and selected DockScan of Outbound for COLUMBUSDC
Then scan door S501 and select lastscan
When Do Last scan on last carton, on that door S501
And validate Backstage cartons are tied to the appointment
!-- validate load Inquire details
When User selects exit
And user click on sub menu LoadInquiry
When scan door S122 number
Then Validate Load Inquiry details for door
When User selects exit
And user click on sub menu LoadInquiry
Then Validate Load Inquiry details for carton
When update the appointment as loadComplete