/**
 * This activity represents the Main game screen for DopeWars 2. The layout is regenerated every
 * move, but consists primarily of multiple rows of "buttons" and a top layer of "messages".
 * Each button represents something that can be done (usually), and each message is purely
 * informational and can be cleared by tapping on it. Holding one message will make them all
 * disappear.
 * 
 * The buttons have a particular color coding that indicates the available options. An option
 * that you can't do anything with (e.g. a drug that costs more money than you have) shows up
 * with a gray background. Something you can buy shows up green. Something you can sell shows
 * up reddish/orange. Something you can both buy and sell shows up blue. Something informational
 * or functional like the Subway or the Inventory shows up yellow.
 * 
 * There are also a lot of various dialogs contained in this activity, for doing things like
 * choosing the quantity of drugs to buy and sell, putting money in the bank, and choosing a
 * new location.
 */

package com.daverin.dopewars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class DopeWarsGame extends Activity {
	
	// The dialogs available in the game include moving from place to place on the subway,
	// buying and selling drugs, looking at your inventory, the loan shark, and the bank.
	public static final int DIALOG_SUBWAY = 2002;
	public static final int DIALOG_DRUG_BUY = 2003;
	public static final int DIALOG_INVENTORY = 2004;
	public static final int DIALOG_DRUG_SELL = 2005;
	public static final int DIALOG_LOAN_SHARK = 2006;
	public static final int DIALOG_BANK_DEPOSIT = 2007;
	public static final int DIALOG_BANK_WITHDRAW = 2008;
	public static final int DIALOG_END_GAME = 2009;
	
	// Strings identify the properties of most objects in the game. Define them here.
	// TODO: use a strings object instead?
	public static final String GUNS_FIREPOWER = "firepower";
	public static final String GUNS_SPACE = "space";
	public static final String GUNS_PRICE = "base_price";
	public static final String GUNS_VARIANCE = "price_variance";
	public static final String COATS_ADDITIONAL_SPACE = "additional_space";
	public static final String LOCATION_X = "map_x";
	public static final String LOCATION_Y = "map_y";
	public static final String LOCATION_LOAN_SHARK = "has_loan_shark";
	public static final String LOCATION_BANK = "has_bank";
	public static final String LOCATION_DRUGS = "base_drugs";
	public static final String LOCATION_VARIANCE = "drug_variance";
	public static final String DRUGS_ICON = "icon";
	public static final String COATS_PRICE = "base_price";
	public static final String COATS_VARIANCE = "price_variance";
	
	public static final String INVENTORY_DRUGS = "drugs";
	public static final String INVENTORY_GUNS = "guns";
	public static final String INVENTORY_MONEY = "money";
	public static final String INVENTORY_EMPTY = "(none)";
	
	// This listener, when activated, just sets the visibility of the view to gone, but it also
	// removes the target message from the queue of messages. It does not redraw immediately
	// when removing because the way messages are handled would make any existing messages hop
	// around the screen as they were being cleared.
	public class HideMessageListener implements View.OnClickListener {
		public HideMessageListener(String message) {
			message_ = message;
		}
		public void onClick(View v) {
			v.setVisibility(View.GONE);
			
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
			game_information_.fight_messages_.remove(message_);
			game_information_.game_messages_.remove(message_);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
		}
		String message_;
	}
	
	// A simple listener that just opens a particular dialog is used in a couple different
	// situations.
	public class BasicDialogListener implements View.OnClickListener {
		public BasicDialogListener(int dialog_id) {
			dialog_id_ = dialog_id;
		}
		public void onClick(View v) {
			showDialog(dialog_id_);
		}
		int dialog_id_;
	}
	
	// A simple long-click listener that just opens a particular dialog is used in a couple
	// different situations.
	// A simple long click listener that just opens a particular dialog is used in a couple
	// different situations.
	// TODO: Is the code savings worth the complexity? Better named listeners could be more
	// self-documenting.
	public class LongClickDialogListener implements View.OnLongClickListener {
		public LongClickDialogListener(int dialog_id) {
			dialog_id_ = dialog_id;
		}
		public boolean onLongClick(View v) {
			showDialog(dialog_id_);
			return true;
		}
		int dialog_id_;
	}
	
	// A click on a drug can indicate buying or selling a particular drug, this will open the
	// right dialog acting on the right drug.
	public class DrugClickListener implements View.OnClickListener {
		public DrugClickListener(String drug_name, int dialog_id) {
			drug_name_ = drug_name;
			dialog_id_ = dialog_id;
		}
		public void onClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(dialog_id_);
		}
		String drug_name_;
		int dialog_id_;
	}
	
	// Respond to a long click that needs a drug to operate on. This is only ever used to sell
	// drugs, in the case when both buying and selling are an option.
	public class SellDrugsLongClickListener implements View.OnLongClickListener {
		public SellDrugsLongClickListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public boolean onLongClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(DIALOG_DRUG_SELL);
			return true;
		}
		String drug_name_;
	}
	
	// When the cops are around and the dealer chooses to fight, this listener executes the fight.
	// The fight consists of the accumulated firepower of the dealer hitting the cops, then
	// whatever's left of the cops hitting the dealer.
	public class FightListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
			
			// Total up the dealer's firepower.
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int total_dealer_firepower = 0;
	        Iterator<String> gun_names = game_information_.dealer_guns_.keySet().iterator();
	        while (gun_names.hasNext()) {
	        	String next_gun_name = gun_names.next();
	        	total_dealer_firepower +=
	        		game_information_.guns_.get(next_gun_name).get(GUNS_FIREPOWER) * 
	        		game_information_.dealer_guns_.get(next_gun_name);
	        }
	        
	        // If the cops are dead, the dealer has won, get some cash from the cops.
	        // TODO: allow the cash amount to be parameterized by game settings
	        // TODO: make the damage less deterministic
	        // TODO: consider having the cops hit the dealer even if the dealer kills them all
	        //       on that turn.
	        game_information_.fight_messages_.clear();
	        if (total_dealer_firepower > game_information_.location_cops_) {
	        	game_information_.dealer_deputies_killed_ +=
	        		Math.max(0, game_information_.location_cops_ - 10);
	        	game_information_.dealer_cops_killed_++;
	        	game_information_.location_cops_ = 0;
	        	int won_cash = rand_gen_.nextInt(5) + 1 * 1000;
	        	game_information_.game_messages_.add("You found $" + Integer.toString(won_cash) +
	        			" on the cops!");
	        	game_information_.dealer_cash_ +=
	        		rand_gen_.nextInt(5) + 1 * 1000;
	        	
	        // If the cops are still alive, have them hit the dealer.
	        } else {
	        	int dealer_damage = 6 + Math.max(0, (game_information_.location_cops_ - 10));
	        	game_information_.dealer_health_ -= dealer_damage;
	        	game_information_.fight_messages_.add("The cops hit you for " +
	        			Integer.toString(dealer_damage) + " points of health");
	        	game_information_.dealer_deputies_killed_ +=
	        		Math.min(total_dealer_firepower, game_information_.location_cops_ - 10);
	        	if (game_information_.location_cops_ > 10) {
	        		game_information_.fight_messages_.add("You killed " +
	        				Integer.toString(Math.min(total_dealer_firepower,
	        						game_information_.location_cops_ - 10)) + " deputies!");
	        	}
	        	if (game_information_.location_cops_ - total_dealer_firepower < 10) {
	        		game_information_.fight_messages_.add("You tagged Officer Hardass for " +
	        				Integer.toString(total_dealer_firepower -
	        						Math.max(0, game_information_.location_cops_ - 10)) + 
	        						"points of damage!");
	        	}
	        	game_information_.location_cops_ -= total_dealer_firepower;
	        }
	        
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}
	
	// When the cops are around and the dealer chooses to run, this listener handles the
	// consequences.
	public class RunListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        
	        // If the dealer gets away, he will drop some cash. If not, the cops will hit him.
	        // TODO: Parameterize the run away success rate in game settings
	        // TODO: Parameterize the cash lost amount in game settings
	        // TODO: Also possibly lose guns (parameterized loss rate)
			game_information_.dealer_run_attempts_++;
	        if (rand_gen_.nextDouble() < 0.5) {
	        	int lost_cash = Math.min(game_information_.dealer_cash_,
	        			rand_gen_.nextInt(5) + 1 * 1000);
	        	game_information_.dealer_cash_ -= lost_cash;
	        	game_information_.game_messages_.add("You got away but lost $" +
	        			Integer.toString(lost_cash) + " while running");
	        	game_information_.location_cops_ = 0;
	        	game_information_.dealer_successful_runs_++;
	        } else {
	        	int dealer_damage = 6 + Math.max(0, (game_information_.location_cops_ - 10));
	        	game_information_.dealer_health_ -= dealer_damage;
	        	game_information_.fight_messages_.clear();
	        	game_information_.fight_messages_.add("You couldn't get away, and got hit for " +
	        			Integer.toString(dealer_damage) + " points of damage");
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}

	// When coats are available they show up as regular buttons, when those buttons are pressed
	// this listener handles the purchase of the coat. When a coat is purchased it is removed
	// from the coat inventory at the current location. Coats are not kept track of as independent
	// items,  buying them just changes the space allotted to the dealer.
	public class BuyCoatListener implements View.OnClickListener {
		public BuyCoatListener(String coat_name) {
			coat_name_ = coat_name;
		}
		
		public void onClick(View v) {
			dealer_data_.open();
			
			// TODO: keep statistics on coats bought
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
			game_information_.dealer_cash_ -=
				game_information_.location_coats_.get(coat_name_).intValue();
	        float new_space = game_information_.coats_.get(coat_name_).get(COATS_ADDITIONAL_SPACE);
	        game_information_.dealer_max_space_ += new_space;
	        game_information_.dealer_space_ += new_space;
	        game_information_.location_coats_.remove(coat_name_);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
		
		String coat_name_;
	}
	
	// When guns are available they show up as regular buttons, when those buttons are pressed
	// this listener handles the purchase of the gun. When a gun is purchased it is removed from
	// the gun inventory at the current location.
	public class BuyGunListener implements View.OnClickListener {
		public BuyGunListener(String gun_name) {
			gun_name_ = gun_name;
		}
		
		public void onClick(View v) {
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));

	        // TODO: keep statistics on guns bought
			game_information_.dealer_cash_ -=
				game_information_.location_guns_.get(gun_name_).intValue();
			game_information_.dealer_space_ -=
				game_information_.guns_.get(gun_name_).get(GUNS_SPACE);
	        int num_guns = 1;
	        if (game_information_.dealer_guns_.get(gun_name_) != null) {
	        	num_guns += game_information_.dealer_guns_.get(gun_name_);
	        }
	        game_information_.dealer_guns_.put(gun_name_, num_guns);
	        game_information_.location_guns_.remove(gun_name_);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, 
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
		
		String gun_name_;
	}
	
	// When a drug is being purchased (the button on the dialog to enact the transaction is
	// pressed) this listener manages the inventory transaction.
	public class BuyDrugsListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: tabulate quantities of drugs that are moved for statistics
	        dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int drug_price = game_information_.location_drugs_.get(dialog_drug_name_).intValue();
	        int drug_quantity = Integer.parseInt(((TextView)drug_buy_dialog_.findViewById(
	        		R.id.drug_quantity)).getText().toString());
	        game_information_.dealer_cash_ -= drug_quantity * drug_price;
	        game_information_.dealer_space_ -= drug_quantity;
	        int new_quantity = drug_quantity;
	        if (game_information_.dealer_drugs_.get(dialog_drug_name_) != null) {
	        	new_quantity += game_information_.dealer_drugs_.get(dialog_drug_name_);
	        }
	        game_information_.dealer_drugs_.put(dialog_drug_name_, new_quantity);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, 
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_BUY);
		}
	}
	
    // When a drug is being sold (the button on the dialog to enact the transaction is pressed)
	// this listener manages the inventory transaction.
	public class SellDrugsListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: tabulate quantities of drugs that are moved for statistics
	        dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int drug_price = game_information_.location_drugs_.get(
	        		dialog_drug_name_).intValue();
	        int drug_quantity = Integer.parseInt(((TextView)drug_sell_dialog_.findViewById(
	        		R.id.drug_quantity)).getText().toString());
	        game_information_.dealer_cash_ += drug_quantity * drug_price;
	        game_information_.dealer_space_ += drug_quantity;
	        int new_quantity = game_information_.dealer_drugs_.get(
	        		dialog_drug_name_) - drug_quantity;
	        if (new_quantity > 0) {
	        	game_information_.dealer_drugs_.put(dialog_drug_name_, new_quantity);
	        } else {
	        	game_information_.dealer_drugs_.remove(dialog_drug_name_);
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_SELL);
		}
	}
	
	// When a loan payment is made this listener handles the money transaction.
	public class CompleteLoanListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: Tabulate when the loan is paid off.
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int loan_payment = Integer.parseInt(((TextView)loan_shark_dialog_.findViewById(
	        		R.id.loan_amount)).getText().toString());
	        game_information_.dealer_loan_ -= loan_payment;
	        game_information_.dealer_cash_ -= loan_payment;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_LOAN_SHARK);
		}
	}
	
	// When a bank deposit is made this listener handles the money transaction.
	public class CompleteBankDepositListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: monitor bank transactions for statistics
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int bank_deposit = Integer.parseInt(((TextView)bank_deposit_dialog_.findViewById(
	        		R.id.bank_amount)).getText().toString());
	        game_information_.dealer_bank_ += bank_deposit;
	        game_information_.dealer_cash_ -= bank_deposit;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_BANK_DEPOSIT);
		}
	}

	// When a bank withdrawal is made this listener handles the money transaction.
	public class CompleteBankWithdrawListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: keep track of transactions for statistics
			dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int bank_withdrawal = Integer.parseInt(((TextView)bank_withdraw_dialog_.findViewById(
	        		R.id.bank_amount)).getText().toString());
	        game_information_.dealer_bank_ -= bank_withdrawal;
	        game_information_.dealer_cash_ += bank_withdrawal;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_BANK_DEPOSIT);
		}
	}
	
	// When the location is changed, the turn number is incremented and a whole new slate of
	// drugs and other random events is chosen. Also the interest rates compound.
	public class ChangeLocationListener implements View.OnClickListener {
		public ChangeLocationListener(int new_location) {
			location_ = new_location;
		}
		public void onClick(View v) {
	        dealer_data_.open();
			game_information_.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        game_information_.location_ = location_;
	        game_information_.game_days_left_ -= 1;
	        
	        // TODO: track interest payments for statistics
	        game_information_.dealer_loan_ +=
	        	(int)((double)game_information_.dealer_loan_ *
	        			game_information_.loan_interest_rate_);
	        game_information_.dealer_bank_ +=
	        	(int)((double)game_information_.dealer_bank_ *
	        			game_information_.bank_interest_rate_);
	        
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information_.getCurrentGameInformation());
	        dealer_data_.close();
			setupLocation();
			refreshDisplay();
			dismissDialog(DIALOG_SUBWAY);
		}
		int location_;
	}
	
	// When creating the activity there are a few initialization steps to perform.
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		loadIcons();
		
        dealer_data_ = new DealerDataAdapter(this);
        dealer_data_.open();
        
        // Initialize the static game information.
        String game = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID);
        String game_info = dealer_data_.getGameString(Integer.parseInt(game));
        game_information_ = new GameInformation(game_info);
       
        // Get the current game information just to see if the initial setup needs to happen.
        String current_game = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
		game_information_.setCurrentGameInformation(current_game);
        dealer_data_.close();
        
        // Set up the main content of the view.
        setContentView(R.layout.main_game_screen);
        
        if (game_information_.do_initial_setup_ == 1) {
        	setupLocation();
        }
        refreshDisplay();
    }
	
	// When the configuration is changed (e.g. orientation changing) the display needs to be
	// re-calculated so the rows of buttons expand/contract to the right width.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		refreshDisplay();
		super.onConfigurationChanged(newConfig);
	}
	
	// Select the appropriate dialog depending on id.
	@Override
    protected Dialog onCreateDialog(int id) {
		// Just initialize all the dialogs instead of checking for each one's initialization.
		// TODO: Is this really the cleanest way to do this?
		initDialogs();
		
		switch(id) {
		case DIALOG_SUBWAY:
			return subway_dialog_;
		case DIALOG_INVENTORY:
			return inventory_dialog_;
		case DIALOG_DRUG_BUY:
			return drug_buy_dialog_;
		case DIALOG_DRUG_SELL:
			return drug_sell_dialog_;
		case DIALOG_LOAN_SHARK:
			return loan_shark_dialog_;
		case DIALOG_BANK_DEPOSIT:
			return bank_deposit_dialog_;
		case DIALOG_BANK_WITHDRAW:
			return bank_withdraw_dialog_;
		case DIALOG_END_GAME:
			return end_game_dialog_;
		}
        return super.onCreateDialog(id);
	}
	
	// There's a lot of work that goes into dialog preparation, since there are so many dialogs
	// and they cover so much functionality.
	// TODO: Maybe the preparation of complicated dialogs should be broken out into its own
	//       sub-fns for some?
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
		dealer_data_.open();
		game_information_.setCurrentGameInformation(
				dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
		Dialog dialog_to_expand = null;
		switch(id) {
		case DIALOG_SUBWAY:
			dialog_to_expand = subway_dialog_;
			RelativeLayout subway_buttons_layout =
				(RelativeLayout)subway_dialog_.findViewById(R.id.subway_button_layout);
			subway_buttons_layout.removeAllViews();
        	int num_locations = game_information_.locations_.size();
	        for (int i = 0; i < num_locations; ++i) {
	        	String location_name =
	        		(String)(game_information_.locations_.keySet().toArray()[i]);
	        	// TODO: make the buttons look good
	        	Button b = new Button(this);
	        	// TODO: make the button size configuration by game parameters
	        	RelativeLayout.LayoutParams layout_params =
	        		new RelativeLayout.LayoutParams(90, 30);
	        	layout_params.leftMargin =
	        		game_information_.locations_.get(location_name).get(LOCATION_X).intValue();
	        	layout_params.topMargin =
	        		game_information_.locations_.get(location_name).get(LOCATION_Y).intValue();
	        	b.setLayoutParams(layout_params);
	        	b.setText(location_name);
	        	b.setOnClickListener(new ChangeLocationListener(i));
	        	subway_buttons_layout.addView(b);
	        }
	        break;
		case DIALOG_INVENTORY:
			dialog_to_expand = inventory_dialog_;
			LinearLayout inventory_layout =
				(LinearLayout)inventory_dialog_.findViewById(R.id.inventory_layout);
			inventory_layout.setGravity(Gravity.CENTER);
			inventory_layout.removeAllViews();
			// TODO: these major headers could probably be static parts of the layout
        	inventory_layout.addView(makeInventoryHeader(INVENTORY_DRUGS));
	        if (game_information_.dealer_drugs_.size() == 0) {
	        	// TODO: make colors configurable
	        	inventory_layout.addView(
	        			makeInventoryText(Color.GRAY, Gravity.CENTER, INVENTORY_EMPTY));
	        } else {
		        Iterator<String> drug_names =
		        	game_information_.dealer_drugs_.keySet().iterator();
		        while (drug_names.hasNext()) {
		        	String drug_string = drug_names.next();
		        	// TODO: make colors configurable
		        	inventory_layout.addView(makeInventoryRow(Color.BLUE, drug_string,
		        			game_information_.dealer_drugs_.get(
		        					drug_string).toString()));
		        }
	        }
	        inventory_layout.addView(makeInventoryHeader(INVENTORY_GUNS));
        	if (game_information_.dealer_guns_.size() == 0) {
        		// TODO: make colors configurable
        		inventory_layout.addView(
        				makeInventoryText(Color.GRAY, Gravity.CENTER, INVENTORY_EMPTY));
	        } else {
		        Iterator<String> gun_names = game_information_.dealer_guns_.keySet().iterator();
		        while (gun_names.hasNext()) {
		        	String gun_string = gun_names.next();
		        	// TODO: make colors configurable
		        	inventory_layout.addView(makeInventoryRow(Color.RED, gun_string,
		        			game_information_.dealer_guns_.get(
		        					gun_string).toString()));
		        }
	        }
        	inventory_layout.addView(makeInventoryHeader(INVENTORY_MONEY));
        	
        	// TODO: staticize all this stuff or better put it in the layout
        	inventory_layout.addView(makeInventoryRow(Color.GREEN, "In hand",
	        		Integer.toString(game_information_.dealer_cash_)));
        	inventory_layout.addView(makeInventoryRow(Color.GREEN, "In bank",
	        		Integer.toString(game_information_.dealer_bank_)));
        	inventory_layout.addView(makeInventoryRow(Color.GREEN, "Owed",
	        		"(" + Integer.toString(game_information_.dealer_loan_) + ")"));
        	inventory_layout.addView(makeInventoryRow(Color.GREEN, "Total",
	        		Integer.toString(game_information_.dealer_cash_ +
	        				game_information_.dealer_bank_ -
	        				game_information_.dealer_loan_)));
        	
        	// A click should make the dialog disappear.
        	inventory_layout.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	    			dismissDialog(DIALOG_INVENTORY);
	        	}
	        });
        	break;
		case DIALOG_DRUG_BUY:
			// Note that this dialog is not expanded.
			
			// TODO: make "Buy" a static part of the layout?
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_name))).setText(
	        		"Buy " + dialog_drug_name_);
	        float drug_buy_price = 
	        	game_information_.location_drugs_.get(dialog_drug_name_);
	        // TODO: make the "$" a static part of the layout?
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_price))).setText(
	        		"$" + Float.toString(drug_buy_price));
	        int max_num_drugs = 
	        	(int)(game_information_.dealer_cash_ /
	        			game_information_.location_drugs_.get(
	        					dialog_drug_name_));
	        max_num_drugs = Math.min(max_num_drugs, game_information_.dealer_space_);
	        
	        // TODO: find the simplest way of making sure that the slider gets drawn right
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
	        		max_num_drugs);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
	        		max_num_drugs);
	        
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_quantity))).setText(
	        		Integer.toString(max_num_drugs));
	        
	        // A click on anything but the target button will make the dialog disappear.
	        ((LinearLayout)drug_buy_dialog_.findViewById(R.id.drug_buy_layout)).setOnClickListener(
	        		new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_DRUG_BUY);
	        	}
	        });
	        ((ImageView)(drug_buy_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new BuyDrugsListener());
	        break;
		case DIALOG_DRUG_SELL:
			// Note that this dialog is not expanded.
			
			// TODO: make "Sell" a static part of the layout?
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_name))).setText(
	        		"Sell " + dialog_drug_name_);
	        float drug_sell_price = game_information_.location_drugs_.get(dialog_drug_name_);
	        // TODO: make the "$" a static part of the layout?
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_price))).setText(
	        		"$" + Float.toString(drug_sell_price));
	        int drug_quantity = game_information_.dealer_drugs_.get(
	        		dialog_drug_name_).intValue();
	        
	        // TODO: find the simplest way of making sure that the slider gets drawn right
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
	        		drug_quantity);
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
	        		drug_quantity);
	        
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_quantity))).setText(
	        		Integer.toString(drug_quantity));
	        
	        // A click on anything but the target button will make the dialog disappear.
	        ((LinearLayout)drug_sell_dialog_.findViewById(R.id.drug_sell_layout)).
	        setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_DRUG_SELL);
	        	}
	        });
	        ((ImageView)(drug_sell_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new SellDrugsListener());
	        break;
		case DIALOG_LOAN_SHARK:
			// Note that this dialog is not expanded.
			
			int max_loan = Math.min(game_information_.dealer_cash_,
					game_information_.dealer_loan_);
	        
	        // TODO: find the simplest way of making sure that the slider gets drawn right
	        ((SeekBar)(loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setMax(max_loan);
	        ((SeekBar)(loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setProgress(max_loan);
	        
	        ((TextView)(loan_shark_dialog_.findViewById(R.id.loan_amount))).setText(
	        		Integer.toString(max_loan));
	        
	        // A click on anything but the target button will make the dialog disappear.
	        ((LinearLayout)loan_shark_dialog_.findViewById(R.id.loan_shark_layout)).
	        setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_LOAN_SHARK);
	        	}
	        });
	        ((ImageView)(loan_shark_dialog_.findViewById(R.id.loan_icon))).setOnClickListener(
	        		new CompleteLoanListener());
	        break;
		case DIALOG_BANK_DEPOSIT:
			// Note that this dialog is not expanded.
			
			// TODO: find the simplest way of making sure that the slider gets drawn right
			((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
					game_information_.dealer_cash_);
	        ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
	        		game_information_.dealer_cash_);
	        
	        ((TextView)(bank_deposit_dialog_.findViewById(R.id.bank_amount))).setText(
	        		Integer.toString(game_information_.dealer_cash_));
	        
	        // A click on anything but the target button will make the dialog disappear.
	        ((LinearLayout)bank_deposit_dialog_.findViewById(R.id.bank_deposit_layout)).
	        setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_BANK_DEPOSIT);
	        	}
	        });
	        ((ImageView)(bank_deposit_dialog_.findViewById(R.id.bank_icon))).setOnClickListener(
	        		new CompleteBankDepositListener());
	        break;
		case DIALOG_BANK_WITHDRAW:
			// Note that this dialog is not expanded.
			
			// TODO: find the simplest way of making sure that the slider gets drawn right
			((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
					game_information_.dealer_bank_);
	        ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
	        		game_information_.dealer_bank_);
	        
	        ((TextView)(bank_withdraw_dialog_.findViewById(R.id.bank_amount))).setText(
	        		Integer.toString(game_information_.dealer_bank_));
	        
	        // A click on anything but the target button will make the dialog disappear.
	        ((LinearLayout)bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_layout)).
	        setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_BANK_WITHDRAW);
	        	}
	        });
	        ((ImageView)(bank_withdraw_dialog_.findViewById(R.id.bank_icon))).setOnClickListener(
	        		new CompleteBankWithdrawListener());
	        break;
		case DIALOG_END_GAME:
			dialog_to_expand = end_game_dialog_;
			
			// TODO: Add lots of statistics about the game.
			((TextView)(end_game_dialog_.findViewById(R.id.total_cash))).setText(
        			"$" + Integer.toString(game_information_.dealer_cash_ + 
        					game_information_.dealer_bank_ - 
        					game_information_.dealer_loan_));

			((TextView)(end_game_dialog_.findViewById(R.id.total_deputies_killed))).setText(
        			Integer.toString(game_information_.dealer_deputies_killed_));
			((TextView)(end_game_dialog_.findViewById(R.id.total_cops_killed))).setText(
        			Integer.toString(game_information_.dealer_cops_killed_));
			((TextView)(end_game_dialog_.findViewById(R.id.running_stats))).setText(
        			Integer.toString(game_information_.dealer_successful_runs_) + " / " +
        			Integer.toString(game_information_.dealer_run_attempts_));
			
			// A click on the dialog will make it disappear.
	        ((LinearLayout)end_game_dialog_.findViewById(R.id.end_of_game_layout)).
	        setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_END_GAME);
	        		Intent i = new Intent();
	        		setResult(RESULT_OK, i);
	        		finish();
	        	}
	        });
	        break;
		}
		if (dialog_to_expand != null) {
			WindowManager.LayoutParams dialog_params =
				dialog_to_expand.getWindow().getAttributes();
			dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
			dialog_to_expand.getWindow().setAttributes(dialog_params);
		}
        dealer_data_.close();
    }
	
	// Convenience function for making a button on the main screen of rows of buttons. A button
	// here consists of a background with an image and two lines of text on it.
	private LinearLayout makeButton(int background_resource,
			int image_resource, String main_string, String secondary_string) {
		LinearLayout new_button = new LinearLayout(this);
		new_button.setOrientation(LinearLayout.VERTICAL);
		new_button.setGravity(Gravity.CENTER_HORIZONTAL);
		new_button.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		new_button.setBackgroundResource(background_resource);
    	ImageView button_image = new ImageView(this);
    	
    	// TODO: make this a configurable parameter
    	button_image.setLayoutParams(new LinearLayout.LayoutParams(44, 44));
    	button_image.setImageResource(image_resource);
    	button_image.setScaleType(ScaleType.FIT_CENTER);
    	new_button.addView(button_image);
    	TextView main_text = new TextView(this);
    	
    	// TODO: make this a configurable parameter
    	main_text.setLayoutParams(new LinearLayout.LayoutParams(
    			44,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	main_text.setGravity(Gravity.CENTER);
    	main_text.setHorizontallyScrolling(true);
    	
    	// TODO: make this a configurable parameter
    	main_text.setTextColor(Color.WHITE);
    	main_text.setText(main_string);
    	new_button.addView(main_text);
    	TextView secondary_text = new TextView(this);
    	
    	// TODO: make this a configurable parameter
    	secondary_text.setLayoutParams(new LinearLayout.LayoutParams(
    			44,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	secondary_text.setGravity(Gravity.CENTER);
    	secondary_text.setHorizontallyScrolling(true);
    	
    	// TODO: make this a configurable parameter
    	secondary_text.setTextColor(Color.GREEN);
    	secondary_text.setText(secondary_string);
    	new_button.addView(secondary_text);
    	
    	return new_button;
	}
	
	// Convenience function that pushes a newly made button onto a row and starts a new row if
	// the current row is full.
	private void pushButton(LinearLayout button) {
		button.measure(viewWidth_, viewHeight_);
		if (button.getMeasuredWidth() + total_width_added_ > viewWidth_) {
			outer_layout_.addView(current_row_);
    		current_row_ = new LinearLayout(this);
    		current_row_.setOrientation(LinearLayout.HORIZONTAL);
    		current_row_.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.FILL_PARENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
    		current_row_.setGravity(Gravity.CENTER_HORIZONTAL);
    		total_width_added_ = 0;
		}
    	total_width_added_ += button.getMeasuredWidth();
    	current_row_.addView(button);
	}
	
	// This completely empties the existing display and re-creates it with the current game state.
	private void refreshDisplay() {
        viewWidth_ = this.getResources().getDisplayMetrics().widthPixels;
        viewHeight_ = this.getResources().getDisplayMetrics().heightPixels;
		outer_layout_ = (LinearLayout)findViewById(R.id.outer_layout);
		current_row_ = new LinearLayout(this);
		current_row_.setOrientation(LinearLayout.HORIZONTAL);
		current_row_.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		outer_layout_.removeAllViews();
		total_width_added_ = 0;
        dealer_data_.open();
		game_information_.setCurrentGameInformation(
				dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
        
        // First check if there are cops. If there are cops don't show anything other than the
        // fight related buttons and info.
        if (game_information_.location_cops_ > 0) {
        	// TODO: make the name configurable
        	// TODO: make the health a parameter
        	// TODO: multiple base cops
        	LinearLayout hardass_button = makeButton(R.drawable.btn_translucent_gray,
        			R.drawable.head_cop, "Hardass",
        			Integer.toString(Math.min(10, 
        					game_information_.location_cops_)));
        	pushButton(hardass_button);
	    	
        	// TODO: make the name configurable
	    	if (game_information_.location_cops_ > 10) {
	    		LinearLayout deputies_button = makeButton(R.drawable.btn_translucent_gray,
	    				R.drawable.deputies, "Deputies",
	    				Integer.toString(game_information_.location_cops_ - 10));
	    		pushButton(deputies_button);
	    	}
	    	
	    	// TODO: make the name configurable
	    	// TODO: change the color?
	    	if (game_information_.dealer_guns_.size() > 0) {
	    		LinearLayout fight_button = makeButton(R.drawable.btn_translucent_green,
	    				R.drawable.fight, "Fight",
	    				Integer.toString(game_information_.dealer_health_));
	    		fight_button.setOnClickListener(new FightListener());
	    		pushButton(fight_button);
	    	}
	    	
	    	// TODO: make the name configurable
	    	// TODO: change the color?
	    	LinearLayout run_button = makeButton(R.drawable.btn_translucent_green,
    				R.drawable.run, "Run",
    				Integer.toString(game_information_.dealer_health_));
	    	run_button.setOnClickListener(new RunListener());
	    	pushButton(run_button);

	    // If there are no cops then add all the available drugs, coats, and guns as buttons
	    // first, then add the bank and loan shark if available, and finally the ever-present
	    // (unless the game is over) subway and inventory buttons.
        } else {
        	// TODO: the order of these changes sometimes, it might be nicer if the order were
        	//       more deterministic
	        Iterator<String> drug_names =
	        	game_information_.location_drugs_.keySet().iterator();
	        while (drug_names.hasNext()) {
	        	String drug_name = drug_names.next();
	        	int drug_price = game_information_.location_drugs_.get(
	        			drug_name).intValue();
				int drug_picture = drug_icons_.get(
						game_information_.drugs_.get(drug_name).get(DRUGS_ICON).intValue());
	        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_gray,
	        			drug_picture, drug_name,
	        			"$" + Integer.toString(drug_price));
	        	
	        	// Check for the buyable/sellable status of the drug and set the colors and
	        	// listeners appropriately.
	        	// TODO: make the colors configurable?
	        	boolean can_buy = ((game_information_.dealer_cash_ > drug_price) &&
	        			(game_information_.dealer_space_ > 0));
	        	boolean can_sell = (game_information_.dealer_drugs_.get(drug_name) != null);
	        	if (can_buy && can_sell) {
	        		next_drug.setBackgroundResource(R.drawable.btn_translucent_blue);
	        		next_drug.setOnClickListener(
	        				new DrugClickListener(drug_name, DIALOG_DRUG_BUY));
	        		next_drug.setOnLongClickListener(new SellDrugsLongClickListener(drug_name));
	        	} else if (can_buy) {
	        		next_drug.setBackgroundResource(R.drawable.btn_translucent_green);
	        		next_drug.setOnClickListener(
	        				new DrugClickListener(drug_name, DIALOG_DRUG_BUY));
	        	} else if (can_sell) {
	        		next_drug.setBackgroundResource(R.drawable.btn_translucent_orange);
	        		next_drug.setOnClickListener(
	        				new DrugClickListener(drug_name, DIALOG_DRUG_SELL));
	        	}
	    		pushButton(next_drug);
	        }
			
			// If the current location has access to the loan shark, display the loan shark
	        // button.
	        // TODO: the location checking is broken, can't index the set array like this
	        //       reliably
	        // TODO: change the default color to yellow
	        // TODO: make the color configurable?
	        // TODO: make the name configurable
	    	String current_location = 
	    		(String)game_information_.locations_.keySet().
	    		toArray()[game_information_.location_];
	    	if (game_information_.locations_.get(current_location).get(
	    			LOCATION_LOAN_SHARK) != null) {
				LinearLayout loan_shark_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.loan_shark, "Shark", Integer.toString(
		    					game_information_.dealer_loan_));
			    loan_shark_button.setOnClickListener(new BasicDialogListener(DIALOG_LOAN_SHARK));
	    		pushButton(loan_shark_button);
			}
			
			// If the current location has access to the bank, display the bank button.
	    	// TODO: the location checking is broken, can't index the set array like this
	    	//       reliably
	    	// TODO: change the default color to yellow
	    	// TODO: make the color configurable?
	    	// TODO: make the name configurable
	    	if (game_information_.locations_.get(current_location).get(LOCATION_BANK) != null) {
				LinearLayout bank_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, "Bank",
		    			Integer.toString(game_information_.dealer_bank_));
				bank_button.setOnClickListener(new BasicDialogListener(DIALOG_BANK_DEPOSIT));
				bank_button.setOnLongClickListener(
						new LongClickDialogListener(DIALOG_BANK_WITHDRAW));
	    		pushButton(bank_button);
			}
	        
	        // Add any coats that are available for purchase at the current location.
	    	// TODO: support multiple coats being available at once.
	    	// TODO: make the color configurable
	        if (game_information_.location_coats_.size() > 0) {
	        	String coat_name = game_information_.location_coats_.
	        			keySet().iterator().next();
	        	int coat_price = game_information_.location_coats_.get(
	        			coat_name).intValue();
				LinearLayout coat_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, coat_name, Integer.toString(coat_price));
				
				// Only indicate that it's buyable if the dealer can afford it.
				if (coat_price <= game_information_.dealer_cash_) {
					coat_button.setOnClickListener(new BuyCoatListener(coat_name));
					coat_button.setBackgroundResource(R.drawable.btn_translucent_green);
				}
	    		pushButton(coat_button);
			}
	        
	        // Add any guns that are available for purchase at the current location.
	        // TODO: support multiple guns being available at once.
	        // TODO: make the color configurable.
	        if (game_information_.location_guns_.size() > 0) {
	        	String gun_name = game_information_.location_guns_.
	        			keySet().iterator().next();
	        	int gun_price = game_information_.location_guns_.get(
	        			gun_name).intValue();
				LinearLayout gun_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, gun_name, Integer.toString(gun_price));
				
				// Only indicate that it's buyable if the dealer can afford it.
				if (gun_price <= game_information_.dealer_cash_) {
					gun_button.setOnClickListener(new BuyGunListener(gun_name));
					gun_button.setBackgroundResource(R.drawable.btn_translucent_green);
				}
	    		pushButton(gun_button);
			}
	        
			// Unless the game is over the subway button is always included.
	        // TODO: make the color configurable
	        // TODO: make the name configurable
			if (game_information_.game_days_left_ > 0) {
		    	LinearLayout subway_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.subway, "Subway",
		    			"[" + Integer.toString(
		    					game_information_.game_days_left_) + "]");
		    	subway_button.setOnClickListener(new BasicDialogListener(DIALOG_SUBWAY));
	    		pushButton(subway_button);
			}
	        
	        // The inventory button is always available no matter what.
			// TODO: factor out the human readability bit
			// TODO: make the human readability bit configurable
			// TODO: apply the human readability bit to other amounts?
			float factor = (float)1.0;
			String suffix = "";
			if (game_information_.dealer_cash_ > 1000000000) {
				factor = (float)1000000000.0;
				suffix = " Bil";
			} else if (game_information_.dealer_cash_ > 1000000) {
				factor = (float)1000000.0;
				suffix = " Mil";
			} else if (game_information_.dealer_cash_ > 1000) {
				factor = (float)1000.0;
				suffix = " G's";
			}
			String human_readable_cash =
				"$" + (new Float(game_information_.dealer_cash_ /
						factor)).intValue() + suffix;
			
			// TODO: make the color configurable
	        LinearLayout inventory_button = makeButton(R.drawable.btn_translucent_gray,
	        		R.drawable.backpack, "(" + Integer.toString(
	        				game_information_.dealer_space_) + ")",
	        		human_readable_cash);
	        inventory_button.setOnClickListener(new BasicDialogListener(DIALOG_INVENTORY));
    		pushButton(inventory_button);
    		
    		// The end of game button is visible if there are no more moves left in the game.
    		// TODO: make default color yellow
    		// TODO: make color configurable
    		if (game_information_.game_days_left_ < 1) {
    			LinearLayout end_of_game_button = makeButton(R.drawable.btn_translucent_green,
    					R.drawable.backpack, "End Game", " ");
    			end_of_game_button.setOnClickListener(new BasicDialogListener(DIALOG_END_GAME));
    			pushButton(end_of_game_button);
    		}
        }
        
        // TODO: a button to enter a new song for the server jukebox
        // TODO: a button to enter a new bit of graffiti
        // TODO: a button to enter a new message
        
        // Each time a button is added the current row is checked and if it's full it's added to
        // the base layout, but if there are buttons added at the end that row hasn't been added
        // to the base layout yet so make sure that happens here.
        if (total_width_added_ > 0) {
        	outer_layout_.addView(current_row_);
        }
        
        // The message display should open those messages appropriate to the current mode (either
        // fight or non-fight mode). Each message should be displayed at a random location on the
        // screen.
        // TODO: none of the above actually happens yet, lots of work to be done here.
        RelativeLayout message_layout = (RelativeLayout)findViewById(R.id.message_layout);
        message_layout.removeAllViews();
        if (game_information_.location_cops_ > 0) {
        	for (int i = 0; i < game_information_.fight_messages_.size(); ++i) {
            	TextView next_message = new TextView(this);
            	RelativeLayout.LayoutParams layout_params =
            		new RelativeLayout.LayoutParams(150,
            			RelativeLayout.LayoutParams.WRAP_CONTENT);
            	next_message.setBackgroundResource(R.drawable.message_background);
            	layout_params.setMargins(rand_gen_.nextInt(100), rand_gen_.nextInt(100), 0, 0);
            	next_message.setLayoutParams(layout_params);
            	next_message.setGravity(Gravity.CENTER);
            	next_message.setTextColor(Color.WHITE);
            	next_message.setText(game_information_.fight_messages_.elementAt(i));
            	next_message.setOnClickListener(
            			new HideMessageListener(game_information_.fight_messages_.elementAt(i)));
            	message_layout.addView(next_message);
        	}
        } else {
	        for (int i = 0; i < game_information_.game_messages_.size(); ++i) {
	        	TextView next_message = new TextView(this);
            	RelativeLayout.LayoutParams layout_params =
            		new RelativeLayout.LayoutParams(150,
            			RelativeLayout.LayoutParams.WRAP_CONTENT);
            	next_message.setBackgroundResource(R.drawable.message_background);
            	layout_params.setMargins(rand_gen_.nextInt(100), rand_gen_.nextInt(100), 0, 0);
	        	next_message.setLayoutParams(layout_params);
	        	next_message.setGravity(Gravity.CENTER);
	        	next_message.setTextColor(Color.WHITE);
	        	next_message.setText(game_information_.game_messages_.elementAt(i));
            	next_message.setOnClickListener(
            			new HideMessageListener(game_information_.game_messages_.elementAt(i)));
	        	message_layout.addView(next_message);
	        }
        }
        
        dealer_data_.close();
	}
	
	// When moving from one location to another (advancing one turn) this resets all the drugs
	// that are available and processes all the random events that can happen on a turn-by-turn
	// basis.
	public void setupLocation() {
		dealer_data_.open();
		game_information_.setCurrentGameInformation(
				dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
        game_information_.game_messages_.clear();
        game_information_.fight_messages_.clear();
        
        // First determine how many drugs are available at the current location.
        // TODO: This is broken, the order doesn't work like this.
        String current_location = 
        	(String)game_information_.locations_.keySet().
        	toArray()[game_information_.location_];
    	int base_drugs_count = game_information_.locations_.get(current_location).
    	    get(LOCATION_DRUGS).intValue();
    	int drug_variance = game_information_.locations_.get(current_location).
	        get(LOCATION_VARIANCE).intValue();
		int num_drugs_present = base_drugs_count + rand_gen_.nextInt(drug_variance + 1);
		if (num_drugs_present < 1) {
			num_drugs_present = 1;
		}
		
        // Now determine which of the drugs are available by making a list including all the
		// possible drugs and removing random elements until the limit is met.
		int num_avail_drugs = game_information_.drugs_.size();
		Vector<Integer> drugs_present = new Vector<Integer>();
		for (int i = 0; i < num_avail_drugs; ++i) {
			drugs_present.add(new Integer(i));
    	}
		while (drugs_present.size() > num_drugs_present) {
			int drug_to_remove = rand_gen_.nextInt(drugs_present.size());
			drugs_present.removeElementAt(drug_to_remove);
    	}
		
		// Add the drugs to the location inventory.
		game_information_.location_drugs_.clear();
		for (int i = 0; i < drugs_present.size(); ++i) {
			// TODO: this probably works okay but is technically wrong, and since the problem
			//       needs to be fixed many other places might as well fix it here too.
			String drug_name = 
				(String)(game_information_.drugs_.keySet().toArray()[drugs_present.elementAt(i)]);
			
			// This will set the price of the drug and also create messages that shown be shown
			// relating to the drug.
			Vector<String> price_and_messages = chooseDrugPrice(
					drug_name, game_information_.drugs_.get(drug_name));
			game_information_.location_drugs_.put(drug_name,
					Integer.parseInt(price_and_messages.elementAt(0)));
			for (int j = 1; j < price_and_messages.size(); ++j) {
				game_information_.game_messages_.add(
						price_and_messages.elementAt(j));
			}
		}

		// Determine if any coats are present and add them to the location inventory if they are.
		// TODO: support multiple coats
		// TODO: more price options
		game_information_.location_coats_.clear();
    	if (rand_gen_.nextDouble() < game_information_.coat_likelihood_) {
    		int coat_number = rand_gen_.nextInt(game_information_.coats_.size());
    		String coat_name = (String)game_information_.coats_.keySet().toArray()[coat_number];
    		int coat_price = (int)(game_information_.coats_.get(coat_name).get(COATS_PRICE) +
    				(rand_gen_.nextDouble() - 0.5) *
    				game_information_.coats_.get(coat_name).get(COATS_VARIANCE));
    		game_information_.location_coats_.put(coat_name, coat_price);
    	}
    	
    	// Determine if any guns are present and add them to the location inventory if they are.
    	// TODO: support multiple guns
    	// TODO: more price options
    	game_information_.location_guns_.clear();
    	if (rand_gen_.nextDouble() < game_information_.gun_likelihood_) {
    		int gun_number = rand_gen_.nextInt(game_information_.guns_.size());
    		String gun_name = (String)game_information_.guns_.keySet().toArray()[gun_number];
    		int gun_price = (int)(game_information_.guns_.get(gun_name).get(GUNS_PRICE) +
    				(rand_gen_.nextDouble() - 0.5) *
    				game_information_.guns_.get(gun_name).get(GUNS_VARIANCE));
    		game_information_.location_guns_.put(gun_name, gun_price);
    	}
    	
    	// Determine if the cops are present and initialize their health and other attributes
    	// if they are.
    	// TODO: make health and firepower less deterministic
    	// TODO: make health and firepower configurable
    	// TODO: more options for cops that just Hardass
    	// TODO: better (and non-deterministic, and configurable) messaging
    	game_information_.location_cops_ = 0;
    	if (rand_gen_.nextDouble() < game_information_.cops_likelihood_) {
    		game_information_.location_cops_ = 10 + rand_gen_.nextInt(3);
    		String fight_message = "Officer Hardass ";
    		if (game_information_.location_cops_ > 10) {
    			fight_message += "and " + 
    				Integer.toString(game_information_.location_cops_ - 10) + " deputies ";
    		}
    		fight_message += "found you!";
    		game_information_.fight_messages_.add(fight_message);
    	}
    	
    	// Save back all the information altered while setting up the new location.
        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
        		game_information_.getCurrentGameInformation());
    	dealer_data_.close();
	}

	// Given a set of drug attributes this will determine a random price within the parameters
	// of that drug. It will return a price and also other messages about the price (notification
	// messages that the drug price is unusually high or low).
	public Vector<String> chooseDrugPrice(
			String name, HashMap<String, Float> drug_attributes) {
		Vector<String> price_and_messages = new Vector<String>();
		int base_price = drug_attributes.get("base_price").intValue();
		int price_variance = drug_attributes.get("price_variance").intValue();
		int price = (int)(base_price - price_variance / 2.0 +
				rand_gen_.nextDouble() * price_variance);
		// Check for price jumps
		if (drug_attributes.get("low_probability") != null) {
			float low_probability = drug_attributes.get("low_probability");
			if (rand_gen_.nextFloat() < low_probability) {
				float multiplier = drug_attributes.get("low_multiplier");
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very low prices!");
			}
		} else if (drug_attributes.get("high_probability") != null) {
			float high_probability = drug_attributes.get("high_probability");
			if (rand_gen_.nextFloat() < high_probability) {
				float multiplier = drug_attributes.get("high_multiplier");
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very high prices!");
			}
		}
		price_and_messages.insertElementAt(Integer.toString(price), 0);
		return price_and_messages;
	}
	
	// A convenience function for creating a text view, since lots of text views get created
	// when making buttons and inventory/statistics views.
	// TODO: re-consider the design of these UI-helper functions.
	private TextView constructTextView(int color, float size, int gravity, int width, int height,
			float weight, String text) {
		TextView t = new TextView(this);
		t.setTextColor(color);
		t.setTextSize(size);
		t.setGravity(gravity);
		t.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
		t.setText(text);
		return t;
	}
	
	// Special convenience function for a part of the inventory dialog.
	// TODO: make parameters customizable?
	private TextView makeInventoryHeader(String text) {
		return constructTextView(Color.WHITE, (float)16.0, Gravity.CENTER,
				LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT, (float)1.0, text);
	}
	
	// Special convenience function for a part of the inventory dialog.
	// TODO: make parameters customizable?
	private TextView makeInventoryText(int color, int gravity, String text) {
		return constructTextView(color, (float)12.0, gravity,
				LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT, (float)1.0, text);
	}
	
	// Special convenience function for a part of the inventory dialog.
	// TODO: make parameters customizable?
	private LinearLayout makeInventoryRow(int quantity_color, String item_text,
			String quantity_text) {
    	LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.HORIZONTAL);
		l.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		l.addView(makeInventoryText(Color.WHITE, Gravity.LEFT, item_text));
		l.addView(makeInventoryText(quantity_color, Gravity.RIGHT, quantity_text));
		return l;
	}

	// Make sure all necessary dialogs exist, and set the base layout for all of them.
	// This is done instead of checking each dialog for initialization separately.
	// TODO: reconsider this function, maybe they should be handled more independently.
	private void initDialogs() {
		if (subway_dialog_ == null) {
			subway_dialog_ = new Dialog(this);
    		subway_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		subway_dialog_.setContentView(R.layout.subway_layout);
		}
		if (inventory_dialog_ == null) {
    		inventory_dialog_ = new Dialog(this);
    		inventory_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		inventory_dialog_.setContentView(R.layout.inventory_layout);
    	}
    	if (drug_buy_dialog_ == null) {
    		drug_buy_dialog_ = new Dialog(this);
    		drug_buy_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		drug_buy_dialog_.setContentView(R.layout.drug_buy_layout);
            ((SeekBar)drug_buy_dialog_.findViewById(R.id.drug_quantity_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).setText(
    						Integer.toString(progress));
    			}
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
    	if (drug_sell_dialog_ == null) {
    		drug_sell_dialog_ = new Dialog(this);
    		drug_sell_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		drug_sell_dialog_.setContentView(R.layout.drug_sell_layout);
            ((SeekBar)drug_sell_dialog_.findViewById(R.id.drug_quantity_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).setText(
    						Integer.toString(progress));
    			}
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
    	if (loan_shark_dialog_ == null) {
    		loan_shark_dialog_ = new Dialog(this);
    		loan_shark_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		loan_shark_dialog_.setContentView(R.layout.loan_shark_layout);
            ((SeekBar)loan_shark_dialog_.findViewById(R.id.loan_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)loan_shark_dialog_.findViewById(R.id.loan_amount)).setText(
    						Integer.toString(progress));
    			}
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
    	if (bank_deposit_dialog_ == null) {
    		bank_deposit_dialog_ = new Dialog(this);
    		bank_deposit_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		bank_deposit_dialog_.setContentView(R.layout.bank_deposit_layout);
            ((SeekBar)bank_deposit_dialog_.findViewById(R.id.bank_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)bank_deposit_dialog_.findViewById(R.id.bank_amount)).setText(
    						Integer.toString(progress));
    			}
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
    	if (bank_withdraw_dialog_ == null) {
    		bank_withdraw_dialog_ = new Dialog(this);
    		bank_withdraw_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		bank_withdraw_dialog_.setContentView(R.layout.bank_withdraw_layout);
            ((SeekBar)bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			@Override
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)bank_withdraw_dialog_.findViewById(R.id.bank_amount)).setText(
    						Integer.toString(progress));
    			}
    			@Override
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			@Override
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
    	if (end_game_dialog_ == null) {
    		end_game_dialog_ = new Dialog(this);
    		end_game_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		end_game_dialog_.setContentView(R.layout.end_of_game_layout);
    	}
	}
	
	// Initialize the array of available icons.
	// TODO: would be nice to have one of these for button drawables and colors?
	public void loadIcons() {
		drug_icons_ = new HashMap<Integer,Integer>();
		drug_icons_.put(0, R.drawable.weed);
		drug_icons_.put(1, R.drawable.acid);
		drug_icons_.put(2, R.drawable.ludes);
		drug_icons_.put(3, R.drawable.heroin);
		drug_icons_.put(4, R.drawable.cocaine);
		drug_icons_.put(5, R.drawable.shrooms);
		drug_icons_.put(6, R.drawable.speed);
		drug_icons_.put(7, R.drawable.hashish);
	}
	
	Dialog subway_dialog_;
	Dialog drug_buy_dialog_;
	Dialog drug_sell_dialog_;
	Dialog inventory_dialog_;
	Dialog loan_shark_dialog_;
	Dialog bank_deposit_dialog_;
	Dialog bank_withdraw_dialog_;
	Dialog end_game_dialog_;
	
	public static HashMap<Integer,Integer> drug_icons_;

	String dialog_drug_name_;
	
	DealerDataAdapter dealer_data_;
	GameInformation game_information_;
	
	int viewWidth_;
    int viewHeight_;
	LinearLayout outer_layout_;
	LinearLayout current_row_;
	int total_width_added_;
	
	// Random number generator for this activity.
    public static Random rand_gen_ = new Random();
}
