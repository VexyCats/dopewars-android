package com.daverin.dopewars;

import java.util.Random;
import java.util.Vector;

public class Global {
	
	public static class Drug {
		public String drug_name_;
		public int base_price_;
		public int range_;
		public boolean outlier_high_;
		public double outlier_high_probability_;
		public double outlier_high_multiplier_;
		public boolean outlier_low_;
		public double outlier_low_probability_;
		public double outlier_low_multiplier_;
	}
	
//	public static long current_dealer_id_;
	
	// Random number generator!
    public static Random rand_gen_ = new Random();
    
    // Game setup information, set by the main menu and used by the game.
    //public static int base_drug_count_;
	//public static int drug_count_variance_;
	//public static Vector<Drug> available_drugs_;
}
