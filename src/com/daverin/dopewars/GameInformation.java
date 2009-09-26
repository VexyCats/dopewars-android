package com.daverin.dopewars;

import java.util.HashMap;
import android.util.Log;

public class GameInformation {
	public GameInformation(String serialized_game_info) {
		drugs_ = new HashMap<String, HashMap<String, Float>>();
		coats_ = new HashMap<String, HashMap<String, Float>>();
		guns_ = new HashMap<String, HashMap<String, Float>>();
		locations_ = new HashMap<String, HashMap<String, Float>>();
		String[] string_groups = serialized_game_info.split("&&");
		for (int i = 0; i < string_groups.length; ++i) {
			String[] group = string_groups[i].split("--", 2);
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals("game_id")) {
				game_id_ = group[1];
			} else if (group[0].equals("drugs")) {
				drugs_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("coats")) {
				coats_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("guns")) {
				guns_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("locations")) {
				locations_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("loan_interest_rate")) {
				loan_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("bank_interest_rate")) {
				bank_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("coat_likelihood")) {
				coat_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("gun_likelihood")) {
				gun_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("cops_likelihood")) {
				cops_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("starting_game_info")) {
				serialized_starting_game_info_ = group[1];
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	public String serializeGameInformation() {
		String serialized_game_info = "";
		serialized_game_info += "game_id--" + game_id_ + "&&" +
			"drugs--" + Global.serializeAttributeGroup(drugs_) + "&&" +
		    "coats--" + Global.serializeAttributeGroup(coats_) + "&&" +
		    "guns--" + Global.serializeAttributeGroup(guns_) + "&&" +
		    "locations--" + Global.serializeAttributeGroup(locations_) + "&&" +
		    "loan_interest_rate--" + Float.toString(loan_interest_rate_) + "&&" +
		    "bank_interest_rate--" + Float.toString(bank_interest_rate_) + "&&" +
		    "coat_likelihood--" + Float.toString(coat_likelihood_) + "&&" +
		    "gun_likelihood--" + Float.toString(gun_likelihood_) + "&&" +
		    "cops_likelihood--" + Float.toString(cops_likelihood_) + "&&" +
		    "starting_game_info--" + serialized_starting_game_info_;
		return serialized_game_info;
	}
	

	public static String getDefaultGameString() {
		return "game_id--default&&" +
		       "drugs--" +
		         "Weed==" +
		           "base_price:400|" + 
		           "price_variance:200|" + 
		           "icon:0|" +
		           "low_probability:0.1|" +
		           "low_multiplier:0.5__" +
		         "Acid==" +
		           "base_price:1500|" +
		           "price_variance:400|" +
		           "icon:1__" +
		         "Ludes==" +
		           "base_price:80|" +
		           "price_variance:20|" +
		           "icon:2__" +
		         "Heroin==" +
		           "base_price:10000|" +
		           "price_variance:2000|" +
		           "icon:3|" +
		           "high_probability:0.1|" +
		           "high_multiplier:2.0__" +
		         "Cocaine==" +
		           "base_price:20000|" +
		           "price_variance:3000|" +
		           "icon:4|" + 
		           "high_probability:0.1|" +
		           "high_multiplier:2.0__" +
		         "Shrooms==" +
		           "base_price:1000|" +
		           "price_variance:200|" +
		           "icon:5__" +
		         "Speed==" +
		           "base_price:110|" +
		           "price_variance:30|" +
		           "icon:6|" +
		           "low_probability:0.1|" +
		           "low_multiplier:0.5__" +
			     "Speed2==" +
			       "base_price:110|" +
			       "price_variance:30|" +
			       "icon:6|" +
			       "low_probability:0.1|" +
			       "low_multiplier:0.5__" +
			     "Speed3==" +
			       "base_price:110|" +
			       "price_variance:30|" +
			       "icon:6|" +
			       "low_probability:0.1|" +
			       "low_multiplier:0.5__" +
			     "Speed4==" +
			       "base_price:110|" +
			       "price_variance:30|" +
			       "icon:6|" +
			       "low_probability:0.1|" +
			       "low_multiplier:0.5__" +
			     "Speed5==" +
			       "base_price:110|" +
			       "price_variance:30|" +
			       "icon:6|" +
			       "low_probability:0.1|" +
			       "low_multiplier:0.5__" +
			     "Speed6==" +
			       "base_price:110|" +
			       "price_variance:30|" +
			       "icon:6|" +
			       "low_probability:0.1|" +
			       "low_multiplier:0.5__" +
		         "Hashish==" +
		           "base_price:180|" +
		           "price_variance:40|" +
		           "icon:7|" +
		           "low_probability:0.1|" +
		           "low_multiplier:0.5&&" +
		       "coats--" +
		         "Gucci==" +
		           "additional_space:10|" +
		           "base_price:2000|" +
		           "price_variance:200|" +
		           "space_factor:0.2__" +
		         "D&G==" +
		           "additional_space:20|" +
		           "base_price:4000|" +
		           "price_variance:400|" +
		           "space_factor:0.4__&&" +
		       "guns--" +
		         "Baretta==" +
		           "firepower:6|" +
		           "base_price:500|" +
		           "price_variance:100|" +
		           "space:5__" +
		         "Saturday Night Special==" +
		           "firepower:8|" +
		           "base_price:1000|" +
		           "price_variance:200|" +
		           "space:8__&&" +
		       "locations--" +
		         "Brooklyn==" +
		           "base_drugs:13|" +
		           "drug_variance:1|" +
		           "map_x:105|" +
		           "map_y:220|" +
		           "has_bank:1|" +
		           "has_loan_shark:1__" +
		         "Manhattan==" +
		           "base_drugs:8|" +
		           "drug_variance:2|" +
		           "map_x:80|" +
		           "map_y:5__" +
		         "Central Park==" +
		           "base_drugs:8|" +
		           "drug_variance:2|" +
		           "map_x:73|" +
		           "map_y:100__" +
		         "The Ghetto==" +
		           "base_drugs:8|" +
		           "drug_variance:2|" +
		           "map_x:80|" +
		           "map_y:335__" +
		         "The Bronx==" +
		           "base_drugs:8|" +
		           "drug_variance:2|" +
		           "map_x:75|" +
		           "map_y:143__" +
		         "Coney Island==" +
		           "base_drugs:8|" +
		           "drug_variance:2|" +
		           "map_x:103|" +
		           "map_y:60__&&" +
		       "loan_interest_rate--0.05&&" +
		       "bank_interest_rate--0.10&&" +
		       "coat_likelihood--0.1&&" +
		       "gun_likelihood--0.1&&" +
		       "cops_likelihood--0.1&&" +
		       "starting_game_info--" +
		         "cash--53000##" +
		         "loan--5500##" +
		         "location--0##" +
		         "space--100##" +
		         "max_space--100##" +
		         "days_left--10##" +
		         "bank--0##" +
		         "dealer_health--100";
	}
	
	public String game_id_;
	public HashMap<String, HashMap<String, Float>> drugs_;
	public HashMap<String, HashMap<String, Float>> coats_;
	public HashMap<String, HashMap<String, Float>> guns_;
	public HashMap<String, HashMap<String, Float>> locations_;
	public float loan_interest_rate_;
	public float bank_interest_rate_;
	public float coat_likelihood_;
	public float gun_likelihood_;
	public float cops_likelihood_;
	
	public String serialized_starting_game_info_;
}
