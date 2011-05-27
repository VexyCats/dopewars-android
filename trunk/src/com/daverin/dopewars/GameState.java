package com.daverin.dopewars;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

public class GameState {
	private static final String TAG = "Dopewars2GameData";
	public static final String game_data_version = "1";
	
	public class Drug {
		Drug(String drug_name, int base_price, int price_variance,
				double low_price_probability, double low_price_multiplier,
				double high_price_probability, double high_price_multiplier) {
		    drug_name_ = drug_name;
		    base_price_ = base_price;
		    price_variance_ = price_variance;
		    low_price_probability_ = low_price_probability;
		    low_price_multiplier_ = low_price_multiplier;
		    high_price_probability_ = high_price_probability;
		    high_price_multiplier_ = high_price_multiplier;
		}
		
		public int randomPrice() {
			int drug_price = (int)(base_price_ - price_variance_ / 2.0 +
					random_.nextDouble() * price_variance_);
			// Check for price jumps
			if (random_.nextFloat() < low_price_probability_) {
				drug_price = (int)(drug_price * low_price_multiplier_);
				// TODO: add messages
			} else if (random_.nextFloat() < high_price_probability_) {
				drug_price = (int)(drug_price * high_price_multiplier_);
				// TODO: add messages
			}
			return drug_price;
		}
		
        public String drug_name_;
        public int base_price_;
        public int price_variance_;
        public double low_price_probability_;
        public double low_price_multiplier_;
        public double high_price_probability_;
        public double high_price_multiplier_;
	}
	
	public class Location {
		Location(String location_name, int base_drugs, int drug_variance,
				boolean has_bank, boolean has_loan_shark) {
			location_name_ = location_name;
			base_drugs_ = base_drugs;
			drug_variance_ = drug_variance;
			has_bank_ = has_bank;
			has_loan_shark_ = has_loan_shark;
		}
		
		public String location_name_;
		public int base_drugs_;
		public int drug_variance_;
		public boolean has_bank_;
		public boolean has_loan_shark_;
	}
	
	// Static game info, this stuff is set up once and not changed and is not saved
	// with saved games.
	public Vector<Drug> drugs_ = new Vector<Drug>();
	public Vector<Location> locations_ = new Vector<Location>();
	public double loan_interest_rate_;
	public double bank_interest_rate_;
	
	public void SetupStaticGameInfo() {
		drugs_.clear();
		drugs_.add(new Drug("Acid", 2700, 1700, 0.05, 0.4, 0, 0));
		drugs_.add(new Drug("Cocaine", 22000, 7000, 0, 0, 0.05, 4.0));
		drugs_.add(new Drug("Hashish", 880, 400, 0.05, 0.5, 0, 0));
		drugs_.add(new Drug("Heroin", 9250, 3750, 0, 0, 0.05, 2.0));
		drugs_.add(new Drug("Ludes", 35, 25, 0.05, 0.3, 0, 0));
		drugs_.add(new Drug("MDMA", 2950, 1450, 0, 0, 0, 0));
		drugs_.add(new Drug("Opium", 895, 355, 0, 0, 0.05, 3.0));
		drugs_.add(new Drug("PCP", 1750, 750, 0, 0, 0, 0));
		drugs_.add(new Drug("Peyote", 460, 240, 0, 0, 0, 0));
		drugs_.add(new Drug("Shrooms", 965, 335, 0, 0, 0, 0));
		drugs_.add(new Drug("Speed", 170, 80, 0, 0, 0.05, 5.0));
		drugs_.add(new Drug ("Weed", 600, 290, 0.05, 0.2, 0, 0));
		
		locations_.clear();
		locations_.add(new Location("Brooklyn", 6, 2, true, true));
		locations_.add(new Location("The Bronx", 9, 2, false, false));
		locations_.add(new Location("Central Park", 9, 2, false, false));
		locations_.add(new Location("Coney Island", 2, 1, false, false));
		locations_.add(new Location("The Ghetto", 9, 2, false, false));
		locations_.add(new Location("Manhattan", 9, 2, false, false));
		locations_.add(new Location("Queens", 9, 2, false, false));
		locations_.add(new Location("Staten Island", 9, 2, false, false));
		
		loan_interest_rate_ = 0.10;
		bank_interest_rate_ = 0.05;
	}
	
