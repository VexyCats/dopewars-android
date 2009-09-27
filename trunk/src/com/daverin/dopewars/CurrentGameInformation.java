/**
 * This file, along with the GameInformation class, mostly facilitates the serialization of game
 * settings for easy storage and retrieval from the database as a string. This has proved to be
 * significantly easier and more efficient (unproven) than making many small database transactions
 * in each function every time information about the game is needed.
 * 
 * TODO: static-ize all the string constants in here, or use a strings file or something
 * TODO: better class name?
 */

package com.daverin.dopewars;

import java.util.HashMap;

import android.util.Log;

public class CurrentGameInformation {
	
	// Initialize the current game info with a serialized version.
	public CurrentGameInformation(String serialized_current_game_info) {
		dealer_inventory_ = new HashMap<String, Float>();
		dealer_guns_ = new HashMap<String, Float>();
		location_inventory_ = new HashMap<String, Float>();
		coat_inventory_ = new HashMap<String, Float>();
		gun_inventory_ = new HashMap<String, Float>();
		messages_ = new HashMap<String, Float>();
		String[] string_groups = serialized_current_game_info.split("&&");
		for (int i = 0; i < string_groups.length; ++i) {
			String[] group = string_groups[i].split("--");
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals("cash")) {
				cash_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("space")) {
				space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("max_space")) {
				max_space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("loan")) {
				loan_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("bank")) {
				bank_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("location")) {
				location_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("days_left")) {
				days_left_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("dealer_health")) {
				dealer_health_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("cops_health")) {
				cops_health_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("dealer_inventory")) {
				dealer_inventory_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("dealer_guns")) {
				dealer_guns_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("location_inventory")) {
				location_inventory_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("coat_inventory")) {
				coat_inventory_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("gun_inventory")) {
				gun_inventory_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("messages")) {
				messages_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("do_initial_setup")) {
				do_initial_setup_ = Integer.parseInt(group[1]);
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	// Serializes all the information stored in this game information object in a very specialized
	// and simple format.
	// TODO: consider xml or something else standard
	public String serializeCurrentGameInformation() {
		String serialized_game_info = "";
		serialized_game_info += "cash--" + Integer.toString(cash_) + "&&" +
			"space--" + Integer.toString(space_) + "&&" +
			"max_space--" + Integer.toString(max_space_) + "&&" +
			"loan--" + Integer.toString(loan_) + "&&" +
			"bank--" + Integer.toString(bank_) + "&&" +
			"location--" + Integer.toString(location_) + "&&" +
			"days_left--" + Integer.toString(days_left_) + "&&" +
			"dealer_health--" + Integer.toString(dealer_health_) + "&&" +
			"cops_health--" + Integer.toString(cops_health_) + "&&" +
			"dealer_inventory--" + Global.serializeAttributes(dealer_inventory_) + "&&" +
			"dealer_guns--" + Global.serializeAttributes(dealer_guns_) + "&&" +
			"location_inventory--" + Global.serializeAttributes(location_inventory_) + "&&" +
			"coat_inventory--" + Global.serializeAttributes(coat_inventory_) + "&&" +
			"gun_inventory--" + Global.serializeAttributes(gun_inventory_) + "&&" +
			"messages--" + Global.serializeAttributes(messages_) + "&&" +
			"do_initial_setup--" + Integer.toString(do_initial_setup_);
		return serialized_game_info;
	}
	
	int cash_;
	int space_;
	int max_space_;
	int loan_;
	int bank_;
	int location_;
	int days_left_;
	int dealer_health_;
	int cops_health_;
	HashMap<String, Float> dealer_inventory_;
	HashMap<String, Float> dealer_guns_;
	HashMap<String, Float> location_inventory_;
	HashMap<String, Float> coat_inventory_;
	HashMap<String, Float> gun_inventory_;
	HashMap<String, Float> messages_;
	public int do_initial_setup_;
}
