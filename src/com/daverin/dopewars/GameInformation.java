/**
 * This file, along with the CurrentGameInformation class, mostly facilitates the serialization of
 * game settings for easy storage and retrieval from the database as a string. This has proved to
 * be significantly easier and more efficient (unproven) than making many small database
 * transactions in each function every time information about the game is needed.
 * 
 * TODO: static-ize all the string constants in here, or use a strings file or something
 * TODO: better class name?
 */

package com.daverin.dopewars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

public class GameInformation {
	
	public static String DELIMITER_1 = "&&";
	public static String DELIMITER_2 = "--";
	public static String DELIMITER_3 = "__";
	public static String DELIMITER_3a = "==";
	public static String DELIMITER_4 = "%%";
	public static String DELIMITER_5 = "::";
	
	public static String DEALER_CASH = "cash";
	public static String DEALER_SPACE = "space";
	public static String DEALER_MAX_SPACE = "max_space";
	public static String DEALER_LOAN = "loan";
	public static String DEALER_BANK = "bank";
	public static String DEALER_LOCATION = "location";
	public static String DEALER_DAYS_LEFT = "days_left";
	public static String DEALER_HEALTH = "dealer_health";
	public static String DEALER_COPS = "cops_health";
	public static String DEALER_INVENTORY = "dealer_inventory";
	public static String DEALER_GUNS = "dealer_guns";
	public static String LOCATION_DRUGS = "location_inventory";
	public static String LOCATION_COATS = "coat_inventory";
	public static String LOCATION_GUNS = "gun_inventory";
	public static String LOCATION_MESSAGES = "messages";
	public static String FIGHT_MESSAGES = "fight_messages";
	
	public static String DEALER_COPS_KILLED = "cops_killed";
	public static String DEALER_DEPUTIES_KILLED = "deputies_killed";
	public static String DEALER_SUCCESSFUL_RUNS = "times_ran";
	public static String DEALER_RUN_ATTEMPTS = "run_attempts";
	
	public static String GAME_ID = "game_id";
    public static String GAME_DRUGS = "drugs";
	public static String GAME_COATS = "coats";
	public static String GAME_GUNS = "guns";
	public static String GAME_LOCATIONS = "locations";
	public static String GAME_LOAN_INTEREST_RATE = "loan_interest_rate";
	public static String GAME_BANK_INTEREST_RATE = "bank_interest_rate";
	public static String GAME_COAT_LIKELIHOOD = "coat_likelihood";
	public static String GAME_GUN_LIKELIHOOD = "gun_likelihood";
	public static String GAME_COPS_LIKELIHOOD = "cops_likelihood";
	public static String GAME_STARTING_GAME_INFO = "starting_game_info";
	
	public static String GAME_DO_INITIALIZE = "do_initial_setup";
	
