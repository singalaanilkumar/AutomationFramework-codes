Meta:
@issue DC2OP-1
@tags Project: E2E Integration testing, Product:OpenSortCount,Program: Off-Price
@automatedBy

Narrative:
In order to Validate Handheld UI after new Release
As a E2E Quality assurance person
I want to Perform Post Deployment Validations

Scenario: Post Depolyment Validations
Meta:
@acceptance
@id IOT-16-SC01
@tag Type:acceptance , Type:regression , Type:e2e , Module: Create Tote, productName RF transaction
@automatedBy BC90965

Given user logs into PROD HH UI
When user selects DC2.0 RF Options
Given Validate HH Menu Pages
|MenuOption 	   |Label		  |ScanText	 |PopupText	      |
|Pack Away Sorting|Scan Bin Box :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption 	   |Label		  	   |ScanText  |PopupText	   |
|Container Inquiry|Scan Container ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption  |Label		   |ScanText  |PopupText	  |
|Print Ticket|Scan Station ID:|1234567890|Invalid Station|
Then Validate HH Menu Pages
|MenuOption  	  |Label		      |ScanText  |PopupText	      |
|Adjust Container|Scan Container ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption|Label		     			|ScanText  |PopupText	  		|
|Runner	|Scan Drop Location/Pallet :|1234567890|No runner activity exist|
Then Validate HH Menu Pages
|MenuOption |Label		         |ScanText  |PopupText	     |
|Ticket/Prep|Scan Container ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption       |Label		       |ScanText  |PopupText	   |
|Consume Container|Scan Container ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption |Label		 |ScanText  |PopupText	     |
|SortToStore|Scan Zone :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption       |Label		       |ScanText  |PopupText	       |
|Create Pack|Scan In-House UPC :|1234567890|Invalid In-House UPC|
Then Validate HH Menu Pages
|MenuOption      |Label		      |ScanText  |PopupText	      |
|Locate Container|Scan Container ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption |Label		    |ScanText  |PopupText	    |
|Create Tote|Scan Tote ID :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption    |Label		   |ScanText  |PopupText	   |
|Putaway Pallet|Scan Pallet : |1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption |Label		            |ScanText  |PopupText	    |
|Split Move |Scan source container :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption |Label		            |ScanText  |PopupText	    |
|Magic Tote |Scan Barcode :|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption    |Label		            |ScanText  |PopupText	|
|Exception Lane|Scan Exception Lane ID:|1234567890|Not a  Valid Exception Lane|
Then Validate HH Menu Pages
|MenuOption    |Label		            |ScanText  |PopupText	|
|Release Lane  |Scan Lane ID:|1234567890|not a valid lane|
!-- Then Validate HH Menu Pages
!-- |MenuOption    |Label		 |ScanText  |PopupText	    |
!-- |Load Lane|Scan Container ID:|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Build Pallet|Scan Report ID:|1234567890|Invalid Report ID|
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Cycle Count   |Scan Location|1234567890|Invalid bar-code |
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Pack Away Pull|Scan Report ID:|1234567890|Invalid location|
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Move Pallet|Scan Report ID:|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Split-Adjust Pallet|Scan Report ID:|1234567890|Invalid bar-code|
Then Validate HH Menu Pages
|MenuOption    |Label		 |ScanText  |PopupText	      |
|Pick To Carton|Scan Location/Activity ID:|1234567890|Invalid location|
When user selects DC2.0 RF Options
Given Validate HH Menu Pages
|MenuOption 	   |Label		  |ScanText	 |PopupText	      |
|ICQA|Scan Bin Box :|1234567890|does not exist|
When user selects Outbound
Given Validate HH Menu Pages
|MenuOption 	   |Label		  |ScanText	 |PopupText	      |
|Untie|Scan Bin Box :|1234567890|Invalid Barcode|
Given Validate HH Menu Pages
|MenuOption 	   |Label		  |ScanText	 |PopupText	      |
|Dock Scan|Scan Bin Box :|1234567890|Invalid Barcode|


