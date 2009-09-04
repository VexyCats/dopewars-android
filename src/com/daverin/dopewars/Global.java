package com.daverin.dopewars;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class Global {
	// These are the drug attributes that the game knows how to handle.
	public static String DRUG_ATTR_BASE_PRICE = "base_price";
	public static String DRUG_ATTR_PRICE_VARIANCE = "price_variance";
	public static HashMap<String,Integer> drug_icons_;
	
	public static void loadIcons() {
		drug_icons_ = new HashMap<String,Integer>();
		drug_icons_.put("weed", R.drawable.weed);
		drug_icons_.put("acid", R.drawable.acid);
		drug_icons_.put("ludes", R.drawable.ludes);
		drug_icons_.put("heroin", R.drawable.heroin);
		drug_icons_.put("cocaine", R.drawable.cocaine);
		drug_icons_.put("shrooms", R.drawable.shrooms);
		drug_icons_.put("speed", R.drawable.speed);
		drug_icons_.put("hashish", R.drawable.hashish);
	}
	
	public static int attributeCount(String attribute_list) {
		if (attribute_list.equals("")) {
			return 0;
		}
		return attribute_list.split("\\|").length;
	}
	
	public static String[] getAttribute(int attr, String attribute_list) {
		String[] attributes = attribute_list.split("\\|");
		if (attr >= attributes.length) {
			return attributes[0].split(":");
		} else {
			return attributes[attr].split(":");
		}
	}
	
	public static HashMap<String, String> parseAllAttributes(String attribute_list) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		String[] attribute_array = attribute_list.split("\\|");
		for (int i = 0; i < attribute_array.length; ++i) {
			String[] attribute_pair = attribute_array[i].split(":");
			if (attribute_pair.length == 2) {
				attributes.put(attribute_pair[0], attribute_pair[1]);
			}
		}
		return attributes;
	}
	
	public static String parseAttribute(String attr, String attribute_list) {
		String[] attributes = attribute_list.split("\\|");
		for (int i = 0; i < attributes.length; ++i) {
			String[] attr_pair = attributes[i].split(":");
			if (attr_pair.length == 2) {
				if (attr_pair[0].equals(attr)) {
					return attr_pair[1];
				}
			}
		}
		return "";
	}
	
	public static String setAttribute(String attr, String new_value, String attribute_list) {
		String new_attributes = "";
		boolean attr_found = false;
		String[] attributes = attribute_list.split("\\|");
		for (int i = 0; i < attributes.length; ++i) {
			if (new_attributes != "") {
				new_attributes += "|";
			}
			String[] attr_pair = attributes[i].split(":");
			if (attr_pair[0].equals(attr)) {
				new_attributes += attr + ":" + new_value;
				attr_found = true;
			} else {
				new_attributes += attributes[i];
			}
		}
		if (!attr_found) {
			if (new_attributes != "") {
				new_attributes += "|";
			}
			new_attributes += attr + ":" + new_value;
		}
		return new_attributes;
	}
	
	public static String removeAttribute(String attr, String attribute_list) {
		String new_attributes = "";
		String[] attributes = attribute_list.split("\\|");
		for (int i = 0; i < attributes.length; ++i) {
			String[] attr_pair = attributes[i].split(":");
			if (!attr_pair[0].equals(attr)) {
				if (new_attributes != "") {
					new_attributes += "|";
				}
				new_attributes += attributes[i];
			}
		}
		return new_attributes;
	}
	
	// The first element of the vector is always the price, any other elements are messages
	// to be shown about the price.
	public static Vector<String> chooseDrugPrice(String name, String drug_attributes) {
		Vector<String> price_and_messages = new Vector<String>();
		HashMap<String, String> drug_attribute_map = parseAllAttributes(drug_attributes);
		int base_price = Integer.parseInt(drug_attribute_map.get("base_price"));
		int price_variance = Integer.parseInt(drug_attribute_map.get("price_variance"));
		int price = (int)(base_price - price_variance / 2.0 +
				Global.rand_gen_.nextDouble() * price_variance);
		// Check for price jumps
		if (drug_attribute_map.get("low_probability") != null) {
			float low_probability = Float.parseFloat(drug_attribute_map.get("low_probability"));
			if (rand_gen_.nextFloat() < low_probability) {
				float multiplier = Float.parseFloat(drug_attribute_map.get("low_multiplier"));
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very low prices!");
			}
		} else if (drug_attribute_map.get("high_probability") != null) {
			float high_probability = Float.parseFloat(drug_attribute_map.get("high_probability"));
			if (rand_gen_.nextFloat() < high_probability) {
				float multiplier = Float.parseFloat(drug_attribute_map.get("high_multiplier"));
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very high prices!");
			}
		}
		price_and_messages.insertElementAt(Integer.toString(price), 0);
		return price_and_messages;
	}
	
	// Random number generator!
    public static Random rand_gen_ = new Random();
}
