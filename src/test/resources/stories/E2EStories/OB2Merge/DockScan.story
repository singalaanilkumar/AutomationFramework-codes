Meta:
@issue DCOA-447
@tags Project: E2E Integration testing, Product:OB2Merge,Program: Off-Price
@automatedBy

Narrative:
In order to Process Outbound to Merge - LastScan, Tie, Untie option
As a E2E Quality assurance person
I want to process LastScan, Tie, Untie to update appointment in carton through Dc four walls

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for OutboundToMerge

Scenario: LastScan is performed
Meta:
@acceptance
@id DCOA-447-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115
Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then divert the package through Pyramid to BA01A025
!-- When SHIPCONFIRM message is sent by Pyramid after store package is shipped with carrier Lane BA01A025
!--  Given Update Package
!--  |requestUrl                       |updateParam                          |
!--  |packageService.updatePackageMulti|{cartonStatus:SHR,divertshipSeq:true}|
Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When Perform Last scan on carton 3, on that door S122
Then Validate door S122 with package status SHR on dock option lastScan
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When Perform Last scan on carton 6, on that door S122
Then Validate door S122 with package status SHR on dock option lastScan
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When scan carton number with Invalid option on that door S122
Then Validate Inline Message Carton is already Tied to this appointment. Use a different Carton for Last Scan. and Message type Success
When User selects exit
And User selects logoff


Scenario: Tie and ReTie is performed
Meta:
@acceptance
@id DCOA-447-SC02
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then divert the package through Pyramid to BA01A025
!-- When SHIPCONFIRM message is sent by Pyramid after store package is shipped with carrier Lane BA01A025
!-- Given Update Package
!-- |requestUrl                       |updateParam       |
!-- |packageService.updatePackageMulti|{cartonStatus:SHR}|
Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with tie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
!-- validate load Inquire details
When User selects exit
And user click on sub menu LoadInquiry
When scan door S122 number
Then Validate Load Inquiry details for door
When User selects exit
And user click on sub menu LoadInquiry
Then Validate Load Inquiry details for carton
!-- Re-Tie option
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with retie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
When User selects back
Then Validate Dock Options Screen
When User selects exit
And User selects logoff

Scenario: Validation DoorNumber, Cartonnumber
Meta:
@acceptance
@id DCOA-447-SC03
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then divert the package through Pyramid to BA01A025
!-- When SHIPCONFIRM message is sent by Pyramid after store package is shipped with carrier Lane BA01A025
!-- Given Update Package
!-- |requestUrl                       |updateParam       |
!-- |packageService.updatePackageMulti|{cartonStatus:SHR}|
Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S1234 number
Then Validate Inline Message Invalid Barcode and Message type Error
When scan door S123456 number
Then Validate Inline Message Invalid Barcode and Message type Error
When scan door S000 number
Then Validate Inline Message Dock Door not configured correctly. Contact Supervisor. and Message type Error
When scan door S1234567 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 15000381418743416285 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 50000000000000000123 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 95000000000000000001 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan 50000000000000000123 carton number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan 95000000000000000001 carton number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan 1234 carton number
Then Validate Inline Message Invalid Barcode and Message type Error
Given Update Package
|requestUrl                       |updateParam       |
|packageService.updatePackageMulti|{cartonStatus:IPK}|

When scan carton number with Invalid option on that door S122
Then Validate Inline Message Invalid Carton Status. Contact Supervisor. and Message type Error
Given Update Package
|requestUrl                       |updateParam       |
|packageService.updatePackageMulti|{cartonStatus:SHR}|
When scan carton number with tie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
When User selects exit
And User selects logoff

Scenario: Tie - ShipVia mismatch is performed
Meta:
@acceptance
@id DCOA-447-SC04
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Get Door Details S123
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S122      |
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S123      |
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then divert the package through Pyramid to BA01A025
!-- When SHIPCONFIRM message is sent by Pyramid after store package is shipped with carrier Lane BA01A025
!-- Given Update Package
!-- |requestUrl                       |updateParam       |
!-- |packageService.updatePackageMulti|{cartonStatus:SHR}|
Then Validate door S122 with package status SHR on dock option no
Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with Invalid option on that door S123
Then Validate Inline Message Carton not associated to this Lane. Cannot perform Last Carton Scan. Move to correct Door S123 and Message type Error
Then Validate door S122 with package status SHR on dock option emptyApptStatus
When scan carton number with tie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with tie option on that door S123
Then Validate door S123 with package status SHR on dock option tie
When User selects exit
And User selects logoff

