/**
 * This file, along with the CurrentGameInformation class, mostly facilitates the serialization of
 * game settings for easy storage and retrieval from the database as a string. This has proved to
 * be significantly easier and more efficient (unproven) than making many small database
 * transactions in each function every time information about the game is needed.
 * 
 * TODO: it'd be nice if these were protobufs but I don't think there's a nice Eclipse plugin for
 *       that yet.
 */

package com.daverin.dopewars;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

public class GameInformation {

	// Ths strings used in serializing the game information.
	public static String ADDITIONAL_SPACE = "additional_space";
	public static String AMOUNT_DRUGS_BOUGHT = "amount_drugs_bought";
	public static String AMOUNT_DRUGS_SOLD = "amount_drugs_sold";
	public static String BANK = "bank";
	public static String BANK_INTEREST = "bank_interest";
	public static String BANK_INTEREST_RATE = "bank_interest_rate";
	public static String BASE_DRUGS = "base_drugs";
	public static String BASE_PRICE = "base_price";
	public static String CASH = "cash";
	public static String COAT = "coat";
	public static String COAT_LIKELIHOOD = "coat_likelihood";
	public static String COATS_ADDED_SIZE = "coats_added_size";
	public static String COATS_BOUGHT = "coats_bought";
	public static String COPS = "cops_health";
	public static String COPS_KILLED = "cops_killed";
	public static String COPS_LIKELIHOOD = "cops_likelihood";
	public static String CUSTOM_MESSAGE = "message";
	public static String DAYS_LEFT = "days_left";
	public static String DAYS_TO_PAY_OFF_LOAN = "days_to_pay_off_loan";
	public static String DEALER_GUNS = "dealer_guns";
	public static String DEALER_LOCATION = "dealer_location";
	public static String DEPUTIES_KILLED = "deputies_killed";
	public static String DO_INITIALIZE = "do_initial_setup";
	public static String DRUG = "drug";
	public static String DRUG_VARIANCE = "drug_variance";
	public static String FIGHT_MESSAGES = "fight_messages";
	public static String GAME_ID = "game_id";
	public static String GUN = "gun";
	public static String GUN_LIKELIHOOD = "gun_likelihood";
	public static String GUNS_BOUGHT = "guns_bought";
	public static String HAS_BANK = "has_bank";
	public static String HAS_LOAN_SHARK = "has_loan_shark";
	public static String HEALTH = "dealer_health";
	public static String HIGH_MULTIPLIER = "high_multiplier";
	public static String HIGH_PROBABILITY = "high_probability";
	public static String ICON = "icon";
	public static String INVENTORY = "dealer_inventory";
	public static String LOAN = "loan";
	public static String LOAN_INTEREST = "loan_interest";
	public static String LOAN_INTEREST_RATE = "loan_interest_rate";
	public static String LOCATION = "location";
	public static String LOCATION_COATS = "coat_inventory";
	public static String LOCATION_DRUGS = "location_inventory";
	public static String LOCATION_GUNS = "gun_inventory";
	public static String LOCATION_MESSAGES = "messages";
	public static String LOW_MULTIPLIER = "low_multiplier";
	public static String LOW_PROBABILITY = "low_probability";
	public static String MAP_X = "map_x";
	public static String MAP_Y = "map_y";
	public static String MAX_FIREPOWER = "max_dealer_firepower";
	public static String MAX_SPACE = "max_space";
	public static String MONEY_INVESTED_IN_BANK = "money_invested";
	public static String MONEY_PAID_TO_LOAN_SHARK = "money_paid_to_loan_shark";
	public static String MONEY_SPENT_ON_COATS = "money_spent_on_coats";
	public static String MONEY_SPENT_ON_GUNS = "money_spent_on_guns";
	public static String NAME = "name";
	public static String NUM_DRUGS_BOUGHT = "num_drugs_bought";
	public static String NUM_DRUGS_SOLD = "num_drugs_sold";
	public static String PRICE_VARIANCE = "price_variance";
	public static String RUN_ATTEMPTS = "run_attempts";
	public static String SPACE = "space";
	public static String SUCCESSFUL_RUNS = "times_ran";
	public static String TOTAL_DAYS = "total_days";
	
