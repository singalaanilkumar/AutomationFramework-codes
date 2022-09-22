package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLOrderFullFilment {
    public static final String RTFUpdateQuery = "UPDATE line_item SET status = 'ALC' "
			+ "WHERE  order_ship_nbr IN (SELECT order_shipment_nbr "
			+ "                          FROM   orderfulfillment.shipment "
			+ "                          WHERE  hold_date IN (#listOfHoldDates) "
			+ "                                 AND expected_ship_date IN (#listOfShipDates)) "
			+ "       AND dept_nbr IN (#deptNumbers) "
			+ "       AND status in ('#statusLists') ";;
   }
