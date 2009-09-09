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
			String[] group = string_groups[i].split("--");
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals("drugs")) {
				drugs_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("coats")) {
				coats_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("guns")) {
				guns_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("locations")) {
				locations_ = Global.deserializeAttributeGroup(group[1]);
			} else if (group[0].equals("loan_location")) {
				loan_location_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("bank_location")) {
				bank_location_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("loan_interest_rate")) {
				loan_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("bank_interest_rate")) {
				bank_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("coat_likelihood")) {
				coat_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals("gun_likelihood")) {
				gun_likelihood_ = Float.parseFloat(group[1]);
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	public String serializeGameInformation() {
		String serialized_game_info = "";
		serialized_game_info += "drugs--" + Global.serializeAttributeGroup(drugs_) + "&&" +
		    "coats--" + Global.serializeAttributeGroup(coats_) + "&&" +
		    "guns--" + Global.serializeAttributeGroup(guns_) + "&&" +
		    "locations--" + Global.serializeAttributeGroup(locations_) + "&&" +
		    "loan_location--" + Integer.toString(loan_location_) + "&&" +
		    "bank_location--" + Integer.toString(bank_location_) + "&&" +
		    "loan_interest_rate--" + Float.toString(loan_interest_rate_) + "&&" +
		    "bank_interest_rate--" + Float.toString(bank_interest_rate_) + "&&" +
		    "coat_likelihood--" + Float.toString(coat_likelihood_) + "&&" +
		    "gun_likelihood--" + Float.toString(gun_likelihood_);
		return serialized_game_info;
	}
	
	public HashMap<String, HashMap<String, Float>> drugs_;
	public HashMap<String, HashMap<String, Float>> coats_;
	public HashMap<String, HashMap<String, Float>> guns_;
	public HashMap<String, HashMap<String, Float>> locations_;
	public int loan_location_;
	public int bank_location_;
	public float loan_interest_rate_;
	public float bank_interest_rate_;
	public float coat_likelihood_;
	public float gun_likelihood_;
}
