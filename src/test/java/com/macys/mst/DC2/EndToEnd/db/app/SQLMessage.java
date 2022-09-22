package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLMessage {

    public static final String GET_MESSAGE_DETAILS_WITH_DATA_RANGE = "select MSG.created_time,MSG.sequence_number,MSG.transaction_name,MSG.destination_id," +
            "TRANS.STATUS,MSG.incoming_payload,MSG.outgoing_payload,MSG.client_id,MSG.retry_attempts from messaging.message MSG," +
            "  messaging.transmission_status TRANS Where MSG.destination_id=''{0}'' and MSG.outgoing_payload like ''%{1}%''" +
            " and MSG.created_time BETWEEN ''{2}'' and ''{3}'' order by MSG.updated_time desc";
    
    public static final String GET_MESSAGE_DETAILS_WITH_DATA_RANGE_MHE = "select MSG.created_time,MSG.sequence_number,MSG.transaction_name,MSG.destination_id," +
            "MSG.incoming_payload,MSG.outgoing_payload,MSG.client_id,MSG.route_id,MSG.retry_attempts,MSG.transmission_status from messaging.message MSG" +
            " Where MSG.destination_id=''{0}'' and MSG.outgoing_payload like ''%{1}%''" +
            " and MSG.created_time BETWEEN ''{2}'' and ''{3}'' order by MSG.updated_time desc";

    public static final String GET_MESSAGE_DETAILS = "select MSG.created_time,MSG.sequence_number,MSG.transaction_name,MSG.destination_id," +
            "TRANS.STATUS,MSG.incoming_payload,MSG.outgoing_payload,MSG.client_id,MSG.retry_attempts from messaging.message MSG,  " +
            "messaging.transmission_status TRANS Where MSG.destination_id=''{0}'' " +
            "and MSG.outgoing_payload like ''%{1}%'' order by MSG.updated_time desc";

    public static final String GET_INVENTORY_SNAPSHOT = "select ITEM,QUANTITY,CONTAINER from inventory.inventory_snapshot where CONTAINER='%s'";

    public static final String GET_MESSAGING_STOREALLOC = "select OUTGOING_PAYLOAD from messaging.message where destination_id = 'STOREALLOC' and outgoing_payload like '%{PONBR}%' and outgoing_payload like '%{RECEIPTNBR}%'and outgoing_payload like '%{UPC}%' and outgoing_payload like '%{STORE}%' order by id desc";


	public static final String UPDATE_SORTING_STORE_ALLOC_DATA = "update sorting.store_alloc set unit_put_qty = '0',allocated_qty='0' where order_number='{PONBR}' and po_receipt='{RECEIPTNBR}'" ;
    public static final String UPDDATE_SORTING_PACKAWAY_LOCATION_DATA = "UPDATE sorting.packaway_location_data SET LOCATION_STATUS='%s',ENABLED=%s WHERE LOCATION_STATUS='OPEN' AND ZONE = '%s' AND ENABLED IS NOT NULL";
	public static final String GET_SORTING_STORE_ALLOC_DATA = "SELECT * FROM sorting.store_alloc where process_area ='BLK' and created_by = 'StoreAlloc' and order_number='{PONBR}' and po_receipt='{RECEIPTNBR}' order by created_ts desc";
	public static final String GET_SORTING_STORE_ALLOC_DATA_WAVE = "SELECT skuupc,store_loc_nbr,distro_qty FROM sorting.store_alloc where process_area ='BLK' and created_by = 'StoreAlloc' and order_number='{WAVENUMBER}' order by created_ts desc";
	public static final String CLEANUP_SORTING_STORE_ALLOC_DATA = "delete from sorting.store_alloc where po like '{PONBR}' and po_receipt like '{RECEIPTNBR}'" ;
    public static final String UPDATE_MESSAGES_TO_FAILED = "update message set transmission_status_id = (select id from transmission_status where status='SENT_TO_DEST_FAILED') where id = '{SEQUENCEID}'";


}