	// Inner class representing a single drug.
	public class Drug {
		public String name_ = "";
		public int base_price_;
		public int icon_;
		public double high_multiplier_;
		public double high_probability_;
		public double low_multiplier_;
		public double low_probability_;
		public String custom_message_ = "";
		public int price_variance_;

		public Drug(String input_string) {
			parseDrug(input_string);
		}
		
		public void parseDrug(String input_string) {
			StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input_string));
			tokenizer.wordChars('_', '_');
			tokenizer.wordChars(':', ':');
			tokenizer.wordChars('&', '&');
			tokenizer.eolIsSignificant(false);
			try {
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					String[] next_attribute = tokenizer.sval.split(":", 2);
					if (next_attribute.length != 2) {
						Log.d("dopewars", "Invalid attribute: " + tokenizer.sval);
					} else if (next_attribute[0].equals(NAME)) {
						name_ = next_attribute[1];
					} else if (next_attribute[0].equals(BASE_PRICE)) {
						base_price_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(CUSTOM_MESSAGE)) {
						custom_message_ = next_attribute[1];
					} else if (next_attribute[0].equals(ICON)) {
						icon_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(HIGH_MULTIPLIER)) {
						high_multiplier_ = Double.parseDouble(next_attribute[1]);
					} else if (next_attribute[0].equals(HIGH_PROBABILITY)) {
						high_probability_ = Double.parseDouble(next_attribute[1]);
					} else if (next_attribute[0].equals(LOW_MULTIPLIER)) {
						low_multiplier_ = Double.parseDouble(next_attribute[1]);
					} else if (next_attribute[0].equals(LOW_PROBABILITY)) {
						low_probability_ = Double.parseDouble(next_attribute[1]);
					} else if (next_attribute[0].equals(PRICE_VARIANCE)) {
						price_variance_ = Integer.parseInt(next_attribute[1]);
					} else {
						Log.d("dopewars", "Valid but unrecognized attribute: " + tokenizer.sval);
					}
				}
			} catch (IOException e) {
				Log.d("dopewars", "Exception reading input string: " + input_string);
			}
		}
		
		public String serializeDrug() {
			return stringParam(NAME, name_) + integerParam(BASE_PRICE, base_price_) +
				stringParam(CUSTOM_MESSAGE, custom_message_) +
				integerParam(ICON, icon_) + doubleParam(HIGH_MULTIPLIER, high_multiplier_) +
				doubleParam(HIGH_MULTIPLIER, high_multiplier_) +
				doubleParam(HIGH_PROBABILITY, high_probability_) +
				doubleParam(LOW_MULTIPLIER, low_multiplier_) +
				doubleParam(LOW_PROBABILITY, low_probability_) +
				integerParam(PRICE_VARIANCE, price_variance_);
		}
	}
	
	// Inner class representing a single coat.
	public class Coat {
		public String name_ = "";
		public int base_price_;
		public int price_variance_;
		public int additional_space_;

		public Coat(String input_string) {
			parseCoat(input_string);
		}
		
		public void parseCoat(String input_string) {
			StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input_string));
			tokenizer.wordChars('_', '_');
			tokenizer.wordChars(':', ':');
			tokenizer.wordChars('&', '&');
			tokenizer.eolIsSignificant(false);
			try {
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					String[] next_attribute = tokenizer.sval.split(":", 2);
					if (next_attribute.length != 2) {
						Log.d("dopewars", "Invalid attribute: " + tokenizer.sval);
					} else if (next_attribute[0].equals(NAME)) {
						name_ = next_attribute[1];
					} else if (next_attribute[0].equals(BASE_PRICE)) {
						base_price_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(ADDITIONAL_SPACE)) {
						additional_space_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(PRICE_VARIANCE)) {
						price_variance_ = Integer.parseInt(next_attribute[1]);
					} else {
						Log.d("dopewars", "Valid but unrecognized attribute: " + tokenizer.sval);
					}
				}
			} catch (IOException e) {
				Log.d("dopewars", "Exception reading input string: " + input_string);
			}
		}
		
		public String serializeCoat() {
			return stringParam(NAME, name_) + integerParam(BASE_PRICE, base_price_) +
				integerParam(ADDITIONAL_SPACE, additional_space_) +
				integerParam(PRICE_VARIANCE, price_variance_);
		}
	}

	// Inner class representing a single gun.
	public class Gun {
		public String name_ = "";
		public int base_price_;
		public int price_variance_;
		public int space_;
		public int firepower_;

		public Gun(String input_string) {
			parseGun(input_string);
		}
		
		public void parseGun(String input_string) {
			StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input_string));
			tokenizer.wordChars('_', '_');
			tokenizer.wordChars(':', ':');
			tokenizer.wordChars('&', '&');
			tokenizer.eolIsSignificant(false);
			try {
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					String[] next_attribute = tokenizer.sval.split(":", 2);
					if (next_attribute.length != 2) {
						Log.d("dopewars", "Invalid attribute: " + tokenizer.sval);
					} else if (next_attribute[0].equals(NAME)) {
						name_ = next_attribute[1];
					} else if (next_attribute[0].equals(BASE_PRICE)) {
						base_price_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(SPACE)) {
						space_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(PRICE_VARIANCE)) {
						price_variance_ = Integer.parseInt(next_attribute[1]);
					} else {
						Log.d("dopewars", "Valid but unrecognized attribute: " +
								tokenizer.sval);
					}
				}
			} catch (IOException e) {
				Log.d("dopewars", "Exception reading input string: " + input_string);
			}
		}
		
		public String serializeGun() {
			return stringParam(NAME, name_) + integerParam(BASE_PRICE, base_price_) +
				integerParam(SPACE, space_) + integerParam(PRICE_VARIANCE, price_variance_);
		}	
	}
	
	// Inner class representing a single location.
	public class Location {
		public String name_ = "";
		public boolean has_bank_;
		public int base_drugs_;
		public int drug_variance_;
		public boolean has_loan_shark_;
		public int map_x_;
		public int map_y_;
		
		public Location(String input_string) {
			parseLocation(input_string);
		}
		
		public void parseLocation(String input_string) {
			StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input_string));
			tokenizer.wordChars('_', '_');
			tokenizer.wordChars(':', ':');
			tokenizer.wordChars('&', '&');
			tokenizer.eolIsSignificant(false);
			try {
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					String[] next_attribute = tokenizer.sval.split(":", 2);
					if (next_attribute.length != 2) {
						Log.d("dopewars", "Invalid attribute: " + tokenizer.sval);
					} else if (next_attribute[0].equals(NAME)) {
						name_ = next_attribute[1];
					} else if (next_attribute[0].equals(HAS_BANK)) {
						has_bank_ = Boolean.parseBoolean(next_attribute[1]);
					} else if (next_attribute[0].equals(BASE_DRUGS)) {
						base_drugs_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(DRUG_VARIANCE)) {
						drug_variance_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(HAS_LOAN_SHARK)) {
						has_loan_shark_ = Boolean.parseBoolean(next_attribute[1]);
					} else if (next_attribute[0].equals(MAP_X)) {
						map_x_ = Integer.parseInt(next_attribute[1]);
					} else if (next_attribute[0].equals(MAP_Y)) {
						map_y_ = Integer.parseInt(next_attribute[1]);
					} else {
						Log.d("dopewars", "Valid but unrecognized attribute: " +
								tokenizer.sval);
					}
				}
			} catch (IOException e) {
				Log.d("dopewars", "Exception reading input string: " + input_string);
			}
		}
		
		public String serializeLocation() {
			return stringParam(NAME, name_) + booleanParam(HAS_BANK, has_bank_) +
				integerParam(BASE_DRUGS, base_drugs_) +
				integerParam(DRUG_VARIANCE, drug_variance_) +
				booleanParam(HAS_LOAN_SHARK, has_loan_shark_) +
				integerParam(MAP_X, map_x_) + integerParam(MAP_Y, map_y_);
		}	
	}
	
	// Initialize the game info with a serialized version.
	public GameInformation(String serialized_game_info) {
		parseGameInformation(serialized_game_info);
	}
	
	// Take a serialized version of the game information and populate this object with its
	// contents.
	public void parseGameInformation(String serialized_game_information) {
		drugs_ = new HashMap<String, Drug>();
		coats_ = new HashMap<String, Coat>();
		guns_ = new HashMap<String, Gun>();
		locations_ = new HashMap<String, Location>();
		dealer_drugs_ = new HashMap<String, Integer>();
		dealer_guns_ = new HashMap<String, Integer>();
		location_drugs_ = new HashMap<String, Integer>();
		location_coats_ = new HashMap<String, Integer>();
		location_guns_ = new HashMap<String, Integer>();
		game_messages_ = new Vector<String>();
		fight_messages_ = new Vector<String>();
		num_drugs_bought_ = new HashMap<String, Integer>();
		amount_drugs_bought_ = new HashMap<String, Integer>();
		num_drugs_sold_ = new HashMap<String, Integer>();
		amount_drugs_sold_ = new HashMap<String, Integer>();
		
		StreamTokenizer tokenizer =
			new StreamTokenizer(new StringReader(serialized_game_information));
		tokenizer.quoteChar('#');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars(':', ':');
		tokenizer.wordChars('&', '&');
		tokenizer.eolIsSignificant(false);
		try {
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				String[] group = tokenizer.sval.split(":", 2);
				if (group.length != 2) {
					Log.d("dopewars", "Invalid game info group: " + tokenizer.sval);
				} else if (group[0].equals(GAME_ID)) {
					game_id_ = group[1];
				} else if (group[0].equals(DRUG)) {
					Drug d = new Drug(group[1]);
					drugs_.put(d.name_, d);
				} else if (group[0].equals(COAT)) {
					Coat c = new Coat(group[1]);
					coats_.put(c.name_, c);
				} else if (group[0].equals(GUN)) {
					Gun g = new Gun(group[1]);
					guns_.put(g.name_, g);
				} else if (group[0].equals(LOCATION)) {
					Location l = new Location(group[1]);
					locations_.put(l.name_, l);
				} else if (group[0].equals(LOAN_INTEREST_RATE)) {
					loan_interest_rate_ = Double.parseDouble(group[1]);
				} else if (group[0].equals(BANK_INTEREST_RATE)) {
					bank_interest_rate_ = Double.parseDouble(group[1]);
				} else if (group[0].equals(COAT_LIKELIHOOD)) {
					coat_likelihood_ = Double.parseDouble(group[1]);
				} else if (group[0].equals(GUN_LIKELIHOOD)) {
					gun_likelihood_ = Double.parseDouble(group[1]);
				} else if (group[0].equals(COPS_LIKELIHOOD)) {
					cops_likelihood_ = Double.parseDouble(group[1]);
				} else if (group[0].equals(CASH)) {
					dealer_cash_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(SPACE)) {
					dealer_space_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(MAX_SPACE)) {
					dealer_max_space_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(LOAN)) {
					dealer_loan_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(BANK)) {
					dealer_bank_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(DEALER_LOCATION)) {
					location_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(DAYS_LEFT)) {
					game_days_left_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(TOTAL_DAYS)) {
					total_days_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(HEALTH)) {
					dealer_health_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(COPS)) {
					location_cops_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(INVENTORY)) {
					dealer_drugs_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(DEALER_GUNS)) {
					dealer_guns_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(LOCATION_DRUGS)) {
					location_drugs_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(LOCATION_COATS)) {
					location_coats_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(LOCATION_GUNS)) {
					location_guns_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(LOCATION_MESSAGES)) {
					game_messages_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(FIGHT_MESSAGES)) {
					fight_messages_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(COPS_KILLED)) {
					dealer_cops_killed_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(DEPUTIES_KILLED)) {
					dealer_deputies_killed_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(SUCCESSFUL_RUNS)) {
					dealer_successful_runs_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(RUN_ATTEMPTS)) {
					dealer_run_attempts_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(COATS_BOUGHT)) {
					coats_bought_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(COATS_ADDED_SIZE)) {
					coats_added_size_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(MONEY_SPENT_ON_COATS)) {
					money_spent_on_coat_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(GUNS_BOUGHT)) {
					guns_bought_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(MONEY_SPENT_ON_GUNS)) {
					money_spent_on_guns_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(MAX_FIREPOWER)) {
					max_dealer_firepower_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(NUM_DRUGS_BOUGHT)) {
					num_drugs_bought_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(AMOUNT_DRUGS_BOUGHT)) {
					amount_drugs_bought_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(NUM_DRUGS_SOLD)) {
					num_drugs_sold_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(AMOUNT_DRUGS_SOLD)) {
					amount_drugs_sold_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(MONEY_PAID_TO_LOAN_SHARK)) {
					money_paid_to_loan_shark_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(LOAN_INTEREST)) {
					loan_interest_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(DAYS_TO_PAY_OFF_LOAN)) {
					days_to_pay_off_loan_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(MONEY_INVESTED_IN_BANK)) {
					money_invested_in_bank_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(BANK_INTEREST)) {
					bank_interest_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(DO_INITIALIZE)) {
					do_initial_setup_ = Integer.parseInt(group[1]);
				} else {
					Log.d("dopewars", "Unknown game info group");
				}
			}
		} catch (IOException e) {
			Log.d("dopewars", "IOException parsing game information.");
		}
	}
	
	// Serializes all the information stored in this game information object in a very specialized
	// and simple format.
	// TODO: consider xml or something else standard
	public String serializeGameInformation() {
		String serialized_game = stringParam(GAME_ID, game_id_) +
			doubleParam(LOAN_INTEREST_RATE, loan_interest_rate_) +
			doubleParam(BANK_INTEREST_RATE, bank_interest_rate_) +
			doubleParam(COAT_LIKELIHOOD, coat_likelihood_) +
			doubleParam(GUN_LIKELIHOOD, gun_likelihood_) +
			doubleParam(COPS_LIKELIHOOD, cops_likelihood_);
		
		Iterator<String> elements = drugs_.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_game += "#" + DRUG + ":" + drugs_.get(next_element).serializeDrug() + "# ";
		}
		elements = coats_.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_game += "#" + COAT + ":" + coats_.get(next_element).serializeCoat() + "# ";
		}
		elements = guns_.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_game += "#" + GUN + ":" + guns_.get(next_element).serializeGun() + "# ";
		}
		elements = locations_.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_game += "#" + LOCATION + ":" + 
					locations_.get(next_element).serializeLocation() + "# ";
		}
		
		serialized_game += integerParam(CASH, dealer_cash_) +
			integerParam(SPACE, dealer_space_) + integerParam(MAX_SPACE, dealer_max_space_) +
			integerParam(LOAN, dealer_loan_) + integerParam(BANK, dealer_bank_) +
			integerParam(DEALER_LOCATION, location_) + integerParam(DAYS_LEFT, game_days_left_) +
			integerParam(TOTAL_DAYS, total_days_) +
			integerParam(HEALTH, dealer_health_) + integerParam(COPS, location_cops_) +
			stringToIntParam(INVENTORY, dealer_drugs_) +
			stringToIntParam(DEALER_GUNS, dealer_guns_) +
			stringToIntParam(LOCATION_DRUGS, location_drugs_) +
			stringToIntParam(LOCATION_COATS, location_coats_) +
			stringToIntParam(LOCATION_GUNS, location_guns_) +
			stringVectorParam(LOCATION_MESSAGES, game_messages_) +
			stringVectorParam(FIGHT_MESSAGES, fight_messages_) +
			integerParam(COPS_KILLED, dealer_cops_killed_) +
			integerParam(DEPUTIES_KILLED, dealer_deputies_killed_) +
			integerParam(SUCCESSFUL_RUNS, dealer_successful_runs_) +
			integerParam(RUN_ATTEMPTS, dealer_run_attempts_) +
			integerParam(COATS_BOUGHT, coats_bought_) +
			integerParam(COATS_ADDED_SIZE, coats_added_size_) +
			integerParam(MONEY_SPENT_ON_COATS, money_spent_on_coat_) +
			integerParam(GUNS_BOUGHT, guns_bought_) +
			integerParam(MONEY_SPENT_ON_GUNS, money_spent_on_guns_) +
			integerParam(MAX_FIREPOWER, max_dealer_firepower_) +
			stringToIntParam(NUM_DRUGS_BOUGHT, num_drugs_bought_) +
			stringToIntParam(AMOUNT_DRUGS_BOUGHT, amount_drugs_bought_) +
			stringToIntParam(NUM_DRUGS_SOLD, num_drugs_sold_) +
			stringToIntParam(AMOUNT_DRUGS_SOLD, amount_drugs_sold_) +
			integerParam(MONEY_PAID_TO_LOAN_SHARK, money_paid_to_loan_shark_) +
			integerParam(LOAN_INTEREST, loan_interest_) +
			integerParam(DAYS_TO_PAY_OFF_LOAN, days_to_pay_off_loan_) +
			integerParam(MONEY_INVESTED_IN_BANK, money_invested_in_bank_) +
			integerParam(BANK_INTEREST, bank_interest_) +
			integerParam(DO_INITIALIZE, do_initial_setup_);
		
		return serialized_game;
	}
	
    // Convenience methods for serializing a couple different types that are used a lot.
	
	public String stringParam(String header, String param) {
		return "\"" + header + ":" + param + "\" ";
	}
	public String integerParam(String header, int param) {
		return header + ":" + Integer.toString(param) + " ";
	}
	public String doubleParam(String header, double param) {
		return header + ":" + Double.toString(param) + " ";
	}
	public String booleanParam(String header, boolean param) {
		return header + ":" + Boolean.toString(param) + " ";
	}
	public String stringVectorParam(String header, Vector<String> param) {
		String serializedVector = "#" + header;
		for (int i = 0; i < param.size(); ++i) {
			serializedVector += param.elementAt(i) + " ";
		}
		serializedVector += "# ";
		return serializedVector;
	}
	public String stringToIntParam(String header, HashMap<String, Integer> param) {
		String serializedParam = "#" + header + ":";
		Iterator<String> names = param.keySet().iterator();
		while (names.hasNext()) {
			String next_param = names.next();
			serializedParam += "\"" + next_param + ":" + Integer.toString(param.get(next_param)) + "\" ";
		}
		serializedParam += "# ";
		return serializedParam;
	}
	
	// Convenience methods for de-serializing a coupld different types that are used a lot.
	
	private Vector<String> parseStringVectorParam(String param_string) {
		Vector<String> param = new Vector<String>();
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(param_string));
		tokenizer.quoteChar('"');
		tokenizer.eolIsSignificant(false);
		try {
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				param.add(tokenizer.sval);
			}
		} catch (IOException e) {
			Log.d("dopewars", "Parsing error on a string vector map.");
		}
		return param;
	}
	private HashMap<String, Integer> parseStringToIntParam(String param_string) {
		HashMap<String, Integer> param = new HashMap<String, Integer>();
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(param_string));
		tokenizer.quoteChar('"');
		tokenizer.eolIsSignificant(false);
		try {
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				String[] group = tokenizer.sval.split(":", 2);
				if (group.length != 2) {
					Log.d("dopewars", "Invalid game info group: " + tokenizer.sval);
				} else {
					param.put(group[0], Integer.parseInt(group[1]));
				}
			}
		} catch (IOException e) {
			Log.d("dopewars", "Parsing error on a string to int map.");
		}
		return param;
	}
	
    // An artificial default game string, useful if starting with no net access and for defaulting
	// things to playable defaults.
	// TODO: this more or less works but isn't a good playable default, i'm not going to waste time
	//       polishing it until it makes sense to have a good playable default
	public static String getDefaultGameString() {
		return "game_id:default " +
			"#drug:name:Weed base_price:400 price_variance:200 icon:0 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Acid base_price:1500 price_variance:400 icon:1 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Ludes base_price:80 price_variance:20 icon:2# " +
			"#drug:name:Heroin base_price:10000 price_variance:2000 icon:3 high_probability:0.1 high_multiplier:2.0# " +
			"#drug:name:Cocaine base_price:20000 price_variance:3000 icon:4 high_probability:0.1 high_multiplier:2.0# " +
			"#drug:name:Shrooms base_price:1000 price_variance:1000 icon:5# " +
			"#drug:name:Speed base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Speed2 base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Speed3 base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Speed4 base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Speed5 base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Speed6 base_price:110 price_variance:30 icon:6 low_probability:0.1 low_multiplier:0.5# " +
			"#drug:name:Hashish base_price:180 price_variance:40 icon:7 low_probability:0.1 low_multiplier:0.5# " +
		    "#coat:name:Gucci additional_space:10 base_price:2000 price_variance:200 space_factor:0.2# " +
			"#coat:name:D&G additional_space:20 base_price:4000 price_variance:400 space_factor:0.4# " +
			"#gun:name:Baretta firepower:5 base_price:500 price_variance:100 space:5# " +
			"#gun:\"name:Saturday Night Special\" firepower:8 base_price:1000 price_variance:200 space:8# " +
			"#location:name:Brooklyn base_drugs:13 drug_variance:1 map_x:105 map_y:220 has_bank:true has_loan_shark:true# " +
			"#location:name:Manhattan base_drugs:8 drug_variance:2 map_x:80 map_y:5# " +
			"#location:\"name:Central Park\" base_drugs:8 drug_variance:2 map_x:73 map_y:100# " +
			"#location:\"name:The Bronx\" base_drugs:8 drug_variance:2 map_x:75 map_y:143# " +
			"#location:\"name:The Ghetto\" base_drugs:8 drug_variance:2 map_x:80 map_y:335# " +
			"#location:\"name:Coney Island\" base_drugs:8 drug_variance:2 map_x:103 map_y:60# " +
			"loan_interest_rate:0.05 " +
			"bank_interest_rate:0.10 " +
			"coat_likelihood:0.1 " +
			"gun_likelihood:0.1 " +
			"cops_likelihood:0.1 " +
			"cash:53000 " +
			"loan:5500 " +
			"dealer_location:0 " +
			"space:100 " +
			"max_space:100 " +
			"days_left:10 " +
			"bank:0 " +
			"dealer_health:100 ";
	}
	
	public String game_id_;
	public HashMap<String, Drug> drugs_;
	public HashMap<String, Coat> coats_;
	public HashMap<String, Gun> guns_;
	public HashMap<String, Location> locations_;
	public double loan_interest_rate_;
	public double bank_interest_rate_;
	public double coat_likelihood_;
	public double gun_likelihood_;
	public double cops_likelihood_;
	
	// Current game information, this information is mutable within a game.
	public int dealer_cash_;
	public int dealer_space_;
	public int dealer_max_space_;
	public int dealer_loan_;
	public int dealer_bank_;
	public int location_;
	public int total_days_;
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
	public int coats_bought_;
	public int coats_added_size_;
	public int money_spent_on_coat_;
    public int guns_bought_;
	public int money_spent_on_guns_;
	public int max_dealer_firepower_;
	public HashMap<String, Integer> num_drugs_bought_;
	public HashMap<String, Integer> amount_drugs_bought_;
	public HashMap<String, Integer> num_drugs_sold_;
	public HashMap<String, Integer> amount_drugs_sold_;
	public int money_paid_to_loan_shark_;
	public int loan_interest_;
	public int days_to_pay_off_loan_;
	public int money_invested_in_bank_;
	public int bank_interest_;
	
	public int do_initial_setup_;

}
