package org.tweet.sentiment.analyis.analyzer;

public class WhatToThink {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		NLP.init();
		long afterInit = System.currentTimeMillis();
		String tweet = "Finally, an explanation for daylight savings that makes sense";
		System.out.println(tweet + " : " + NLP.findSentiment(tweet));
		long b = System.currentTimeMillis();
		String te = "RT @NRDC: These climate programs would be axed under Trump's proposed budget. https:t.coijRd56F7uI via @insideclimate";
		System.out.println(te + " : " + NLP.findSentiment(te));
		
		long c = System.currentTimeMillis();
		te = "The news media spent SIX HUNDRED CONSECUTIVE DAYS on Hillary's emails. No Trump story, however egregious, got e\u2026";
		System.out.println(te + " : " + NLP.findSentiment(te));
		System.out.println(System.currentTimeMillis()-start);
		System.out.println(System.currentTimeMillis()-afterInit);
		System.out.println(System.currentTimeMillis()-b);
		System.out.println(System.currentTimeMillis()-c);
	}
}