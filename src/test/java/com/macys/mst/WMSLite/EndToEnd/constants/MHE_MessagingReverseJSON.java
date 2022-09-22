package com.macys.mst.WMSLite.EndToEnd.constants;

public class MHE_MessagingReverseJSON {
    String heartbeat = "<STX>#sequenceno#|HEARTBEAT<ETX>";
    String heartbeat_request = "{\n" +
            "  \"tname\": \"#tname#\",\n" +
            "  \"payload\": \"<STX>#sequenceno#|HEARTBEAT<ETX>\",\n" +
            "  \"clientId\": \"testing123\",\n" +
            "  \"locnNbr\":3,\n" +
            "  \"messageType\": \"HEARTBEAT\"\n" +
            "}\n";
    public static final String SCAN_WEIGH_MESSAGE = "\u0002#sequenceno#|SCANWEIGH|#barCode#|#weight#\u0003";
    public static final String SHIP_CONFIRM_MESSAGE = "\u0002#sequenceno#|SHIPCONFIRM|#barCode#|#shipLane#\u0003";
    public static final String TOTE_CLOSE_MESSAGE = "\u0002#sequenceno#|TOTECLOSE|#toteNumber#||#quantity#\u0003";
    public static final String CONT_CLOSE_MESSAGE = "\u0002#sequenceno#|CONTCLOSED|#cartonNumber#|#storeLocNbr#\u0003";
    public static final String CONT_DIVERT = "\u0002#sequenceno#|CONTDIVERT|#lpnNumber#|||#divertedLane#\u0003";
    public static final String CONT_RECEIVE = "\u0002#sequenceno#|CONTRECEIVE|#conatinerBarcode#|#ASN#||N|#PO#|#Rcpt#|#divertLane#||\u0003";
    public static final String UNITPUT = "\u0002#sequenceno#|UNITPUT|#conatinerBarcode#|#cartonBarcode#|#SKU#|1|||||||||||||||||||||||||||||#store#|#dept#|#qty#|0|N000|W|#waveNbr#||||\u0003";
    public static final String MANIFEST_PAYLOAD = "{\n" +
            "    \"cartonNumber\": \"#carton#\",\n" +
            "    \"orderNumber\": \"1456\",\n" +
            "    \"cartonStatus\": \"Weighed\",\n" +
            "    \"shipToDetails\": {\n" +
            "        \"name\": \"QUEENS PLACE\",\n" +
            "        \"address1\": \"88-01 QUEENS BOULLEVARD\",\n" +
            "        \"address2\": \" \",\n" +
            "        \"city\": \"ELMHURST\",\n" +
            "        \"state\": \"NY\",\n" +
            "        \"zipCode\": \"11373\",\n" +
            "        \"country\": \"US\"\n" +
            "    },\n" +
            "    \"actualWeight\": 121.34,\n" +
            "    \"boxBag\": \"Box\",\n" +
            "    \"dimension\": {\n" +
            "        \"length\": 0,\n" +
            "        \"width\": 0,\n" +
            "        \"height\": 0\n" +
            "    },\n" +
            "    \"shipVia\": {\n" +
            "        \"shipVia\": \"U3\",\n" +
            "        \"allowHAZ\": false,\n" +
            "        \"allowRoute\": false\n" +
            "    },\n" +
            "    \"baseAmount\": 50,\n" +
            "    \"totalShipmentAmount\": 89,\n" +
            "    \"msnId\": \"\",\n" +
            "    \"hazmatIndicator\": \"N\",\n" +
            "    \"destinationStoreNumber\": 6326,\n" +
            "    \"routeAsNewStore\": true,\n" +
            "    \"shipViaConfig\": {\n" +
            "        \"values\": [\n" +
            "            {\n" +
            "                \"shipVia\": \"U3\",\n" +
            "                \"serviceLevel\": \"UPS3DAY\",\n" +
            "                \"hazmat\": \"N\",\n" +
            "                \"allowHAZ\": false,\n" +
            "                \"allowRoute\": true\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"bagBoxConfig\": {\n" +
            "        \"values\": [\n" +
            "            \"Box\"\n" +
            "        ]\n" +
            "    },\n" +
            "    \"departments\": \"853,854,855\",\n" +
            "    \"statusMap\": {\n" +
            "        \"IPK\": \"Packing In Progress\",\n" +
            "        \"CRE\": \"Created\",\n" +
            "        \"WGH\": \"Weighed\",\n" +
            "        \"PCK\": \"Packed\",\n" +
            "        \"SHP\": \"Shipped\",\n" +
            "        \"MFT\": \"Manifested\",\n" +
            "        \"MFS\": \"Manifested\"\n" +
            "    },\n" +
            "    \"oldShipVia\": {\n" +
            "        \"shipVia\": \"U3\"\n" +
            "    },\n" +
            "    \"printerIP\": \"\\\\\\\\ma001xswms95\\\\ZM2-T01\"\n" +
            "}";
}