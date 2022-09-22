Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Validate Production Endpoints after new Release
As a E2E Quality assurance person
I want to Perform Post Deployment Validations

Scenario: Post Depolyment Validations
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BC90965

Given Validate API Response Status Code and Details for Service
|serviceName                  |
|INVENTORY_PO_RCPT            |
|POLINE_BARCODE               |
|POLINE_DISTRO_INFO           |
|DEPT_PID_BY_SKU              |
|PO_DISTRIBUTION_INFO         |
|GET_WSM_ACTIVITY             |
|SINGLE_SKU_INFO              |
|REPORTING                    |
|INVENTORY_SNAPSHOT_DETAIL    |
|INVENTORY_RELATIONSHIP_DETAIL|
|CONTAINER_DETAILS            |
|PACKAGE                      |
Examples:
|PO_NBR |RCPT_NBR |REPORT_ID|SKUNUM      |BARCODE      |CONTAINER           |CARTON|
|4654265|197908339|40483    |492611310393|1061678262482|95120000000000264348|15000325000000221593|
