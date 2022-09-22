IIT-173 Search for MHE messages based on MessageType & specific dateTime range

Meta:
@issue IIT-173
@issue "messaging search API using MessageType and Date Range filters"
@tags Project: Infrastructure & Interfaces – Track1 , Product:Foundational, Program: Off-Price
@automatedBy BH04765_Rajanehs_Kuruppath

Narrative:
In order to view MHE messages
As a Backstage application user
I want to search messages with different type of filters

Scenario:1 publish a new message and search with given filters
Meta:
@id IIT-173_001
@tag Type:acceptance , Type:regression , Module:messaging
@automatedBy BH04765_Raj
Given Request JSON is created per MHE message type for MessagingForward
When POST service is called for the MHE message request JSON
Then Read auto generated Sequence No from header in response
Then search the message using filters and validate

Examples:
|ScnDesc                    |MessageType|LocnNbr|FilterMsgTyp|FromTimeStamp|ToTimeStamp|HowmanyMsg|TextFltr|JSONFltr|Status|TranName|SeqNbr|PageNbr|
|All filters Prefil         |storealloc |7221   |Y           |Y::PREFILL   |Y::PREFILL |10        |        |        |      |        |      |
|All filters user given time|heartbeat  |7221   |Y           |Y::PREFILL   |Y::PREFILL |10        |        |        |      |        |      |
|Only Size Filter           |controute  |7221   |            |Y::PREFILL   |Y::PREFILL |20        |        |        |      |        |      |
|Only From & To time filter |totecont   |7221   |            |Y::PREFILL   |Y::PREFILL |          |        |        |      |        |      |