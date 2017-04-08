package org.tweet.sentiment.analyis.analyzer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhatToThink {

	private static int nThreads = 1;

	private static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		SentimentRunner sentimentThread = new SentimentRunner();

		executorService.submit(sentimentThread);

	}
}