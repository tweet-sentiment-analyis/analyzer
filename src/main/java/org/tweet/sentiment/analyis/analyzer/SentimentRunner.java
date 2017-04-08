package org.tweet.sentiment.analyis.analyzer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

/**
 * Created by Raphael on 04.04.17.
 */
public class SentimentRunner implements Runnable {

    private boolean isStopped;

    private AmazonSQS sqs;

    public SentimentRunner() {
        NLP.init();
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("sqs").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. ",
                    e);
        }
        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials));
        clientBuilder.setRegion(Regions.US_WEST_2.getName());
        sqs = clientBuilder.build();
    }

    public void run() {
        JSONParser parser = new JSONParser();

        try {
            while (! isStopped) {
                // Receive fetched tweets
                String queueUrl = sqs.getQueueUrl("fetched-tweets").getQueueUrl();
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
                List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

                if (messages.size() == 0) {
                    Thread.sleep(500);
                    continue;
                }


                for (Message msg : messages) {
                    JSONObject jsonObject = (JSONObject) parser.parse(msg.getBody());

                    JSONObject tweet = (JSONObject) jsonObject.get("tweet");

                    int sentiment = NLP.findSentiment((String) tweet.get("text"));
                    jsonObject.put("sentiment", sentiment);
                    //System.out.println(jsonObject.get("Sentiment"));


                    // Delete the fetched tweet
                    System.out.println("Deleting a message.\n");
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));

                    // Send the analyzed tweet
                    String dbQueue = sqs.getQueueUrl("analyised-tweets").getQueueUrl();
                    System.out.println("Sending a message to analyzed-tweets.\n");
                    sqs.sendMessage(new SendMessageRequest(dbQueue, jsonObject.toJSONString()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void terminate() {
        isStopped = true;
    }
}
