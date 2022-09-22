package com.macys.mst.Atlas.db.app;

public class SQLQueries {
    public static final String GET_M_PACKAGE_TESTDATA = "select * from m_package\n" +
            "    where status = '12'\n" +
            "    order by date_created desc\n" +
            "    fetch first 1 rows only";


}
