package org.tweet.sentiment.analyis.analyzer;

import java.io.FileReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WhatToThink {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		NLP.init();
		long afterInit = System.currentTimeMillis();

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(
					"/Users/Raphael/Documents/UZH/2_Semester/testMessage.json"));

			JSONObject jsonObject = (JSONObject) obj;

			String text = (String) jsonObject.get("text");
            long a = System.currentTimeMillis();
			System.out.println(text + " : " + + NLP.findSentiment(text));
            long c = System.currentTimeMillis();
            System.out.println(c-a);
            System.out.println(afterInit-start);

            String msg = jsonObject.toJSONString();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}