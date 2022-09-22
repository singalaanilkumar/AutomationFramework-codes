Meta:
@issue OrderSelectionBatchInquiryAndBatchDetailInquiryWholeFlow Story
@tags Project: WMS_LITE, Product:Login, Program: Login
@automatedBy BH16726_Jannatul

Narrative:
Order Selection and Printing story for wms lite application

Lifecycle:

After:
Outcome: FAILURE
Then take screenshot for WaveSort2Store


Scenario: Positive scenario - cancelling the order after waving the order ->
Meta:
@id Login Scenario
@tag Type:endToend , Module:MainPage
@automatedBy BH16726_Jannatul


Given user logs in WMS LITE application
When user click menu item in WMS LITE application
Then Menu Item's popup will display
Then user click on wmslite Order Selection & Printing submenu of main menu Planner
Then user click Search/Selection Criteria in Order selection page
Then user enters value into inputLabel input from examples
Then user will validate wave status code as 0 in joppaDB before preview wave
Then user will validate data in osmDB
Then user clicks the SEARCH button
Then user click Preview Batch button in Order Selection Page
Then user click Yes button in preview batch popup
Then user gives 2 in max reservation per batch for Preview
Then user gives 5 in max reservation per print for Preview
Then user click Preview button in OrderReservation popup
Then user will validate wave status code as 5 in joppaDB after preview wave
Then user will validate data in osmDB
Then user selects row 1 from the BatchInquiry table
Then user click View Details button in BatchInquiry
Then user will see Reservation# numbers related to batch number
Then user click Back to Batch Inquiry button
Then user enters batchvalue in Batch Number input in Batch Inquiry page
Then user click Search Button
Then user selects row 1 from the BatchInquiry table
Then user click Submit Batch button in BatchInquiry
Then user click Yes button in preview batch popup in BatchInquiry
Then user select Test option from Printer list in BatchInquiry
Then user click Print button in select printer popup
Then user get Success Batch Number Msg
Then user will validate wave status code as 10 in joppaDB after Submit wave
Then user will validate data in osmDB
When user click menu item in WMS LITE application
Then user click on wmslite Batch Detail Inquiry submenu of main menu Planner
Then user enters ReservationNumber in Reservation# input at Batch Detail Enquiry
Then user click Search Button
Then user selects row 1 from the BatchDetailInquiry table
Then user click Undo Reservation button in BatchDetailInquiry
Then user click Yes button in UndoOrders batch popup in BatchDetailInquiry
Then user will validate wave status code as 0 in joppaDB after Undo wave
Then user will validate data in osmDB




Examples:
|inputLabel|value         |
|Reservation#|110019476 |



















