package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLConfiguration {
    public static final String ConfigurationValue = "select config_value from configuration.configuration where locn_nbr = '%s' and app_name = '%s' and module_name = '%s' and config_key = '%s' and enabled = '%s'";
}