	// Dynamic game info, this stuff is initialized at the start of a game but changes
	// as the game progresses, and is saved as a saved game.
	public int cash_;
	public int bank_;
	public int loan_;
	public int guns_;
	public int health_;
	public int max_space_;
	public int location_;
	public int days_left_;
	public Vector<Integer> dealer_drugs_ = new Vector<Integer>();
	public Vector<Integer> drug_price_ = new Vector<Integer>();
	public int hardass_health_;
	public int hardass_deputies_;
	
	public int game_length;
	public int game_type;
	
	public GameState(String serialized_game_info) {
		SetupStaticGameInfo();

		LoadGame(serialized_game_info);
		
		if (!GameInitialized()) {
			cash_ = 2000;
			bank_ = 0;
			loan_ = 5500;
			location_ = 0;
			max_space_ = 100;
			health_ = 100;
			guns_ = 0;
			days_left_ = 3;
			drug_price_.clear();
			for (int i = 0; i < drugs_.size(); ++i) {
				drug_price_.add(0);
			}
			dealer_drugs_.clear();
			for (int i = 0; i < drugs_.size(); ++i) {
				dealer_drugs_.add(0);
			}
			hardass_health_ = 0;
			hardass_deputies_ = 0;
			
			SetupNewLocation();
		}
	}
	
	// A game is initialized if there are any drugs at the current location.
	public boolean GameInitialized() {
		for (int i = 0; i < drug_price_.size(); ++i) {
			if (drug_price_.elementAt(i) > 0) {
				return true;
			}
		}
		return false;
	}
	
	public void SetupNewDrugs() {
		// First choose candidate prices for all drugs.
		for (int i = 0; i < drug_price_.size(); ++i) {
			drug_price_.setElementAt(drugs_.elementAt(i).randomPrice(), i);
		}
		
		// Next determine how many drugs are available at the current location.
		int base_drugs_count = locations_.elementAt(location_).base_drugs_;
		int drug_variance = locations_.elementAt(location_).drug_variance_;
		int num_drugs_present = base_drugs_count + random_.nextInt(drug_variance + 1);
		if (num_drugs_present < 1) { num_drugs_present = 1; }
		
		// Now determine which of the drugs are available by making a list including all the
		// possible drugs and zeroing the price of random elements until the limit is met.
		for (int i = 0; i < (drug_price_.size() - num_drugs_present); ++i) {
			int starting_location = random_.nextInt(drug_price_.size());
			int zero_location = starting_location;
			while (drug_price_.elementAt(zero_location) <= 0) {
				zero_location += 1;
				zero_location %= drug_price_.size();
				if (zero_location == starting_location) {
					break;
				}
			}
			drug_price_.setElementAt(0, zero_location);
		}
	}
	
	public void SetupHardass() {
		if (hardass_health_ > 0) {
			return;
		}
		if (random_.nextFloat() < 0.1) {
			hardass_health_ = 10;
			hardass_deputies_ = 1 + random_.nextInt(3);
		}
	}
	
	// When moving from one location to another (advancing one turn) this resets all the drugs
	// that are available and processes all the random events that can happen on a turn-by-turn
	// basis.
	public void SetupNewLocation() {
		SetupNewDrugs();
		SetupHardass();
	}
	
	public int NumDrugsAvailable() {
		int num_drugs = 0;
		for (int i = 0; i < drug_price_.size(); ++i) {
			if (drug_price_.elementAt(i) > 0) {
				++num_drugs;
			}
		}
		return num_drugs;
	}
	
	public boolean DealerHasDrugs() {
		for (int i = 0; i < dealer_drugs_.size(); ++i) {
			if (dealer_drugs_.elementAt(i) > 0) {
				return true;
			}
		}
		return false;
	}
	
