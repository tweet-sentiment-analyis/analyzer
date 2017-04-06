package org.tweet.sentiment.analyis.analyzer;

public class WhatToThink {

	private static int nThreads = 1;

	public static void main(String[] args) {
		SentimentRunner sentimentThread = new SentimentRunner();

		for (int i = 0; i< nThreads; i++) {
			Thread t = new Thread(sentimentThread);
			t.start();
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		sentimentThread.terminate();

	}
}