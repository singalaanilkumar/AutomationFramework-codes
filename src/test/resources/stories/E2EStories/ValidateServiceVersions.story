Meta:
@issue Service Version Validations
@tags Project: Service Version Validations, Product:DC20,Program: Off-Price
@automatedBy Rajanesh Kuruppath

Narrative:
In order to make sure all services are up and running with right versions
As a DC20 Team Member
I want to Perform version check the DC20 Services

Scenario: All services version validation
Meta:
@acceptance
@id versionValidation
@tag Type:acceptance, Type:smoke, Module: DC20, ProductName: DC20-AllServices
@automatedBy Rajanesh Kuruppath

Given below details validate all the services version

Examples:
| uri                                            | requiredVersion |
| accesscontrol-service/actuator/info            | PROD_VERSION    |
| appointment-service/actuator/info              | PROD_VERSION    |
| cfnetworkmap-service/actuator/info             | PROD_VERSION    |
| configuration-service/actuator/info            | PROD_VERSION    |
| distroengine-service/actuator/info             | PROD_VERSION    |
| handheld-service/actuator/info                 | PROD_VERSION    |
| hh-ddops-service/actuator/info                 | PROD_VERSION    |
| hh-receiving-service/actuator/info             | PROD_VERSION    |
| inventory-service/actuator/info                | PROD_VERSION    |
| inventory-messaging-service/actuator/info      | PROD_VERSION    |
| location-service/actuator/info                 | PROD_VERSION    |
| manifest-service/actuator/info                 | PROD_VERSION    |
| messaging-service/actuator/info                | PROD_VERSION    |
| msp-whm-orderfulfillment-service/actuator/info | PROD_VERSION    |
| msp-whm-ordermanagement-service/actuator/info  | PROD_VERSION    |
| msp-whm-shipment-service/actuator/info         | PROD_VERSION    |
| msp-whm-support-service/actuator/info          | PROD_VERSION    |
| package-service/actuator/info                  | PROD_VERSION    |
| pofourwalls-service/actuator/info              | PROD_VERSION    |
| print-adaptor-service/actuator/info            | PROD_VERSION    |
| pyramid-communication-service/actuator/info    | 1.19.0          |
| receiving-service/actuator/info                | PROD_VERSION    |
| reporting-service/actuator/info                | PROD_VERSION    |
| shipping-service/actuator/info                 | PROD_VERSION    |
| shipping-closeout-service/actuator/info        | PROD_VERSION    |
| sortation-service/actuator/info                | PROD_VERSION    |
| sorting-service/actuator/info                  | PROD_VERSION    |
| supplychain-composite-service/actuator/info    | PROD_VERSION    |
| waving-service/actuator/info                   | PROD_VERSION    |
| wsm-service/actuator/info                      | PROD_VERSION    |
| ApptIntegration                                | PROD_VERSION    |
| BuyIntegration                                 | PROD_VERSION    |
| LocnIntegration                                | PROD_VERSION    |
| RcvIntegration                                 | PROD_VERSION    |
| CebIntegration                                 | 1.0.58          |
| SortationIntegration                           | PROD_VERSION    |
| AuthSecurity                                   | 1.143.0         |