	public String SerializeGame() {
		String serialized_game = game_data_version + ",";
		serialized_game += Integer.toString(cash_) + ",";
		serialized_game += Integer.toString(bank_) + ",";
		serialized_game += Integer.toString(loan_) + ",";
		serialized_game += Integer.toString(guns_) + ",";
		serialized_game += Integer.toString(health_) + ",";
		serialized_game += Integer.toString(max_space_) + ",";
		serialized_game += Integer.toString(location_) + ",";
		serialized_game += Integer.toString(days_left_) + ",";
		for (int i = 0; i < drug_price_.size(); ++i) {
			serialized_game += Integer.toString(drug_price_.elementAt(i)) + ",";
		}
		// TODO: handle inventory
		serialized_game += Integer.toString(hardass_health_) + ",";
		serialized_game += Integer.toString(hardass_deputies_) + ",";
		return serialized_game;
	}
	
	public void LoadGame(String serialized_game_info) {
		StreamTokenizer tokenizer =
			new StreamTokenizer(new StringReader(serialized_game_info));
		tokenizer.whitespaceChars((int)',', (int)',');
		tokenizer.quoteChar('"');
		tokenizer.eolIsSignificant(false);
		try {
			tokenizer.nextToken();
			if (tokenizer.sval != game_data_version) {
				Log.d(TAG, "Trying to load wrong data version, got " + 
						tokenizer.sval + ", expected " + game_data_version);
			}
			tokenizer.nextToken();
			cash_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			bank_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			loan_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			guns_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			health_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			max_space_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			location_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			days_left_ = (int)tokenizer.nval;
			for (int i = 0; i < drugs_.size(); ++i) {
				tokenizer.nextToken();
				if (drug_price_.size() <= i) {
					drug_price_.add((int)tokenizer.nval);
				} else {
					drug_price_.setElementAt((int)tokenizer.nval, i);
				}
			}
			// TODO: handle inventory
			tokenizer.nextToken();
			hardass_health_ = (int)tokenizer.nval;
			tokenizer.nextToken();
			hardass_deputies_ = (int)tokenizer.nval;
		} catch (IOException e) {
			Log.d(TAG, "Parsing error with value " + tokenizer.sval);
		}
	}
	
