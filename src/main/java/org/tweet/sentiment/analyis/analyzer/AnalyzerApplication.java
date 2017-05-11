package org.tweet.sentiment.analyis.analyzer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AnalyzerApplication {

    private static final Logger logger          = Logger.getLogger(AnalyzerApplication.class.getName());
    private static final String NO_THREADS      = "NO_THREADS";
    private static final int    DEFAULT_THREADS = 1;

    private static ExecutorService executorService;

    public static void main(String[] args) {
        logger.info("Starting sentiment analyzer...");
        logger.info("Looking for environment variable '" + NO_THREADS + "' for number of threads...");

        int noOfThreads = DEFAULT_THREADS;
        if (null == System.getenv(NO_THREADS)) {
            logger.warning("Did not found environment variable specifying no. of threads. Assuming " + DEFAULT_THREADS);
        } else {
            logger.info("Found environment variable specifying no. of threads");
            noOfThreads = Integer.parseInt(System.getenv(NO_THREADS));
        }

        logger.info("Starting sentiment analyzer using '" + noOfThreads + "' threads.");
        executorService = Executors.newFixedThreadPool(noOfThreads);

        // starting dynamic number of same thread
        for (int i = 0; i < noOfThreads; i++) {
            FetchAnalyzeComponent sentimentThread = new FetchAnalyzeComponent();
            executorService.submit(sentimentThread);
        }
    }
}