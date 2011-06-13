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
  public int coat_price_;
  public int gun_price_;

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
      coat_price_ = 0;
      gun_price_ = 0;

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

  public void SetupCoats() {
    if (coat_price_ > 0) {
      return;
    }
    if (random_.nextFloat() < 0.8) {
      coat_price_ = 1000;
    } else {
      coat_price_ = 0;
    }
  }

  public void SetupGuns() {
    if (gun_price_ > 0) {
      return;
    }
    if (random_.nextFloat() < 0.8) {
      gun_price_ = 1000;
    } else {
      gun_price_ = 0;
    }
  }

  // When moving from one location to another (advancing one turn) this resets all the drugs
  // that are available and processes all the random events that can happen on a turn-by-turn
  // basis.
  public void SetupNewLocation() {
    SetupNewDrugs();
    SetupHardass();
    SetupCoats();
    SetupGuns();
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
    for (int i = 0; i < dealer_drugs_.size(); ++i) {
      serialized_game += Integer.toString(dealer_drugs_.elementAt(i)) + ",";
    }
    serialized_game += Integer.toString(hardass_health_) + ",";
    serialized_game += Integer.toString(hardass_deputies_) + ",";
    serialized_game += Integer.toString(coat_price_) + ",";
    serialized_game += Integer.toString(gun_price_) + ",";
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
      for (int i = 0; i < drugs_.size(); ++i) {
        tokenizer.nextToken();
        if (dealer_drugs_.size() <= i) {
          dealer_drugs_.add((int)tokenizer.nval);
        } else {
          dealer_drugs_.setElementAt((int)tokenizer.nval, i);
        }
      }
      tokenizer.nextToken();
      hardass_health_ = (int)tokenizer.nval;
      tokenizer.nextToken();
      hardass_deputies_ = (int)tokenizer.nval;
      tokenizer.nextToken();
      coat_price_ = (int)tokenizer.nval;
      tokenizer.nextToken();
      gun_price_ = (int)tokenizer.nval;
    } catch (IOException e) {
      Log.d(TAG, "Parsing error with value " + tokenizer.sval);
    }
  }

  // Random number generator for this activity.
  public static Random random_ = new Random();
}
