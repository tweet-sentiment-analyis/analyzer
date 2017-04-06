package org.tweet.sentiment.analyis.analyzer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/**
 * Created by Raphael on 04.04.17.
 */
public class SentimentRunner implements Runnable {

    private boolean isStopped;

    private String queueUrl;

    private AmazonSQS sqs;

    public SentimentRunner() {
        NLP.init();
         /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
       /* AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials));
        clientBuilder.setRegion(Regions.EU_CENTRAL_1.getName());
        sqs = clientBuilder.build();*/
    }

    public void run() {
        JSONParser parser = new JSONParser();

        // Get twitter message from queue
        try {
            while (!isStopped) {
                // Receive messages
               /* System.out.println("Receiving messages from MyQueue.\n");
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
                List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
                for (Message message : messages) {
                    System.out.println("  Message");
                    System.out.println("    MessageId:     " + message.getMessageId());
                    System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                    System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                    System.out.println("    Body:          " + message.getBody());
                    for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                        System.out.println("  Attribute");
                        System.out.println("    Name:  " + entry.getKey());
                        System.out.println("    Value: " + entry.getValue());
                    }
                }*/

                Object obj = parser.parse(new FileReader("/Users/Raphael/Documents/UZH/2_Semester/testMessage.json"));
                JSONObject jsonObject = (JSONObject) obj;

                String text = (String) jsonObject.get("text");
                int sentiment = NLP.findSentiment(text);
                jsonObject.put("Sentiment", sentiment);
                String msg = jsonObject.toJSONString();


                // REPLACE WITH PUT INTO QUEUE CODE
                // Send a message
               /* System.out.println("Sending a message to MyQueue.\n");
                sqs.sendMessage(new SendMessageRequest(queueUrl, msg));*/

                System.out.println(msg);
                System.out.println(jsonObject.get("Sentiment"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void terminate() {
        isStopped = true;
    }
}
