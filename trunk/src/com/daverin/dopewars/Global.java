package com.daverin.dopewars;

import java.util.Random;
import java.util.Vector;

public class Global {
	// These are the drug attributes that the game knows how to handle.
	public static String DRUG_ATTR_BASE_PRICE = "base_price";
	public static String DRUG_ATTR_PRICE_VARIANCE = "price_variance";
	
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
	
	public static String chooseDrugPrice(String drug_attributes) {
		int base_price = Integer.parseInt(parseAttribute("base_price", drug_attributes));
		int price_variance = Integer.parseInt(parseAttribute("price_variance", drug_attributes));
		int price = (int)(base_price - price_variance / 2.0 +
				Global.rand_gen_.nextDouble() * price_variance);
		/*
		if (Boolean.parseBoolean(cursor.getString(3))) {
			if (Global.rand_gen_.nextDouble() < Double.parseDouble(cursor.getString(7))) {
				price = (int)(price * Double.parseDouble(cursor.getString(8)));
			}
		}
		if (Boolean.parseBoolean(cursor.getString(4))) {
			if (Global.rand_gen_.nextDouble() < Double.parseDouble(cursor.getString(5))) {
				price = (int)(price / Double.parseDouble(cursor.getString(6)));
			}
		}
		*/
		return Integer.toString(price);
	}
	
	// Random number generator!
    public static Random rand_gen_ = new Random();
}
