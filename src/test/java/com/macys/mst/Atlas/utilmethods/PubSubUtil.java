package com.macys.mst.Atlas.utilmethods;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PubSubUtil implements MessageReceiver{

    private SubscriptionAdminClient subscriptionAdminClient;
    private ProjectSubscriptionName projectSubscriptionName;
    private Subscriber subscriber;
    CommonUtils commonUtils = new CommonUtils();
    private final String projectId = commonUtils.getEnvConfigValue("GCPProjId");

    private String MatchingMessage;
    private List<String> ListOfMatchingMessages = new LinkedList<>();

    public String getMatchingMessage() {
        return MatchingMessage;
    }

    public void setMatchingMessage(String matchingMessage) {
        MatchingMessage = matchingMessage;
    }

    private static List<PubsubMessage> messages = new ArrayList<>();

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
        log.info("Receiving messages........");
        messages.add(message);
        log.info("Message inside consumer :" + message.getData().toStringUtf8());
        StepDetail.addDetail("Message inside consumer :" + message.getData().toStringUtf8(), true);
        consumer.ack();
    }

    public synchronized String readMessages(String validationContent, int delayTime) throws InterruptedException {
        TimeUnit.SECONDS.sleep(delayTime);
        List<PubsubMessage> nonMatchedMessages = new ArrayList<>();
        if (messages != null && messages.size() > 0) {
            for (int size = 0; size < messages.size(); ) {
                PubsubMessage message = messages.remove(size);
                String messageInString = message.getData().toStringUtf8();
                if (messageInString.contains(validationContent)) {
                    commonUtils.doJbehavereportConsolelogAndAssertion("MATCHING MESSAGE FOUND :",messageInString,true);
                    setMatchingMessage(messageInString);
                    break;
                } else {
                    setMatchingMessage(null);
                    nonMatchedMessages.add(message);
                }
                log.info("Messages Size after polling :" + messages.size());
                StepDetail.addDetail("Messages Size after polling :" + messages.size(), false);
            }
        }
        messages.addAll(nonMatchedMessages);
        return getMatchingMessage();
    }


    public void createSubscription(String subscriptionId, String topicId) throws IOException {
        try {
            if (null == subscriptionAdminClient) {
                subscriptionAdminClient = SubscriptionAdminClient.create();
            }

            if (null == projectSubscriptionName) {
                projectSubscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
            }
            ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);

            subscriptionAdminClient.createSubscription(projectSubscriptionName, topicName, PushConfig.getDefaultInstance(), 0);

        } catch (Exception ex) {
            log.info("Creation of subscriber " + subscriptionId + " is not successful");
            ex.printStackTrace();
        }
    }

    public synchronized void subscribeProject(String subscriptionId) {
        if (null == projectSubscriptionName) {
            projectSubscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
        }
        subscriber = Subscriber.newBuilder(projectSubscriptionName, new PubSubUtil()).build();
        subscriber.startAsync().awaitRunning();
    }

    public void stopSubscriber(){
        subscriber.stopAsync();
    }

    public void deleteSubscription(String subscriptionId) throws IOException {
        try {
            if (null == subscriptionAdminClient) {
                subscriptionAdminClient = SubscriptionAdminClient.create();
            }

            if (null == projectSubscriptionName) {
                projectSubscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
            }
            subscriptionAdminClient.deleteSubscription(projectSubscriptionName);
        } catch (Exception ex) {
            log.info("Deletion of subscriber "+subscriptionId+" is not successful");
            ex.printStackTrace();
        }
    }


    public synchronized void publishMessage (String ProjectId, String topic, List < String > messages) throws Exception {
        ProjectTopicName topicName = ProjectTopicName.of(ProjectId, topic);
        Publisher publisher = null;

        try {

            publisher = Publisher.newBuilder(topicName).build();
            for (String message : messages) {
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                if (throwable instanceof ApiException) {
                                    ApiException apiException = ((ApiException) throwable);

                                    log.info("ERROR code :" + apiException.getStatusCode().getCode()+"Retryable : "+apiException.isRetryable());
                                }
                                log.info("ERROR PUBLISHING MESSAGE :" + message);
                                StepDetail.addDetail("ERROR PUBLISHING MESSAGE :" + message, true);
                            }

                            @Override
                            public void onSuccess(String messageId) {
                                log.info("Message Successfully published " + messageId);
                                StepDetail.addDetail("Message Successfully published " + messageId, true);
                            }
                        },
                        MoreExecutors.directExecutor());
            }

        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
                log.info("PUBLISHER STOPPED");
                StepDetail.addDetail("PUBLISHER STOPPED", true);
            }
        }

    }


}