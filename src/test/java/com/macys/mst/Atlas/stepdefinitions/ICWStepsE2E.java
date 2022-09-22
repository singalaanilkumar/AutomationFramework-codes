package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.selenium.LocalDriverManager;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.atlas.stepdefinitions.OsmXMLPosting;
import com.macys.mst.wavefunction.db.app.DBMethods;
import com.macys.mst.wavefunction.sqlconstants.Constants;
import com.macys.wms.util.TalosUtils;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ICWStepsE2E {
//	private StepsContext stepsContext;
//	public ICWStepsE2E(StepsContext stepsContext) {
//		this.stepsContext = stepsContext;
//	}

	private Logger log = Logger.getLogger(ICWStepsE2E.class.getName());
	private boolean postOsmXml = false;
	private boolean postwmsXml = false;

	public static String getOSMdbSchema() {
		return OSMdbSchema;
	}

	public static void setOSMdbSchema(String OSMdbSchema) {
		ICWStepsE2E.OSMdbSchema = OSMdbSchema;
	}

	private static String OSMdbSchema = FileConfig.getInstance().getStringConfigValue("was.env.osmBOSSSchema");
	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	public long TestNGThreadID = Thread.currentThread().getId();

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String, String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId()), obj);
		setOSMdbSchema(OSMdbSchema);
	}

	@Then("Validate SHP_HDR table with below parameters:$parameters")
	public void validateshphdrvalues(ExamplesTable paramtable) {
		setOSMIntegrationFlag();
		try {
			TimeUnit.SECONDS.sleep(15);
			if (postOsmXml) {
				List<String> ordernumbers = getOrderNumberFromMPackage();
				//List<String> ordernumbers = DataGeneration.ordernumber;
				for (String ordernumber : ordernumbers) {
					OsmXMLPosting.finalFulfillmentNumbr = ordernumber;

					List<Map<String, String>> shpHdrvalues = DBMethods
							.getDBValuesAsListOfMaps("select * from SHP_HDR where FULLFILLMENT_NBR ='"
									+ OsmXMLPosting.finalFulfillmentNumbr + "'", OSMdbSchema);
					for (Map<String, String> row : paramtable.getRows()) {
						if (row.containsKey("osm_stat_cd")) {
							String osm_stat_cd = row.get("osm_stat_cd");
							log.info("stroy file == " + osm_stat_cd);
							log.info("DB value ======= " + shpHdrvalues.get(0).get("OSM_STAT_CD"));
							Assert.assertEquals(osm_stat_cd, shpHdrvalues.get(0).get("OSM_STAT_CD"));
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Then("Validate SHP_LI table with below parameters:$parameters")
	public void validateshplivalues(ExamplesTable paramtable) {
		if (postOsmXml) {
			List<String> ordernumbers = getOrderNumberFromMPackage();
			for (String ordernumber : ordernumbers) {
				OsmXMLPosting.finalFulfillmentNumbr = ordernumber;
				List<Map<String, String>> resNbrList = DBMethods
						.getDBValuesAsListOfMaps("select RES_NBR from SHP_HDR where FULLFILLMENT_NBR ='"
								+ OsmXMLPosting.finalFulfillmentNumbr + "'", OSMdbSchema);
				String resNbr = resNbrList.get(0).get("RES_NBR");
				List<Map<String, String>> shpLivalues = DBMethods.getDBValuesAsListOfMaps(
						"Select OSM_DTL_STAT_CD,RES_LN_STAT,RQST_QTY,PCK_QTY,ORG_QTY,CNCL_QTY,ALLOC_QTY,sum(PACKED_QTY) as PACKED_QTY,SHORTED_QTY from SHP_LI where RES_NBR='"
								+ resNbr
								+ "' group by OSM_DTL_STAT_CD,RES_LN_STAT,RQST_QTY,PCK_QTY,ORG_QTY,CNCL_QTY,ALLOC_QTY,SHORTED_QTY",
						OSMdbSchema);

				for (Map<String, String> row : paramtable.getRows()) {
					if (row.containsKey("osm_dtl_stat_cd")) {
						String osm_dtl_stat_cd = row.get("osm_dtl_stat_cd");
						log.info("stroy file == " + osm_dtl_stat_cd);
						log.info("DB value ======= " + shpLivalues.get(0).get("OSM_DTL_STAT_CD"));
						Assert.assertEquals(osm_dtl_stat_cd, shpLivalues.get(0).get("OSM_DTL_STAT_CD"));
					}
				}
			}
		}

	}

	@Then("Validate SHP_PCKG and SHP_PCKG_LI tables")
	public void validateshpPkg() {
		if (postOsmXml) {
			List<String> ordernumbers = getOrderNumberFromMPackage();
			for (String ordernumber : ordernumbers) {
				OsmXMLPosting.finalFulfillmentNumbr = ordernumber;

				List<Map<String, String>> resNbrList = DBMethods
						.getDBValuesAsListOfMaps("select RES_NBR from SHP_HDR where FULLFILLMENT_NBR ='"
								+ OsmXMLPosting.finalFulfillmentNumbr + "'", OSMdbSchema);
				String resNbr = resNbrList.get(0).get("RES_NBR");
				List<Map<String, String>> mpkglistDb = DBMethods
						.getDBValuesAsListOfMaps("Select * from m_package where SHIPMENT_NUMBER = '"
								+ OsmXMLPosting.finalFulfillmentNumbr + "' and status!='99'  order by 1", "");
				List<Map<String, String>> shppkglist = DBMethods.getDBValuesAsListOfMaps(
						"Select * from SHP_PCKG where RES_NBR ='" + resNbr + "' order by 1 ", OSMdbSchema);
				List<Map<String, String>> shppkgLIlist = DBMethods.getDBValuesAsListOfMaps(
						"Select * from SHP_PCKG_LI where RES_NBR ='" + resNbr + "' order by 1 ", OSMdbSchema);
				List<Map<String, String>> shphdrlist = DBMethods
						.getDBValuesAsListOfMaps("Select * from SHP_HDR where FULLFILLMENT_NBR ='"
								+ OsmXMLPosting.finalFulfillmentNumbr + "' order by 1 ", OSMdbSchema);

				String mpkgdb = mpkglistDb.get(0).get("PACKAGE_NUMBER");
				String osmplg = shppkglist.get(0).get("PCKG_NBR");
				String dbtrnbr = mpkglistDb.get(0).get("TRACKING_NBR");
				String osmtrknbr = shppkglist.get(0).get("TRCK_NBR");
				String pkgli = shppkgLIlist.get(0).get("PCKG_NBR");
				String pkgfil = shppkglist.get(0).get("FIL_LOC_NBR");
				String liFil = shppkgLIlist.get(0).get("FIL_LOC_NBR");
				String liqty = shppkgLIlist.get(0).get("QTY");
				String shphdrfil = shphdrlist.get(0).get("FIL_LOC_NBR");

				log.info("package no from m_package " + mpkgdb);
				log.info("package no from SHP_PCKG " + osmplg);
				if (mpkgdb.equals(osmplg)) {
					log.info("PASS");
					Assert.assertTrue(true);
				} else {
					Assert.assertTrue(false);
				}

				if (dbtrnbr.equals(osmtrknbr)) {
					log.info("DB Trackingnbr: " + dbtrnbr);
					Assert.assertTrue(true);
					log.info("OSM Trackingnbr: " + osmtrknbr);
				} else {
					Assert.assertTrue(false);
				}

				if (mpkgdb.equals(pkgli)) {
					log.info("DB Package: " + mpkgdb);
					Assert.assertTrue(true);
					log.info("SHP_PCK_LI package: " + pkgli);
				} else {
					Assert.assertTrue(false);
				}
				if (pkgfil.equals(liFil)) {
					log.info("DB FIL_LOC_NBR: " + pkgfil);
					log.info("SHP_PCK_LI FIL_LOC_NBR: " + liFil);
					log.info("SHP_Hdr FIL_LOC_NBR: " + shphdrfil);
					if (shphdrfil.equals(liFil)) {
						log.info("SHP_PCKG_LI QTY: " + liqty);
						Assert.assertTrue(true);
					}
				} else {
					Assert.assertTrue(false);
					log.info("False");
				}
			}
		}
	}

	private void setOSMIntegrationFlag() {
		postOsmXml = false;
		postwmsXml = false;
		String proteusIntegration = "";
		String atlasIntegration = "";
		String talosIntegration = "";

		String getOSMIntegrationFlagQuery = Constants.GET_OSM_INTEGRATION_FLAG.replace("#CONFIG_CODE#",
				"OSM/OSM_INTEGRATION");
		List<Map<String, String>> osmIntegrationListInfo = DBMethods.getDBValuesAsListOfMaps(getOSMIntegrationFlagQuery,
				"");
		for (Map<String, String> osmIntegration : osmIntegrationListInfo) {
			if ("ATLAS_INTEGRATION".equals(osmIntegration.get("COLUMN_NAME"))) {
				atlasIntegration = osmIntegration.get("CONFIG_VALUE");
			} else if ("TALOS_INTEGRATION".equals(osmIntegration.get("COLUMN_NAME"))) {
				talosIntegration = osmIntegration.get("CONFIG_VALUE");
			} else if ("PROTEUS_INTEGRATION".equals(osmIntegration.get("COLUMN_NAME"))) {
				proteusIntegration = osmIntegration.get("CONFIG_VALUE");
			}
		}
		log.info("ATLAS_INTEGRATION-> " + atlasIntegration + "& PROTEUS_INTEGRATION-> " + proteusIntegration
				+ "& TALOS_INTEGRATION-> " + talosIntegration);

		if ("Y".equalsIgnoreCase(atlasIntegration) && "Y".equalsIgnoreCase(proteusIntegration)
				&& "N".equalsIgnoreCase(talosIntegration)) {
			postOsmXml = true;
		} else if ("N".equalsIgnoreCase(atlasIntegration) && "N".equalsIgnoreCase(proteusIntegration)
				&& "N".equalsIgnoreCase(talosIntegration)) {
			postwmsXml = true;
		}
	}
	
	public List<String> getOrderNumberFromMPackage(){
		String workBatchNumber ="";
		if(TalosUtils.getWorkBatchNumber().length()<15){
			workBatchNumber=TalosUtils.getWorkBatchNumber()+"001";
		}else{
			workBatchNumber=TalosUtils.getWorkBatchNumber();
		}
		String sql = "select SHIPMENT_NUMBER from m_package where WORK_BATCH_NBR='"+workBatchNumber+"' and STATUS='90'";
		List<String> orders = DBMethods.getDBValuesInList(sql);
		return orders;
		
	}

}