Scenario: Tie -Override appointment is performed
Meta:
@acceptance
@id DCOA-447-SC05
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S123
Given Get Door Details S424
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S123      |
Then CONTCLOSED message is sent after the carton is closed
When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
Then divert the package through Pyramid to BA01A025
!-- When Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service
!-- Then SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed
!-- When SHIPCONFIRM message is sent by Pyramid after store package is shipped with carrier Lane BA01A025
!-- Given Update Package
!-- |requestUrl                       |updateParam       |
!-- |packageService.updatePackageMulti|{cartonStatus:SHR}|
Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with tie option on that door S123
Then Validate door S123 with package status SHR on dock option tie
!-- Re-Tie with different appointment option
Given Update Package
|requestUrl                       |updateParam                    |
|packageService.updatePackageMulti|{cartonStatus:SHR,apptNbr:S424}|
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with Invalid option on that door S123
Then Validate override appointment Message Carton already Tied to a different Appointment #appt. Do you want to override? and click No for door S424
When scan carton number with Invalid option on that door S123
Then Validate override appointment Message Carton already Tied to a different Appointment #appt. Do you want to override? and click Yes for door S424
And Validate door S123 with package status SHR on dock option tie
And Validate Inline Message 1 Carton Tied successfully to the Appointment and Message type Success
When User selects exit
And User selects logoff

