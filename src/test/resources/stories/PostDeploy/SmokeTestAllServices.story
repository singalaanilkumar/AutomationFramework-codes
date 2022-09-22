Meta:
@issue SmokeTest
@tags Project: Smoke Test Services, Product:DC20,Program: Off-Price
@automatedBy Rajanesh Kuruppath

Narrative:
In order to make sure all services are up and running
As a DC20 Team Member
I want to Perform Smoke test all the DC20 Services

Scenario: Smoke Test All services
Meta:
@acceptance
@id SmokeTest
@tag Type:acceptance, Type:smoke, Module: DC20, ProductName: DC20-AllServices
@automatedBy Rajanesh Kuruppath

Given Validation method for all services from DEV
