package com.macys.mst.DC2.EndToEnd.execdrivers;

import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.config.GetPasswordCyberArk;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.serenityJbehaveJira.Executiondriver;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.foundationalServices.StepDefinitions.MHESteps.MHESteps;
import com.macys.mst.whm.coreautomation.db.PasswordManager;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ExecutionConfig extends Executiondriver {
	private static Logger log = Logger.getLogger(MHESteps.class.getName());
	public static ThreadLocal<String> passwordobj = new ThreadLocal<String>();
	public static ThreadLocal<String> cyberarksafe = new ThreadLocal<String>();
	public static ThreadLocal<String> cyberarkappid = new ThreadLocal<String>();
	public static ThreadLocal<String> appUrlUIPwdObjId = new ThreadLocal<String>();
	public static String appUIUserName;
	public static String appUIPassword;

	public static String getExecEnv() {
		return execEnv;
	}

	public static void setExecEnv(String execEnv) {
		ExecutionConfig.execEnv = execEnv;
	}

	public static String execEnv;

	public static void setHeaders(Map<String, String> headers) {
		ExecutionConfig.headers = headers;
	}
	public static Map<String,String> headers= new HashMap<String,String>();

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		ExecutionConfig.password = password;
	}
	private static String password ;



	@Override
	public void calserenitylocaltestrunner() {
		SerenityLocalRunConfig SerRun = new SerenityLocalRunConfig();
		getDbPwd();
		appUIPassword = getUIuserPwd();
		RestUtilities.setHeaders(getHeaders());
		WhmRestCoreAutomationUtils.setHeaders(getHeaders());
		try {
			SerRun.run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void getDbPwd(){
		cyberarksafe.set(FileConfig.getInstance().getStringConfigValue("cyberark.safe"));
		cyberarkappid.set(FileConfig.getInstance().getStringConfigValue("cyberark.appid"));
		passwordobj.set(FileConfig.getInstance().getStringConfigValue("cyberark.pwdobjectid"));
		password = (GetPasswordCyberArk.getpassword(cyberarksafe.get(), cyberarkappid.get(), passwordobj.get()));
		setPassword(password);
		String execenv = LocalDriverManager.getInstance().getexecenvflag();
		log.info("execenv : " +execenv);
		setExecEnv(execenv);
	}

	private String getUIuserPwd(){
		appUrlUIPwdObjId.set(FileConfig.getInstance().getStringConfigValue("AppUrls.password"));
		appUIUserName = FileConfig.getInstance().getStringConfigValue("AppUrls.userName");
		return GetPasswordCyberArk.getpassword(cyberarksafe.get(), cyberarkappid.get(), appUrlUIPwdObjId.get());
	}

	public static Map<String, String> getHeaders() {
		Map<String,String> headersArg = new HashMap<String,String>();
        headersArg.put("X-WHM-JWT", PasswordManager.getHeaders().get("X-WHM-JWT"));
        setHeaders(headersArg);
		return headersArg;
	}
}