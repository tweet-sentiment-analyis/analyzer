package org.tweet.sentiment.analyis.analyzer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
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
import java.util.logging.Logger;

public class FetchAnalyzeComponent implements Runnable {

    private static final Logger logger = Logger.getLogger(FetchAnalyzeComponent.class.getName());
    private static final int FETCH_TIMEOUT = 200;

    private boolean   isStopped;
    private AmazonSQS sqs;
    private Analyzer  analyzer;

    public FetchAnalyzeComponent() {
        this.analyzer = new Analyzer();

        AWSCredentials credentials;
        try {
            credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the environment. " +
                            "Please make sure that your credentials are located in the environment variables " +
                            "AWS_ACCESS_KEY_ID resp. AWS_SECRET_ACCESS_KEY",
                    e);
        }

        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials));
        clientBuilder.setRegion(Regions.US_WEST_2.getName());
        sqs = clientBuilder.build();
    }

    public void run() {
        logger.info("Starting FetchAnalyzeComponent...");

        JSONParser parser = new JSONParser();

        try {
            while (! isStopped) {
                // Receive fetched tweets
                String queueUrl = sqs.getQueueUrl("fetched-tweets").getQueueUrl();
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
                List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

                if (messages.size() == 0) {
                    Thread.sleep(FETCH_TIMEOUT);
                    continue;
                }

                for (Message msg : messages) {
                    JSONObject jsonObject = (JSONObject) parser.parse(msg.getBody());

                    JSONObject tweet = (JSONObject) jsonObject.get("tweet");

                    int sentiment = this.analyzer.findSentiment((String) tweet.get("text"));
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
