package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLPackage {
  
	public static final String PackagesForPO_SQL = "SELECT "
    		+ "    DISTINCT(pkg.id) as packageID "
    		+ "FROM "
    		+ "    package.package pkg "
    		+ "    inner join "
    		+ "    package.package_dtl pkgdtl on pkgdtl.package_id = pkg.id "
    		+ "WHERE "
    		+ "   pkg.status in ('IPK' , 'PCK', 'MFT', 'WGH', 'SHP') "
    		+ "   and pkg.enabled = 1 "
    		+ "   and pkgdtl.order_number = '%s' "
    		+ "   and pkgdtl.order_number_ex = '%s'";
  
	}
