package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.tibco.tibjms.TibjmsQueueConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import javax.jms.*;

@Slf4j
public class CreateOrderPage extends BasePage{

    public CreateOrderPage(WebDriver driver) {
        super(driver);
    }
    SeUiContextBase seUiContextBase = new SeUiContextBase();

    public void sendQueueMessage() {

        try {
            String certFilePath = ".\\src\\test\\resources\\certs\\rtfcerts\\";
            TextMessage msg;

            log.info("Publishing to destination '" );

            TibjmsQueueConnectionFactory factory = new com.tibco.tibjms.TibjmsQueueConnectionFactory();

            factory.setServerUrl("ssl://tibenp09.federated.fds:7345");
            factory.setUserName("admd2cwms");
            factory.setUserPassword("admd2cwms");
            //factory.setSSLIdentityEncoding("PKCS12");
            //factory.setSSLIdentity(certFilePath + "WMSESS.p12");
            factory.setSSLIdentity(certFilePath + "WMSESSNP.P12");
            //factory.setSSLTrustedCertificate(certFilePath+"MacysRootCA.pem");
            factory.setSSLTrustedCertificate(certFilePath + "MACYSJCEICA01.pem");
            factory.setSSLTrustedCertificate(certFilePath + "MACYSORCAJC256.pem");
            factory.setSSLPassword("badpassword1");
            factory.setSSLEnableVerifyHost(true);
            factory.setSSLEnableVerifyHostName(true);
            factory.setSSLTrace(true);
            factory.setSSLDebugTrace(false);
            log.info("MSTEMSSender - Queue Connection String -" + factory.toString());

            QueueConnection connection = factory.createQueueConnection();
            QueueSession session = connection.createQueueSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
            //Queue senderQueue = session.createQueue("M.AIN.EOSEMS.ORDER.ORDEREVENTS.V1");
            Queue senderQueue = session.createQueue("M.D2C.WMJP.ORDER.OSM.EVENT.RECEIVE");
            QueueSender sender = session.createSender(senderQueue);

            /* publish messages */
            TextMessage jmsMessage = session.createTextMessage();

            jmsMessage.setStringProperty("eventType", "SHPRTFIL");
            jmsMessage.setStringProperty("fillLocationNbr", "44");
            jmsMessage.setStringProperty("sellZLDivisionNbr", "71");
            jmsMessage.setStringProperty("orderID", "502342019");

            /* set message text */
            String xmlPath = "src/test/resources/RequestTemplates/Customer Order Single Line Item.txt";
            String messageStr = CommonUtils.readAllBytes(xmlPath);
            jmsMessage.setText(messageStr);

            /* publish message */
            sender.send(jmsMessage);
            log.info("Published message: " + messageStr);

            connection.close();

           /* //adding ssl certificates
            qc.setSSLIdentity(".\\src\\test\\resources\\certs\\rtfcerts\\WMSESS.p12");
            qc.setSSLTrustedCertificate(".\\src\\test\\resources\\certs\\rtfcerts\\MacysRootCA.pem");
            qc.setSSLIdentityEncoding("PKCS12");
            qc.setSSLPassword("badpassword1");
            qc.setSSLEnableVerifyHost(true);

            System.out.println("UserName is as: admd2cwms and password is as: ");

            QueueConnection queuecon = qc.createQueueConnection("wmsuser", "badpassword1");

            QueueSession queuesession = queuecon.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            // Use createQueue() to enable sending into dynamic queues.
            Queue senderQueue = queuesession.createQueue("M.AIN.EOSEMS.ORDER.ORDEREVENTS.V1");
            QueueSender  sender = queuesession.createSender(senderQueue);*/

            /* create text message */
            /*msg = queuesession.createTextMessage();

            msg.setStringProperty("eventType", "SHPRTFIL");
            msg.setStringProperty("fillLocationNbr", "44");
            msg.setStringProperty("sellZLDivisionNbr", "71");
            msg.setStringProperty("orderID", "502342019");

            *//* set message text *//*
            String xmlPath = "src/test/resources/RequestTemplates/OrderXmls/SingleOrder.txt";
            String messageStr = CommonUtils.readAllBytes(xmlPath);

            msg.setText(messageStr);

            *//* publish message *//*
            sender.send(msg);
            log.info("Published message: " + messageStr);

            *//* close the connection *//*
            queuecon.close();*/

        } catch (JMSException e) {
            e.printStackTrace();
            log.info("exception in sendQueueMessage() "+e);
        }
    }




}