	// Random number generator for this activity.
    public static Random random_ = new Random();
/*
	// This is hard-coded information about the game, it will probably get softer over time.
	public static double LOAN_INTEREST_RATE = 0.05;
	public static double BANK_INTEREST_RATE = 0.10;
	public static double COAT_LIKELIHOOD = 0.10;
	public static double GUN_LIKELIHOOD = 0.10;
	public static double COPS_LIKELIHOOD = 0.10;
	
	public static String[] DRUG_NAMES = new String[] {   "Acid", "Cocaine", "Hashish", "Heroin", "Ludes", "MDA", "Opium", "PCP", "Peyote", "Shrooms", "Speed", "Weed" };
	public static int[] DRUG_BASE_PRICE = new int[]  {     2700,     22000,       880,     9250,      35,  2950,     895,  1750,      460,       965,     170,    600 };
	public static int[] DRUG_PRICE_VARIANCE = new int[]  { 1700,      7000,       400,     1875,      25,  1450,     355,   750,      240,       335,      80,    290 };
	public static double[] DRUG_LOW_PROB = new double[]  {  0.2,         0,       0.2,        0,     0.1,     0,       0,     0,        0,         0,       0,    0.1 };
	public static double[] DRUG_LOW_MULT = new double[]  {  0.4,         0,       0.5,        0,     0.3,     0,       0,     0,        0,         0,       0,    0.2 };
	public static double[] DRUG_HIGH_PROB = new double[]  {   0,       0.1,         0,      0.2,       0,     0,     0.1,     0,        0,         0,     0.1,      0};
	public static double[] DRUG_HIGH_MULT = new double[]  {   0,       4.0,         0,      2.0,       0,     0,     3.0,     0,        0,         0,     5.0,      0};
	
	public static String[] COAT_NAMES = new String[] { "Gucci", "D&G" };
	public static int[] COAT_SPACE = new int[]       {      10,    20 };
	public static int[] COAT_BASE_PRICE = new int[]  {    2000,  4000 };
	public static int[] COAT_PRICE_VARIANCE = new int[] {  200,   400 };
	
	public static int getCoatSpace(String coat_name) {
		for (int i = 0; i < COAT_NAMES.length; ++i) {
			if (COAT_NAMES[i] == coat_name) {
				return COAT_SPACE[i];
			}
		}
		return 0;
	}

	public static String[] GUN_NAMES = new String[] { "Baretta", "Sat. Night Special" };
	public static int[] GUN_FIREPOWER = new int[] {           5,                    8 };
	public static int[] GUN_BASE_PRICE = new int[] {        500,                 1000 };
	public static int[] GUN_PRICE_VARIANCE = new int[] {    100,                  200 };
	public static int[] GUN_SPACE = new int[] {               5,                    8 };
	
	public static int getGunFirepower(String gun_name) {
		for (int i = 0; i < GUN_NAMES.length; ++i) {
			if (GUN_NAMES[i] == gun_name) {
				return GUN_FIREPOWER[i];
			}
		}
		return 0;
	}
	
	public static int getGunSpace(String gun_name) {
		for (int i = 0; i < GUN_NAMES.length; ++i) {
			if (GUN_NAMES[i] == gun_name) {
				return GUN_SPACE[i];
			}
		}
		return 0;
	}
	
	public static String[] LOCATION_NAMES = new String[] { "Brooklyn",     "Bronx", "Central Park", "Coney Island", "Ghetto", "Manhattan", "Queens", "Staten Island" };
	public static int[] LOCATION_BASE_DRUGS = new int[] {           6,           9,              9,              2,        9,           9,        9,               9 };
	public static int[] LOCATION_DRUG_VARIANCE = new int[] {        2,           2,              2,              1,        2,           2,        2,               3 };
	public static boolean[] LOCATION_HAS_BANK = new boolean[] {  true,       false,          false,          false,    false,       false,    false,           false };
	public static boolean[] LOCATION_HAS_SHARK = new boolean[] { true,       false,          false,          false,    false,       false,    false,           false };
	
	// Ths strings used in serializing the game information.
	public static String PARAM_ADDITIONAL_SPACE = "additional_space";
	public static String PARAM_AMOUNT_DRUGS_BOUGHT = "amount_drugs_bought";
	public static String PARAM_AMOUNT_DRUGS_SOLD = "amount_drugs_sold";
	public static String PARAM_BANK = "bank";
	public static String PARAM_BANK_INTEREST = "bank_interest";
	public static String PARAM_BANK_INTEREST_RATE = "bank_interest_rate";
	public static String PARAM_BASE_DRUGS = "base_drugs";
	public static String PARAM_BASE_PRICE = "base_price";
	public static String PARAM_CASH = "cash";
	public static String PARAM_COAT = "coat";
	public static String PARAM_COAT_LIKELIHOOD = "coat_likelihood";
	public static String PARAM_COATS_ADDED_SIZE = "coats_added_size";
	public static String PARAM_COATS_BOUGHT = "coats_bought";
	public static String PARAM_COPS = "cops_health";
	public static String PARAM_COPS_KILLED = "cops_killed";
	public static String PARAM_COPS_LIKELIHOOD = "cops_likelihood";
	public static String PARAM_CUSTOM_MESSAGE = "message";
	public static String PARAM_DAYS_LEFT = "days_left";
	public static String PARAM_DAYS_TO_PAY_OFF_LOAN = "days_to_pay_off_loan";
	public static String PARAM_DEALER_GUNS = "dealer_guns";
	public static String PARAM_DEALER_LOCATION = "dealer_location";
	public static String PARAM_DEPUTIES_KILLED = "deputies_killed";
	public static String PARAM_DO_INITIALIZE = "do_initial_setup";
	public static String PARAM_DRUG = "drug";
	public static String PARAM_DRUG_VARIANCE = "drug_variance";
	public static String PARAM_FIGHT_MESSAGES = "fight_messages";
	public static String PARAM_FIREPOWER = "firepower";
	public static String PARAM_GAME_ID = "game_id";
	public static String PARAM_GUN = "gun";
	public static String PARAM_GUN_LIKELIHOOD = "gun_likelihood";
	public static String PARAM_GUNS_BOUGHT = "guns_bought";
	public static String PARAM_HAS_BANK = "has_bank";
	public static String PARAM_HAS_LOAN_SHARK = "has_loan_shark";
	public static String PARAM_HEALTH = "dealer_health";
	public static String PARAM_HIGH_MULTIPLIER = "high_multiplier";
	public static String PARAM_HIGH_PROBABILITY = "high_probability";
	public static String PARAM_ICON = "icon";
	public static String PARAM_INVENTORY = "dealer_inventory";
	public static String PARAM_LOAN = "loan";
	public static String PARAM_LOAN_INTEREST = "loan_interest";
	public static String PARAM_LOAN_INTEREST_RATE = "loan_interest_rate";
	public static String PARAM_LOCATION = "location";
	public static String PARAM_LOCATION_COATS = "coat_inventory";
	public static String PARAM_LOCATION_DRUGS = "location_inventory";
	public static String PARAM_LOCATION_GUNS = "gun_inventory";
	public static String PARAM_USER_MESSAGES = "user_messages";
	public static String PARAM_USER_SONGS = "user_songs";
	public static String PARAM_DRUG_MESSAGES = "drug_messages";
	public static String PARAM_LOW_MULTIPLIER = "low_multiplier";
	public static String PARAM_LOW_PROBABILITY = "low_probability";
	public static String PARAM_MAP_X = "map_x";
	public static String PARAM_MAP_Y = "map_y";
	public static String PARAM_MAX_FIREPOWER = "max_dealer_firepower";
	public static String PARAM_MAX_SPACE = "max_space";
	public static String PARAM_MONEY_INVESTED_IN_BANK = "money_invested";
	public static String PARAM_MONEY_PAID_TO_LOAN_SHARK = "money_paid_to_loan_shark";
	public static String PARAM_MONEY_SPENT_ON_COATS = "money_spent_on_coats";
	public static String PARAM_MONEY_SPENT_ON_GUNS = "money_spent_on_guns";
	public static String PARAM_NAME = "name";
	public static String PARAM_NUM_DRUGS_BOUGHT = "num_drugs_bought";
	public static String PARAM_NUM_DRUGS_SOLD = "num_drugs_sold";
	public static String PARAM_PRICE_VARIANCE = "price_variance";
	public static String PARAM_RUN_ATTEMPTS = "run_attempts";
	public static String PARAM_SPACE = "space";
	public static String PARAM_SUCCESSFUL_RUNS = "times_ran";
	public static String PARAM_TOTAL_DAYS = "total_days";
	
	// Initialize the game info with a serialized version.
	public GameState(String serialized_game_info) {
		dealer_drugs_ = new HashMap<String, Integer>();
		dealer_guns_ = new HashMap<String, Integer>();
		location_drugs_ = new HashMap<String, Integer>();
		location_coats_ = new HashMap<String, Integer>();
		location_guns_ = new HashMap<String, Integer>();
		user_messages_ = new Vector<String>();
		user_songs_ = new Vector<String>();
		drug_messages_ = new Vector<String>();
		fight_messages_ = new Vector<String>();
		num_drugs_bought_ = new HashMap<String, Integer>();
		amount_drugs_bought_ = new HashMap<String, Integer>();
		num_drugs_sold_ = new HashMap<String, Integer>();
		amount_drugs_sold_ = new HashMap<String, Integer>();
		
		setupDynamicGameInformation(serialized_game_info);
		if (location_drugs_.size() == 0) {
			initializeGame();
		}
	}
	
	
	public void initializeGame() {
		dealer_cash_ = 5500;
		dealer_space_ = 100;
		dealer_max_space_ = 100;
		dealer_loan_ = 3500;
		dealer_bank_ = 0;
			
		// TODO: use constants for locations
		location_ = 0;
		
		game_days_left_ = 30;
		total_days_ = 30;
		dealer_health_ = 100;
		location_cops_ = 0;
		dealer_drugs_ = new HashMap<String, Integer>();
		dealer_guns_ = new HashMap<String, Integer>();
		location_drugs_ = new HashMap<String, Integer>();
		location_coats_ = new HashMap<String, Integer>();
		location_guns_ = new HashMap<String, Integer>();
		user_messages_ = new Vector<String>();
		user_songs_ = new Vector<String>();
		drug_messages_ = new Vector<String>();
		fight_messages_ = new Vector<String>();
		dealer_cops_killed_ = 0;
		dealer_deputies_killed_ = 0;
		dealer_successful_runs_ = 0;
		dealer_run_attempts_ = 0;
		coats_bought_ = 0;
		coats_added_size_ = 0;
		money_spent_on_coat_ = 0;
		guns_bought_ = 0;
		money_spent_on_guns_ = 0;
		max_dealer_firepower_ = 0;
		num_drugs_bought_ = new HashMap<String, Integer>();
		amount_drugs_bought_ = new HashMap<String, Integer>();
		num_drugs_sold_ = new HashMap<String, Integer>();
		amount_drugs_sold_ = new HashMap<String, Integer>();
		money_paid_to_loan_shark_ = 0;
		loan_interest_ = 0;
		days_to_pay_off_loan_ = 0;
		money_invested_in_bank_ = 0;
		bank_interest_ = 0;
	}
	
	public void setupDynamicGameInformation(String serialized_game_info) {
		StreamTokenizer tokenizer =
			new StreamTokenizer(new StringReader(serialized_game_info));

		tokenizer.quoteChar('#');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars(':', ':');
		tokenizer.eolIsSignificant(false);
		try {
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				String[] group = tokenizer.sval.split(":", 2);
				if (group.length != 2) {
					Log.d("dopewars", "Invalid game info group: " + tokenizer.sval);
				} else if (group[0].equals(PARAM_CASH)) {
					dealer_cash_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_SPACE)) {
					dealer_space_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_MAX_SPACE)) {
					dealer_max_space_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_LOAN)) {
					dealer_loan_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_BANK)) {
					dealer_bank_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_DEALER_LOCATION)) {
					location_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_DAYS_LEFT)) {
					game_days_left_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_TOTAL_DAYS)) {
					total_days_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_HEALTH)) {
					dealer_health_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_COPS)) {
					location_cops_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_INVENTORY)) {
					dealer_drugs_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_DEALER_GUNS)) {
					dealer_guns_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_LOCATION_DRUGS)) {
					location_drugs_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_LOCATION_COATS)) {
					location_coats_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_LOCATION_GUNS)) {
					location_guns_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_USER_MESSAGES)) {
					user_messages_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(PARAM_USER_SONGS)) {
					user_songs_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(PARAM_DRUG_MESSAGES)) {
					drug_messages_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(PARAM_FIGHT_MESSAGES)) {
					fight_messages_ = parseStringVectorParam(group[1]);
				} else if (group[0].equals(PARAM_COPS_KILLED)) {
					dealer_cops_killed_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_DEPUTIES_KILLED)) {
					dealer_deputies_killed_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_SUCCESSFUL_RUNS)) {
					dealer_successful_runs_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_RUN_ATTEMPTS)) {
					dealer_run_attempts_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_COATS_BOUGHT)) {
					coats_bought_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_COATS_ADDED_SIZE)) {
					coats_added_size_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_MONEY_SPENT_ON_COATS)) {
					money_spent_on_coat_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_GUNS_BOUGHT)) {
					guns_bought_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_MONEY_SPENT_ON_GUNS)) {
					money_spent_on_guns_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_MAX_FIREPOWER)) {
					max_dealer_firepower_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_NUM_DRUGS_BOUGHT)) {
					num_drugs_bought_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_AMOUNT_DRUGS_BOUGHT)) {
					amount_drugs_bought_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_NUM_DRUGS_SOLD)) {
					num_drugs_sold_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_AMOUNT_DRUGS_SOLD)) {
					amount_drugs_sold_ = parseStringToIntParam(group[1]);
				} else if (group[0].equals(PARAM_MONEY_PAID_TO_LOAN_SHARK)) {
					money_paid_to_loan_shark_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_LOAN_INTEREST)) {
					loan_interest_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_DAYS_TO_PAY_OFF_LOAN)) {
					days_to_pay_off_loan_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_MONEY_INVESTED_IN_BANK)) {
					money_invested_in_bank_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_BANK_INTEREST)) {
					bank_interest_ = Integer.parseInt(group[1]);
				} else if (group[0].equals(PARAM_DO_INITIALIZE)) {
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
		String serialized_game = integerParam(PARAM_CASH, dealer_cash_) +
			integerParam(PARAM_SPACE, dealer_space_) +
			integerParam(PARAM_MAX_SPACE, dealer_max_space_) +
			integerParam(PARAM_LOAN, dealer_loan_) +
			integerParam(PARAM_BANK, dealer_bank_) +
			integerParam(PARAM_DEALER_LOCATION, location_) +
			integerParam(PARAM_DAYS_LEFT, game_days_left_) +
			integerParam(PARAM_TOTAL_DAYS, total_days_) +
			integerParam(PARAM_HEALTH, dealer_health_) +
			integerParam(PARAM_COPS, location_cops_) +
			stringToIntParam(PARAM_INVENTORY, dealer_drugs_) +
			stringToIntParam(PARAM_DEALER_GUNS, dealer_guns_) +
			stringToIntParam(PARAM_LOCATION_DRUGS, location_drugs_) +
			stringToIntParam(PARAM_LOCATION_COATS, location_coats_) +
			stringToIntParam(PARAM_LOCATION_GUNS, location_guns_) +
			stringVectorParam(PARAM_USER_MESSAGES, user_messages_) +
			stringVectorParam(PARAM_USER_SONGS, user_songs_) +
			stringVectorParam(PARAM_DRUG_MESSAGES, drug_messages_) +
			stringVectorParam(PARAM_FIGHT_MESSAGES, fight_messages_) +
			integerParam(PARAM_COPS_KILLED, dealer_cops_killed_) +
			integerParam(PARAM_DEPUTIES_KILLED, dealer_deputies_killed_) +
			integerParam(PARAM_SUCCESSFUL_RUNS, dealer_successful_runs_) +
			integerParam(PARAM_RUN_ATTEMPTS, dealer_run_attempts_) +
			integerParam(PARAM_COATS_BOUGHT, coats_bought_) +
			integerParam(PARAM_COATS_ADDED_SIZE, coats_added_size_) +
			integerParam(PARAM_MONEY_SPENT_ON_COATS, money_spent_on_coat_) +
			integerParam(PARAM_GUNS_BOUGHT, guns_bought_) +
			integerParam(PARAM_MONEY_SPENT_ON_GUNS, money_spent_on_guns_) +
			integerParam(PARAM_MAX_FIREPOWER, max_dealer_firepower_) +
			stringToIntParam(PARAM_NUM_DRUGS_BOUGHT, num_drugs_bought_) +
			stringToIntParam(PARAM_AMOUNT_DRUGS_BOUGHT, amount_drugs_bought_) +
			stringToIntParam(PARAM_NUM_DRUGS_SOLD, num_drugs_sold_) +
			stringToIntParam(PARAM_AMOUNT_DRUGS_SOLD, amount_drugs_sold_) +
			integerParam(PARAM_MONEY_PAID_TO_LOAN_SHARK, money_paid_to_loan_shark_) +
			integerParam(PARAM_LOAN_INTEREST, loan_interest_) +
			integerParam(PARAM_DAYS_TO_PAY_OFF_LOAN, days_to_pay_off_loan_) +
			integerParam(PARAM_MONEY_INVESTED_IN_BANK, money_invested_in_bank_) +
			integerParam(PARAM_BANK_INTEREST, bank_interest_) +
			integerParam(PARAM_DO_INITIALIZE, do_initial_setup_);
		
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
	
	public String game_id_;
	public double loan_interest_rate_;
	public double bank_interest_rate_;
	public double coat_likelihood_;
	public double gun_likelihood_;
	public double cops_likelihood_;
	
	// Current game information, this information is mutable within a game.
	public int game_type_;
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
	public Vector<String> user_messages_;
	public Vector<String> user_songs_;
	public Vector<String> drug_messages_;
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
*/
}
