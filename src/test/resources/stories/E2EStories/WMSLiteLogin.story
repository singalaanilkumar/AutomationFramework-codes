Meta:
@issue Login Story
@tags Project: WMS_LITE, Product:Login, Program: Login
@automatedBy BH16641_SmitaLunkad

Narrative:
Login story for wms lite application

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for WaveSort2Store

Scenario:Positive scenario - Login Story ->
Meta:
@id Login Scenario
@tag Type:endToend , Module:MainPage
@automatedBy BH16641_SmitaLunkad

Given user logs in WMS LITE application
Then user validates data from OSM DB using fullfillmentnbr
Then user validates data from Joppa DB using fullfillmentnbr



