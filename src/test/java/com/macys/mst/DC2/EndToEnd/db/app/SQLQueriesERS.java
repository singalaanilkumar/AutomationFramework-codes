package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLQueriesERS {
    public static final String SELECT_RCPT_LI_BY_RCPT = "select RCPT_LI_STAT_NBR as RCPT_STATUS,SKU_UPC_NBR,INV_LOC_NBR,sum(XPCTD_QTY) AS SUM_EXP_QTY, sum(ACTL_QTY) AS SUM_ACTL_QTY  from RCPT_LI where RCPT_NBR=%s group by RCPT_LI_STAT_NBR,SKU_UPC_NBR,INV_LOC_NBR";

    public static final String SELECT_FLO_CNTR_BY_CNTR = "select CNTR_STAT_NBR,ORGN_LOC_NBR, DEST_LOC_NBR,XPCTD_UNIT_QTY,ACTL_UNIT_QTY from FLO_CNTR where CNTR_20DIGIT_NBR='%s'";

}
