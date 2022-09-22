Meta:
@issue OrderInquiry Story
@tags Project: WMS_LITE, Product:Login, Program: Login
@automatedBy BH16726_Jannatul

Narrative:
Order Selection and Printing story for wms lite application

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for WaveSort2Store


Scenario: Positive scenario - Order Inquiry ->
Meta:
@id Login Scenario
@tag Type:endToend , Module:MainPage
@automatedBy BH16726_Jannatul


Given user logs in WMS LITE application
When user click menu item in WMS LITE application
Then Menu Item's popup will display
Then user click on wmslite Order Inquiry submenu of main menu Planner
Then user click Filter button in Order inquiry page
Then user enters value into Label input from examples
Then user click Search button in Order inquiry page
Then user will see the search data in Resv# at Table
Then user click Export button in Order inquiry page
Then user click Yes button in Order inquiry page


Examples:
|Label|value         |
|Res Nbr|110003211|




