Scenario: Untie and Re-Untie is performed
Meta:
@acceptance
@id DCOA-447-SC06
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S123
Given Create Package
|requestUrl                  |templateName      |requestParams                                      |doorNumber|
|packageService.createPackage|CreatePackage.json|{#barcode:150003-D-14,#status:IPK,#processArea:OSC}|S123      |
Then CONTCLOSED message is sent after the carton is closed
Given Update Package
|requestUrl                       |updateParam       |
|packageService.updatePackageMulti|{cartonStatus:SHR}|
Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
When scan carton number with tie option on that door S123
Then Validate door S123 with package status SHR on dock option tie
When User selects back
Then Validate Dock Options Screen
When User selects exit
When User selects untie
When scan carton number with untie option on that door S123
Then Validate door S123 with package status SHR on dock option no
When scan carton number with reUntie option on that door S123
When User selects exit
And User selects logoff

Scenario: WMS Legacy LastScan is performed
Meta:
@acceptance
@id DCOA-447-SC08
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:2,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:3,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:4,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:5,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:6,#locnNbr:7221}|S122      |
Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When Perform Last scan on carton 3, on that door S122
Then Validate door S122 with package status SHR on dock option lastScan
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When Perform Last scan on carton 6, on that door S122
Then Validate door S122 with package status SHR on dock option lastScan
When scan door S122 number
Then Validate Dock Options Screen
When User selects lastscan
Then Validate door S122 with lastscan Dock option
When scan carton number with Invalid option on that door S122
Then Validate Inline Message Carton is already Tied to this appointment. Use a different Carton for Last Scan. and Message type Success
When User selects exit
And User selects logoff

Scenario: WMS Legacy Tie and ReTie is performed
Meta:
@acceptance
@id DCOA-447-SC09
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S122      |

Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with tie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
!-- Re-Tie option
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with retie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
When User selects back
Then Validate Dock Options Screen
When User selects exit
And User selects logoff


Scenario: WMS Legacy Validation DoorNumber, Cartonnumber
Meta:
@acceptance
@id DCOA-447-SC10
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S122      |

Then Validate door S122 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S1234 number
Then Validate Inline Message Invalid Barcode and Message type Error
When scan door S123456 number
Then Validate Inline Message Invalid Barcode and Message type Error
When scan door S000 number
Then Validate Inline Message Dock Door not configured correctly. Contact Supervisor. and Message type Error
When scan door S1234567 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 15000381418743416285 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 50000000000000000123 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door 95000000000000000001 number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan 50000000000000000123 carton number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan 95000000000000000001 carton number
Then Validate Inline Message Invalid Container Type and Message type Error
When scan 1234 carton number
Then Validate Inline Message Invalid Barcode and Message type Error
Given Update Package
|requestUrl                       |updateParam       |
|packageService.updatePackageMulti|{cartonStatus:IPK}|

When scan carton number with Invalid option on that door S122
Then Validate Inline Message Invalid Carton Status. Contact Supervisor. and Message type Error
Given Update Package
|requestUrl                       |updateParam       |
|packageService.updatePackageMulti|{cartonStatus:SHR}|
When scan carton number with tie option on that door S122
Then Validate door S122 with package status SHR on dock option tie
When User selects exit
And User selects logoff

Scenario: WMS Legacy Tie - ShipVia mismatch is performed
Meta:
@acceptance
@id DCOA-447-SC11
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S122
Given Get Door Details S123
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S122      |
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S123      |

Then Validate door S122 with package status SHR on dock option no
Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S122 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S122 with tie Dock option
When scan carton number with Invalid option on that door S123

Then Validate Inline Message Carton not associated to this Lane. Cannot perform Last Carton Scan. Move to correct Door S123 and Message type Error
Then Validate door S122 with package status SHR on dock option emptyApptStatus
When scan carton number with tie option on that door S122

Then Validate door S122 with package status SHR on dock option tie
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with tie option on that door S123

Then Validate door S123 with package status SHR on dock option tie
When User selects exit
And User selects logoff

Scenario: WMS Legacy Tie -Override appointment is performed
Meta:
@acceptance
@id DCOA-447-SC12
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S123
Given Get Door Details S424
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S123      |

Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with tie option on that door S123
Then Validate door S123 with package status SHR on dock option tie
!-- Re-Tie with different appointment option
Given Update Package
|requestUrl                       |updateParam                    |
|packageService.updatePackageMulti|{cartonStatus:SHR,apptNbr:S424}|
When User selects exit
And user click on sub menu DockScan
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
Then Validate door S123 with tie Dock option
When scan carton number with Invalid option on that door S123
Then Validate override appointment Message Carton already Tied to a different Appointment #appt. Do you want to override? and click No for door S424
When scan carton number with Invalid option on that door S123
Then Validate override appointment Message Carton already Tied to a different Appointment #appt. Do you want to override? and click Yes for door S424
And Validate door S123 with package status SHR on dock option tie
And Validate Inline Message 1 Carton Tied successfully to the Appointment and Message type Success
When User selects exit
And User selects logoff

Scenario: WMS Legacy Untie and Re-Untie is performed
Meta:
@acceptance
@id DCOA-447-SC13
@tag Type:acceptance , Type:regression , Type:e2e , Module: Dock Scan, productName RF transaction
@automatedBy B006115

Given Reset scenario
Then Clean the cache
Given Get configuration
|appName |moduleName           |configKey            |
|outbound|DockScanConfiguration|dockScanConfiguration|
Given Get Door Details S123
Given Publish Carton Event
|requestUrl                    |topicName              |templateName         |requestParams                                                          |doorNumber|
|TestingServices.publishMessage|pubSub.topics.wmsCarton|WMSCreatePackage.json|{#barcode:150003-D-14,#divertshipTS:CAL,#divertshipSeq:1,#locnNbr:7221}|S123      |

Then Validate door S123 with package status SHR on dock option no
Given user loged in and selected DockScan of Outbound
Then Validate Dock Scan Screen
When scan door S123 number
Then Validate Dock Options Screen
When User selects tie
When scan carton number with tie option on that door S123
Then Validate door S123 with package status SHR on dock option tie
When User selects back
Then Validate Dock Options Screen
When User selects exit
When User selects untie
When scan carton number with untie option on that door S123
Then Validate door S123 with package status SHR on dock option no
When scan carton number with reUntie option on that door S123
When User selects exit
And User selects logoff