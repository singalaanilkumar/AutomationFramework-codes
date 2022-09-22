Meta:
@issue DCOA-578
@tags Project: OB Through Merge, Product:SupplychainUI, Program: Network Map
@automatedBy BH16766

Narrative:
As a E2E Quality assurance person
I want to validate All UI components for Add and View Route

Scenario:1 Add Direct Route
Meta:
@acceptance
@id DCOA-578
@tag Type:acceptance , Type:regression , Type:e2e , Module: Network Map
@automatedBy BH16766
Given Clear old data
|getRoute|deleteRoute|GETQueryParams|StartDate|EndDate|
|NetworkMap.getRoute|NetworkMap.deleteRoute|cfcLocNbr:4420,routeType:DIRECT,sdcLocNbr:6|2031-11-12|2031-12-12|
Given user logged in NetworkMap UI and selected Add Route of Network Map
When User select Direct tab and provide details for Add Route
|CFC|SDC|Date|
|GOODYEAR|HAYWARD|11/13/2031 - 12/12/2031|
When clicked on Add Route button
Then Route Added successfully message is displayed
Then Select logout

Scenario:2 Add Merge Route
Meta:
@acceptance
@id DCOA-578
@tag Type:acceptance , Type:regression , Type:e2e , Module: Network Map
@automatedBy BH16766
Given Clear old data
|getRoute|deleteRoute|GETQueryParams|StartDate|EndDate|
|NetworkMap.getRoute|NetworkMap.deleteRoute|cfcLocNbr:4420,mcLocNbr:6250,routeType:MERGE,sdcLocNbr:4|2031-11-12|2031-12-12|
Given user logged in NetworkMap UI and selected Add Route of Network Map
When User select Merge tab and provide details for Add Route
|CFC|MergeCenter|SDC|Date|
|GOODYEAR|HIGH POINT, NC|TUKWILA|11/13/2031 - 12/12/2031|
When clicked on Add Route button
Then Route Added successfully message is displayed
Then Select logout

Scenario:3 Adding a Route for already existing route
Meta:
@acceptance
@id DCOA-578
@tag Type:acceptance , Type:regression , Type:e2e , Module: Network Map
@automatedBy BH16766
Given User logs in SCM application
When user click on supplychain main menu NetworkMap
Then A new window opens for NetworkMapUI and user switches to new window for validations
Given user switched to new NetworkMap UI window and selected Add Route of Network Map
When User select Direct tab and provide details for Add Route
|CFC|SDC|Date|
|GOODYEAR|HAYWARD|11/13/2031 - 12/12/2031|
When clicked on Add Route button
Then Cannot create new Route message is displayed
Then Select logout


Scenario:4 Searching a particular root using CFC criteria
Meta:
@acceptance
@id DCOA-578
@tag Type:acceptance , Type:regression , Type:e2e , Module: Network Map
@automatedBy BH16766
Given user logged in NetworkMap UI and selected View Route of Network Map
When User provide the CFC details
|CFC|
|GOODYEAR, AZ|
When clicked on Search button
Then All the route for the given CFC should be displayed in the grid and validate it with DB
Then Select logout

Scenario:5 Perform copy,edit and delete on the searched route
Meta:
@acceptance
@id DCOA-578
@tag Type:acceptance , Type:regression , Type:e2e , Module: Network Map
@automatedBy BH16766
Given Clear old data
|getRoute|deleteRoute|GETQueryParams|StartDate|EndDate|
|NetworkMap.getRoute|NetworkMap.deleteRoute|cfcLocNbr:4420,routeType:DIRECT,sdcLocNbr:3|2031-11-12|2031-12-12|
|NetworkMap.getRoute|NetworkMap.deleteRoute|cfcLocNbr:4420,routeType:DIRECT,sdcLocNbr:7|2031-11-12|2031-12-12|
Given user logged in NetworkMap UI and selected View Route of Network Map
When User provide the CFC and Date details
|CFC|Date|
|GOODYEAR|11/13/2031 - 12/12/2031|
When clicked on Search button
When click on the copy action for the searched route
When click on the cancel button
Then the copy pop up is closed
When click on the copy action for the searched route
When User provide the SDC on the Copy route
|SDC|
|STONE MOUNTAIN|
When click on the Add route button on the Copy route pop up
Then Route Added successfully message is displayed on pop up
When click on the cancel button
When click on the edit button for a route
When edit the SDC option
|SDC|
|SECAUCUS|
When Click on the Save route button
Then Route Added successfully message is displayed on pop up
When click on the cancel button
When click on the delete button for a route
When click on the Delete route button on the pop up
Then Route deleted successfully message is displayed