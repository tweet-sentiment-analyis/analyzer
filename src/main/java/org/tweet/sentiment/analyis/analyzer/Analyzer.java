package org.tweet.sentiment.analyis.analyzer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.logging.Logger;

public class Analyzer {

    private static final Logger logger = Logger.getLogger(Analyzer.class.getName());

    private StanfordCoreNLP pipeline;

    public Analyzer() {
        this.pipeline = new StanfordCoreNLP("NLPPropFile.properties");
    }

    public int findSentiment(String tweet) {
        logger.info("Analyzing tweet: '" + tweet + "'");

        if (tweet == null || tweet.length() < 1) {
            logger.warning("Tweet was null or length equal to zero. Assuming sentiment 0");
            return 0;
        }

        int mainSentiment = 0;
        int longest = 0;
        Annotation annotation = pipeline.process(tweet);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentAnnotatedTree.class);
            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            String partText = sentence.toString();

            if (partText.length() > longest) {
                mainSentiment = sentiment;
                longest = partText.length();
            }
        }

        logger.info("Found sentiment value '" + mainSentiment + "' for tweet '" + tweet + "'");
        return mainSentiment;
    }
}