	public void setCurrentGameInformation(String serialized_current_game_info) {
		dealer_drugs_ = new HashMap<String, Integer>();
		dealer_guns_ = new HashMap<String, Integer>();
		location_drugs_ = new HashMap<String, Integer>();
		location_coats_ = new HashMap<String, Integer>();
		location_guns_ = new HashMap<String, Integer>();
		game_messages_ = new Vector<String>();
		fight_messages_ = new Vector<String>();
		
		String[] string_groups = serialized_current_game_info.split(DELIMITER_1);
		for (int i = 0; i < string_groups.length; ++i) {
			String[] group = string_groups[i].split(DELIMITER_2);
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals(DEALER_CASH)) {
				dealer_cash_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_SPACE)) {
				dealer_space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_MAX_SPACE)) {
				dealer_max_space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_LOAN)) {
				dealer_loan_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_BANK)) {
				dealer_bank_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_LOCATION)) {
				location_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_DAYS_LEFT)) {
				game_days_left_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_HEALTH)) {
				dealer_health_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_COPS)) {
				location_cops_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_INVENTORY)) {
				dealer_drugs_ = parseStringToIntMap(group[1]);
			} else if (group[0].equals(DEALER_GUNS)) {
				dealer_guns_ = parseStringToIntMap(group[1]);
			} else if (group[0].equals(LOCATION_DRUGS)) {
				location_drugs_ = parseStringToIntMap(group[1]);
			} else if (group[0].equals(LOCATION_COATS)) {
				location_coats_ = parseStringToIntMap(group[1]);
			} else if (group[0].equals(LOCATION_GUNS)) {
				location_guns_ = parseStringToIntMap(group[1]);
			} else if (group[0].equals(LOCATION_MESSAGES)) {
				game_messages_ = parseStringVector(group[1]);
			} else if (group[0].equals(FIGHT_MESSAGES)) {
				fight_messages_ = parseStringVector(group[1]);
			} else if (group[0].equals(DEALER_COPS_KILLED)) {
				dealer_cops_killed_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_DEPUTIES_KILLED)) {
				dealer_deputies_killed_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_SUCCESSFUL_RUNS)) {
				dealer_successful_runs_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(DEALER_RUN_ATTEMPTS)) {
				dealer_run_attempts_ = Integer.parseInt(group[1]);
			} else if (group[0].equals(GAME_DO_INITIALIZE)) {
				do_initial_setup_ = Integer.parseInt(group[1]);
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	// Serializes all the information stored in this game information object in a very specialized
	// and simple format.
	// TODO: consider xml or something else standard
	public String getCurrentGameInformation() {
		String serialized_game_info = "";
		serialized_game_info +=
			DEALER_CASH + DELIMITER_2 + Integer.toString(dealer_cash_) + DELIMITER_1 +
			DEALER_SPACE + DELIMITER_2 + Integer.toString(dealer_space_) + DELIMITER_1 +
			DEALER_MAX_SPACE + DELIMITER_2 + Integer.toString(dealer_max_space_) + DELIMITER_1 +
			DEALER_LOAN + DELIMITER_2 + Integer.toString(dealer_loan_) + DELIMITER_1 +
			DEALER_BANK + DELIMITER_2 + Integer.toString(dealer_bank_) + DELIMITER_1 +
			DEALER_LOCATION + DELIMITER_2 + Integer.toString(location_) + DELIMITER_1 +
			DEALER_DAYS_LEFT + DELIMITER_2 + Integer.toString(game_days_left_) + DELIMITER_1 +
			DEALER_HEALTH + DELIMITER_2 + Integer.toString(dealer_health_) + DELIMITER_1 +
			DEALER_COPS + DELIMITER_2 + Integer.toString(location_cops_) + DELIMITER_1 +
			DEALER_INVENTORY + DELIMITER_2 + serializeStringToIntMap(dealer_drugs_) +
			DELIMITER_1 +
			DEALER_GUNS + DELIMITER_2 + serializeStringToIntMap(dealer_guns_) + DELIMITER_1 +
			LOCATION_DRUGS + DELIMITER_2 + serializeStringToIntMap(location_drugs_) +
			DELIMITER_1 +
			LOCATION_COATS + DELIMITER_2 + serializeStringToIntMap(location_coats_) +
			DELIMITER_1 +
			LOCATION_GUNS + DELIMITER_2 + serializeStringToIntMap(location_guns_) +
			DELIMITER_1 +
			LOCATION_MESSAGES + DELIMITER_2 + serializeStringVector(game_messages_) + DELIMITER_1 +
			FIGHT_MESSAGES + DELIMITER_2 + serializeStringVector(fight_messages_) + DELIMITER_1 +
			DEALER_COPS_KILLED + DELIMITER_2 + Integer.toString(dealer_cops_killed_) +
			DELIMITER_1 +
			DEALER_DEPUTIES_KILLED + DELIMITER_2 + Integer.toString(dealer_deputies_killed_) +
			DELIMITER_1 +
			DEALER_SUCCESSFUL_RUNS + DELIMITER_2 + Integer.toString(dealer_successful_runs_) +
			DELIMITER_1 +
			DEALER_RUN_ATTEMPTS + DELIMITER_2 + Integer.toString(dealer_run_attempts_) +
			DELIMITER_1 +
			GAME_DO_INITIALIZE + DELIMITER_2 + Integer.toString(do_initial_setup_);
		return serialized_game_info;
	}
	
	// Initialize the game info with a serialized version.
	public GameInformation(String serialized_game_info) {
		setCurrentGameInformation("");
		
		drugs_ = new HashMap<String, HashMap<String, Float>>();
		coats_ = new HashMap<String, HashMap<String, Float>>();
		guns_ = new HashMap<String, HashMap<String, Float>>();
		locations_ = new HashMap<String, HashMap<String, Float>>();
		String[] string_groups = serialized_game_info.split(DELIMITER_1);
		for (int i = 0; i < string_groups.length; ++i) {
			String[] group = string_groups[i].split(DELIMITER_2, 2);
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals(GAME_ID)) {
				game_id_ = group[1];
			} else if (group[0].equals(GAME_DRUGS)) {
				drugs_ = parseStringToStringToFloatMap(group[1]);
			} else if (group[0].equals(GAME_COATS)) {
				coats_ = parseStringToStringToFloatMap(group[1]);
			} else if (group[0].equals(GAME_GUNS)) {
				guns_ = parseStringToStringToFloatMap(group[1]);
			} else if (group[0].equals(GAME_LOCATIONS)) {
				locations_ = parseStringToStringToFloatMap(group[1]);
			} else if (group[0].equals(GAME_LOAN_INTEREST_RATE)) {
				loan_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals(GAME_BANK_INTEREST_RATE)) {
				bank_interest_rate_ = Float.parseFloat(group[1]);
			} else if (group[0].equals(GAME_COAT_LIKELIHOOD)) {
				coat_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals(GAME_GUN_LIKELIHOOD)) {
				gun_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals(GAME_COPS_LIKELIHOOD)) {
				cops_likelihood_ = Float.parseFloat(group[1]);
			} else if (group[0].equals(GAME_STARTING_GAME_INFO)) {
				serialized_starting_game_info_ = group[1];
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	// Serializes all the information stored in this game information object in a very specialized
	// and simple format.
	// TODO: consider xml or something else standard
	public String serializeGameInformation() {
		String serialized_game_info = "";
		serialized_game_info += GAME_ID + DELIMITER_2 + game_id_ + DELIMITER_1 +
			GAME_DRUGS + DELIMITER_2 + serializeStringToStringToFloatMap(drugs_) + DELIMITER_1 +
			GAME_COATS + DELIMITER_2 + serializeStringToStringToFloatMap(coats_) + DELIMITER_1 +
			GAME_GUNS + DELIMITER_2 + serializeStringToStringToFloatMap(guns_) + DELIMITER_1 +
			GAME_LOCATIONS + DELIMITER_2 + serializeStringToStringToFloatMap(locations_) + DELIMITER_1 +
			GAME_LOAN_INTEREST_RATE + DELIMITER_2 + Float.toString(loan_interest_rate_) + DELIMITER_1 +
			GAME_BANK_INTEREST_RATE + DELIMITER_2 + Float.toString(bank_interest_rate_) + DELIMITER_1 +
			GAME_COAT_LIKELIHOOD + DELIMITER_2 + Float.toString(coat_likelihood_) + DELIMITER_1 +
			GAME_GUN_LIKELIHOOD + DELIMITER_2 + Float.toString(gun_likelihood_) + DELIMITER_1 +
			GAME_COPS_LIKELIHOOD + DELIMITER_2 + Float.toString(cops_likelihood_) + DELIMITER_1 +
			GAME_STARTING_GAME_INFO + DELIMITER_2 + serialized_starting_game_info_;
		return serialized_game_info;
	}

    // An artificial default game string, useful if starting with no net access and for defaulting
	// things to playable defaults.
	// TODO: this more or less works but isn't a good playable default, i'm not going to waste time
	//       polishing it until it makes sense to have a good playable default
	public static String getDefaultGameString() {
		return GAME_ID + DELIMITER_2 + "default" + DELIMITER_1 +
			GAME_DRUGS + DELIMITER_2 +
		    	"Weed" + DELIMITER_3a +
		        	"base_price" + DELIMITER_5 + "400" + DELIMITER_4 + 
		            "price_variance" + DELIMITER_5 + "200" + DELIMITER_4 +
		            "icon" + DELIMITER_5 + "0" + DELIMITER_4 +
		            "low_probability" + DELIMITER_5 + "0.1" + DELIMITER_4 +
		            "low_multiplier" + DELIMITER_5 + "0.5" + DELIMITER_3 +
		         "Acid" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "1500" + DELIMITER_4 +
		            "price_variance" + DELIMITER_5 + "400" + DELIMITER_4 +
		            "icon" + DELIMITER_5 + "1__" +
		         "Ludes" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "80" + DELIMITER_4 +
		            "price_variance" + DELIMITER_5 + "20" + DELIMITER_4 +
		            "icon" + DELIMITER_5 + "2__" +
		         "Heroin" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "10000" + DELIMITER_4 +
		            "price_variance" + DELIMITER_5 + "2000" + DELIMITER_4 +
		            "icon" + DELIMITER_5 + "3%%" +
		            "high_probability" + DELIMITER_5 + "0.1" + DELIMITER_4 +
		            "high_multiplier" + DELIMITER_5 + "2.0__" +
		         "Cocaine" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "20000%%" +
		            "price_variance" + DELIMITER_5 + "3000%%" +
		            "icon" + DELIMITER_5 + "4%%" + 
		            "high_probability" + DELIMITER_5 + "0.1%%" +
		            "high_multiplier" + DELIMITER_5 + "2.0__" +
		         "Shrooms" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "1000%%" +
		            "price_variance" + DELIMITER_5 + "200%%" +
		            "icon" + DELIMITER_5 + "5__" +
		         "Speed" + DELIMITER_3a +
		            "base_price" + DELIMITER_5 + "110%%" +
		            "price_variance" + DELIMITER_5 + "30%%" +
		            "icon" + DELIMITER_5 + "6%%" +
		            "low_probability" + DELIMITER_5 + "0.1%%" +
		            "low_multiplier" + DELIMITER_5 + "0.5__" +
			     "Speed2==" +
			        "base_price::110%%" +
			        "price_variance::30%%" +
			        "icon::6%%" +
			        "low_probability::0.1%%" +
			        "low_multiplier::0.5__" +
			     "Speed3==" +
			       "base_price::110%%" +
			       "price_variance::30%%" +
			       "icon::6%%" +
			       "low_probability::0.1%%" +
			       "low_multiplier::0.5__" +
			     "Speed4==" +
			       "base_price::110%%" +
			       "price_variance::30%%" +
			       "icon::6%%" +
			       "low_probability::0.1%%" +
			       "low_multiplier::0.5__" +
			     "Speed5==" +
			       "base_price::110%%" +
			       "price_variance::30%%" +
			       "icon::6%%" +
			       "low_probability::0.1%%" +
			       "low_multiplier::0.5__" +
			     "Speed6==" +
			       "base_price::110%%" +
			       "price_variance::30%%" +
			       "icon::6%%" +
			       "low_probability::0.1%%" +
			       "low_multiplier::0.5__" +
		         "Hashish==" +
		           "base_price::180%%" +
		           "price_variance::40%%" +
		           "icon::7%%" +
		           "low_probability::0.1%%" +
		           "low_multiplier::0.5&&" +
		       "coats--" +
		         "Gucci==" +
		           "additional_space::10%%" +
		           "base_price::2000%%" +
		           "price_variance::200%%" +
		           "space_factor::0.2__" +
		         "D&G==" +
		           "additional_space::20%%" +
		           "base_price::4000%%" +
		           "price_variance::400%%" +
		           "space_factor::0.4__&&" +
		       "guns--" +
		         "Baretta==" +
		           "firepower::6%%" +
		           "base_price::500%%" +
		           "price_variance::100%%" +
		           "space::5__" +
		         "Saturday Night Special==" +
		           "firepower::8%%" +
		           "base_price::1000%%" +
		           "price_variance::200%%" +
		           "space::8__&&" +
		       "locations--" +
		         "Brooklyn==" +
		           "base_drugs::13%%" +
		           "drug_variance::1%%" +
		           "map_x::105%%" +
		           "map_y::220%%" +
		           "has_bank::1%%" +
		           "has_loan_shark::1__" +
		         "Manhattan==" +
		           "base_drugs::8%%" +
		           "drug_variance::2%%" +
		           "map_x::80%%" +
		           "map_y::5__" +
		         "Central Park==" +
		           "base_drugs::8%%" +
		           "drug_variance::2%%" +
		           "map_x::73%%" +
		           "map_y::100__" +
		         "The Ghetto==" +
		           "base_drugs::8%%" +
		           "drug_variance::2%%" +
		           "map_x::80%%" +
		           "map_y::335__" +
		         "The Bronx==" +
		           "base_drugs::8%%" +
		           "drug_variance::2%%" +
		           "map_x::75%%" +
		           "map_y::143__" +
		         "Coney Island==" +
		           "base_drugs::8%%" +
		           "drug_variance::2%%" +
		           "map_x::103%%" +
		           "map_y::60__&&" +
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
	
	// Convenience method to parse a string to integer hash map.
	private HashMap<String, Integer> parseStringToIntMap(String attribute_string) {
		HashMap<String, Integer> attributes = new HashMap<String, Integer>();
		String[] attribute_strings = attribute_string.split(DELIMITER_4);
		for (int j = 0; j < attribute_strings.length; ++j) {
			String[] next_attribute = attribute_strings[j].split(DELIMITER_5);
			if (next_attribute.length != 2) {
				Log.d("dopewars", "Invalid attribute: " + attribute_strings[j]);
			}
			attributes.put(next_attribute[0], Integer.parseInt(next_attribute[1]));
		}
		return attributes;
	}
	
	// Convenience method to parse a string to float hash map.
	public HashMap<String, Float> parseStringToFloatMap(String attribute_string) {
		HashMap<String, Float> attributes = new HashMap<String, Float>();
		String[] attribute_strings = attribute_string.split(DELIMITER_4);
		for (int j = 0; j < attribute_strings.length; ++j) {
			String[] next_attribute = attribute_strings[j].split(DELIMITER_5);
			if (next_attribute.length != 2) {
				Log.d("dopewars", "Invalid attribute: " + attribute_strings[j]);
			}
			attributes.put(next_attribute[0], Float.parseFloat(next_attribute[1]));
		}
		return attributes;
	}
	
	// Convenience method to parse a multi-level map of strings to strings to floats.
	public HashMap<String, HashMap<String, Float>> parseStringToStringToFloatMap(
			String attribute_group_string) {
		HashMap<String, HashMap<String, Float>> new_attributes =
			new HashMap<String, HashMap<String, Float>>();
		String[] elements = attribute_group_string.split(DELIMITER_3);
		for (int i = 0; i < elements.length; ++i) {
			
			// TODO: i did these out of order and then should be re-numbered
			String[] next_element = elements[i].split(DELIMITER_3a);
			if (next_element.length != 2) {
				Log.d("dopewars", "Invalid element description: " + elements[i]);
			} else {
				new_attributes.put(next_element[0], parseStringToFloatMap(next_element[1]));
			}
		}
		return new_attributes;
	}
	
	// Convenience method to serialize a string to integer hash map.
	private String serializeStringToIntMap(HashMap<String, Integer> attribute_map) {
		String serialized_attributes = "";
		Iterator<String> names = attribute_map.keySet().iterator();
		while (names.hasNext()) {
			String next_attribute = names.next();
			serialized_attributes += next_attribute + DELIMITER_5 +
					Integer.toString(attribute_map.get(next_attribute));
			if (names.hasNext()) {
				serialized_attributes += DELIMITER_4;
			}
		}
		return serialized_attributes;
	}
	
	// Convenience method to serialize a string to float hash map.
	public String serializeStringToFloatMap(HashMap<String, Float> attributes) {
		String serialized_attributes = "";
		Iterator<String> names = attributes.keySet().iterator();
		while (names.hasNext()) {
			String next_attribute = names.next();
			serialized_attributes += next_attribute + DELIMITER_5 + Float.toString(attributes.get(next_attribute));
			if (names.hasNext()) {
				serialized_attributes += DELIMITER_4;
			}
		}
		return serialized_attributes;
	}
	
	// Uses the save format as above to serialize a hash_map of hash_maps into a big
	// complicated string.
	public String serializeStringToStringToFloatMap(
			HashMap<String, HashMap<String, Float>> attribute_group) {
		String serialized_attributes = "";
		Iterator<String> elements = attribute_group.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_attributes += next_element + DELIMITER_3a + serializeStringToFloatMap(
					attribute_group.get(next_element));
			if (elements.hasNext()) {
				serialized_attributes += DELIMITER_3;
			}
		}
		return serialized_attributes;
	}
	
	// Convenience method to parse a string vector.
	private Vector<String> parseStringVector(String attribute_string) {
		Vector<String> strings = new Vector<String>();
		String[] attribute_strings = attribute_string.split(DELIMITER_4);
		for (int j = 0; j < attribute_strings.length; ++j) {
			strings.add(attribute_strings[j]);
		}
		return strings;
	}
	
	// Convenience method to serialize a string vector.
	private String serializeStringVector(Vector<String> strings) {
		String serialized_strings = "";
		for (int j = 0; j < strings.size(); ++j) {
			serialized_strings += strings.elementAt(j);
			if ((j + 1) < strings.size()) {
				serialized_strings += DELIMITER_4;
			}
		}
		return serialized_strings;
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
	
	// Current game information, this information is mutable within a game.
	public int dealer_cash_;
	public int dealer_space_;
	public int dealer_max_space_;
	public int dealer_loan_;
	public int dealer_bank_;
	public int location_;
	public int game_days_left_;
	public int dealer_health_;
	public int location_cops_;
	public HashMap<String, Integer> dealer_drugs_;
	public HashMap<String, Integer> dealer_guns_;
	public HashMap<String, Integer> location_drugs_;
	public HashMap<String, Integer> location_coats_;
	public HashMap<String, Integer> location_guns_;
	public Vector<String> game_messages_;
	public Vector<String> fight_messages_;
	
	// Statistics information for display at the end of the game.
	public int dealer_cops_killed_;
	public int dealer_deputies_killed_;
	public int dealer_successful_runs_;
	public int dealer_run_attempts_;
	
	public int do_initial_setup_;
	
	public String serialized_starting_game_info_;
}
