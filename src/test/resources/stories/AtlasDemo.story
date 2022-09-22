Meta:
@issue AtlasAutomationDemo Story
@tags Project: Atlas, Product:PO, Program: Demo
@automatedBy BH16641_SmitaLunkad

Narrative:
Demo Story for Atlas Automation

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for WaveSort2Store

Scenario:Positive scenario - Demo Story ->
Meta:
@id Demo Scenario
@tag Type:endToend , Module:Handheld
@automatedBy BH16641_SmitaLunkad

Given Delete records in db for package_number 10000000000043241994
Given Get TestData from DB
Given Update dates for test data in DB for given package no
Given User logs in Handheld
When User navigates to outbound->packing->pack and print option and scans package_no
Then User validates status of package updated to 20 in DB
Then User validates ship_via of package in DB
Given User logs in Apollo
Then User navigates to mainmenu Talos and submenu Package Detail Inquiry
Then User searches for given package no in Package Detail Inquiry
Then User generates invoice
Then User generates shipment label
Then User Logs out from Apollo


Scenario: RF Manifest ->
Meta:
@id Demo Scenario
@tag Type:endToend , Module:Handheld
@automatedBy BH16641_SmitaLunkad

Given User logs in Handheld
When User navigates to outbound->exceptions->rf manifest package option and scans package_no
Then User validates manifest request submitted message
Then User validates status of package updated to 40 in DB
Then User navigates to Talos->Package Detail Inquiry
Then User searches for given package no in Package Detail Inquiry
Then User generates invoice
Then User generates shipment label

Scenario: Divertship using API->
Meta:
@id Demo Scenario
@tag Type:endToend , Module:Handheld
@automatedBy BH16641_SmitaLunkad

Given User performs Divertship using API call
|requestUrl |queryParams                                           |
|manifesturl|transactionName:Manifest|

Scenario: Manual Manifestation ->
Meta:
@id Demo Scenario
@tag Type:endToend , Module:Handheld
@automatedBy BH16641_SmitaLunkad

Given User logs in Apollo
Then User navigates to mainmenu Atlas and submenu Manifest Package
Then User enters given package no in Manifest Package
Then User enters estimated weight from DB in weight field
Then User user clicks on Manifest button
Then User validates manifest message on page
Then User validates status of package updated to 90 in DB
