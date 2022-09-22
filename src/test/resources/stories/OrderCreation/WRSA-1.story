Meta:
@issue WRSA-1

Scenario: Validate Beumer - PD SOT wave flow
Meta:
@acceptance
@id WRSA-1-SC01
@productName WMSE2E
@moduleName WMSE2E
@automatedBy BH00016

Given Get location details for prodline CSR with aisles as 1 with 1
Given reset the data for wave function for ITEMS N,Orders Y,Waves Y
Given data is created for wave by posting orders using below parameter
| OrderNo | Prodlines | OrderQty | AisleNumber | OnHandQty | OrderCatogery | pickupStartDays |deliveryDateTime| ItemNumber | LPN_BRK_ATTRIB | LPN_TYPE | unitvolume | unitweight | ItemsLBH | criticalLBH | AllocatedQty |order-Category|noteCode|ROUTETO|
| 1       | CSR       | 5        | AISLE1:1    | 25        | NST           | -1              |FUTURE          | 1          | B1             | BAG      | 10         | 0.1        | 1/5/2    | 1/5/2       | 0            |      SS      |   SS   |3      |
Given Generic configuration set for Beumer in New Wave Type Configuration with below parameters
| MAX_ORDERS | MAX_UNITS |
| 3          | 15        |

Given Generic configuration set for Beumer in New Storage Type Mix Configuration with below parameters
| BTR | BTP | CSR | GOH | HFG | PLR | SEC | SHO |
| 0   | 0   | 0   | 0   | 0   | 100 | 0   | 0   |

When wave planner selects Beumer wave with multi singles and SOT checkbox is unchecked and RUN wave with Pickdensity as checked and date from -1 to -1
Then verify that the order ORD1,ORD2,ORD3 is selected for RUN wave for batch number 1
Given release wave for logical group
|releaseParam             |
|logicalgrp:BEU-ALL       |


Then Login to handheld and select Pick By Work ID menu
Given User complete picking for Pick By Work ID transaction
|Scan Work|Scan Tote       |Scan Item            |Scan Qty|
|    1    |as unique number|enters item displayed|5       | 
|  Same   |      NA        |enters item displayed|5       |   
|  Same   |      NA        |enters item displayed|5       |

Given Instantiate beumer packing
|sort type|package index|sorted quantity|status|
|     S   |      1      |      5        |  12  |
|     S   |      2      |      5        |  12  |
|     S   |      3      |      5        |  12  |

When User uses Chute To Tote for patrolling Package 1 and press No button :
|packageStatus|screenName1|screenName2|
|     12      |Scan Chute |Scan Tote  |

When User uses Pack and Print for packing Package 1 :
|printer|tote_package|screenName|
|123-123|Printer     |Scan Tote |


When User uses Chute To Tote for patrolling Package 2 and press No button :
|packageStatus|screenName1|screenName2|
|     12      |Scan Chute |Scan Tote  |

When User uses Pack and Print for packing Package 2 :
|printer|tote_package|screenName|
|123-123|Printer     |Scan Tote|


When User uses Chute To Tote for patrolling Package 3 and press No button :
|packageStatus|screenName1|screenName2|
|     12      |Scan Chute |Scan Tote  |

When User uses Pack and Print for packing Package 3 :
|printer|tote_package|screenName|
|123-123|Printer     |Scan Tote |

Then Sending package for manifesting 
|menu               |index|action          |
|RF Manifest Package|0    |MANIFEST PACKAGE|
|RF Manifest Package|1    |MANIFEST PACKAGE|
|RF Manifest Package|2    |MANIFEST PACKAGE|

!-- OSM Validations
Then Validate SHP_HDR table with below parameters:
|osm_stat_cd|
|190|	

Then Validate SHP_LI table with below parameters:
|osm_dtl_stat_cd|packed_qty|
|190|1|

Then Validate SHP_PCKG and SHP_PCKG_LI tables

Given Reset the data for wave function for ITEMS Y,Orders Y,Waves Y
