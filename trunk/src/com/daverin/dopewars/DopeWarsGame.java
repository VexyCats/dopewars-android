package com.daverin.dopewars;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
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
	public static final int DIALOG_SUBWAY = 1002;
	public static final int DIALOG_DRUG_BUY = 1003;
	public static final int DIALOG_INVENTORY = 1004;
	public static final int DIALOG_DRUG_SELL = 1005;
	public static final int DIALOG_LOAN_SHARK = 1006;
	public static final int DIALOG_BANK_DEPOSIT = 1007;
	public static final int DIALOG_BANK_WITHDRAW = 1008;
	
	// Respond to a click that just opens a dialog with no additional information.
	public class BasicDialogListener implements View.OnClickListener {
		public BasicDialogListener(int dialog_id) {
			dialog_id_ = dialog_id;
		}
		public void onClick(View v) {
			showDialog(dialog_id_);
		}
		int dialog_id_;
	}
	
	// Respond to a long click that just opens a dialog with no additional information.
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
	
	// Respond to a click that needs a drug to operate on.
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
	
	// Respond to a long click that needs a drug to operate on.
	public class DrugLongClickListener implements View.OnLongClickListener {
		public DrugLongClickListener(String drug_name, int dialog_id) {
			drug_name_ = drug_name;
			dialog_id_ = dialog_id;
		}
		public boolean onLongClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(dialog_id_);
			return true;
		}
		String drug_name_;
		int dialog_id_;
	}
	
	public class FightListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        GameInformation game_information = new GameInformation(
	        		dealer_data_.getGameStrings());
	        int total_dealer_firepower = 0;
	        Iterator<String> gun_names = game_info.dealer_guns_.keySet().iterator();
	        while (gun_names.hasNext()) {
	        	String next_gun_name = gun_names.next();
	        	total_dealer_firepower += game_information.guns_.get(next_gun_name).get("firepower") * game_info.dealer_guns_.get(next_gun_name);
	        }
	        if (total_dealer_firepower > game_info.cops_health_) {
	        	game_info.cops_health_ = 0;
	        	game_info.cash_ += Global.rand_gen_.nextInt(5) + 1 * 1000;
	        } else {
	        	game_info.dealer_health_ -= 6 + Math.max(0, (game_info.cops_health_ - 10));
	        	game_info.cops_health_ -= total_dealer_firepower;
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}
	
	public class RunListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        if (Global.rand_gen_.nextDouble() < 0.5) {
	        	game_info.cash_ = Math.max(0, game_info.cash_ - Global.rand_gen_.nextInt(5) + 1 * 1000);
	        	game_info.cops_health_ = 0;
	        } else {
	        	game_info.dealer_health_ -= 6 + Math.max(0, (game_info.cops_health_ - 10));
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}
	public class BuyCoatListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        GameInformation game_information = new GameInformation(
	        		dealer_data_.getGameStrings());
	        String coat_name = game_info.coat_inventory_.keySet().iterator().next();
	        int coat_price = game_info.coat_inventory_.get(coat_name).intValue();
	        game_info.cash_ -= coat_price;
	        game_info.max_space_ += game_information.coats_.get(coat_name).get("additional_space");
	        game_info.space_ += game_information.coats_.get(coat_name).get("additional_space");
	        game_info.coat_inventory_.clear();
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}
	
	public class BuyGunListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        GameInformation game_information = new GameInformation(
	        		dealer_data_.getGameStrings());
	        String gun_name = game_info.gun_inventory_.keySet().iterator().next();
	        int gun_price = game_info.gun_inventory_.get(gun_name).intValue();
	        game_info.cash_ -= gun_price;
	        game_info.space_ -= game_information.guns_.get(gun_name).get("space");
	        if (game_info.dealer_guns_.get(gun_name) != null) {
	        	game_info.dealer_guns_.put(gun_name, game_info.dealer_guns_.get(gun_name) + 1);
	        } else {
	        	game_info.dealer_guns_.put(gun_name, (float)1);
	        }
	        game_info.gun_inventory_.clear();
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
		}
	}
	
	public class CompleteSaleListener implements View.OnClickListener {
		public CompleteSaleListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public void onClick(View v) {
	        dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int drug_price = game_info.location_inventory_.get(drug_name_).intValue();
	        int drug_quantity = Integer.parseInt(((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).getText().toString());
	        game_info.cash_ = game_info.cash_ - drug_quantity * drug_price;
	        game_info.space_ = game_info.space_ - drug_quantity;
	        if (game_info.dealer_inventory_.get(drug_name_) != null) {
	        	game_info.dealer_inventory_.put(drug_name_, game_info.dealer_inventory_.get(drug_name_) + drug_quantity);
	        } else {
	        	game_info.dealer_inventory_.put(drug_name_, (float)drug_quantity);
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_BUY);
		}
		String drug_name_;
	}
	

	public class CompleteBuyListener implements View.OnClickListener {
		public CompleteBuyListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public void onClick(View v) {
	        dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int drug_price = game_info.location_inventory_.get(drug_name_).intValue();
	        int drug_quantity = Integer.parseInt(((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).getText().toString());
	        game_info.cash_ = game_info.cash_ + drug_quantity * drug_price;
	        game_info.space_ = Math.min(game_info.max_space_, game_info.space_ + drug_quantity);
	        if (game_info.dealer_inventory_.get(drug_name_) > drug_quantity) {
	        	game_info.dealer_inventory_.put(drug_name_, game_info.dealer_inventory_.get(drug_name_) - drug_quantity);
	        } else {
	        	game_info.dealer_inventory_.remove(drug_name_);
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_SELL);
		}
		String drug_name_;
	}
	
	public class CompleteLoanListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int loan_payment = Integer.parseInt(((TextView)loan_shark_dialog_.findViewById(R.id.loan_amount)).getText().toString());
	        game_info.loan_ -= loan_payment;
	        game_info.cash_ -= loan_payment;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_LOAN_SHARK);
		}
	}
	
	public class CompleteBankDepositListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int bank_deposit = Integer.parseInt(((TextView)bank_deposit_dialog_.findViewById(R.id.bank_amount)).getText().toString());
	        game_info.bank_ += bank_deposit;
	        game_info.cash_ -= bank_deposit;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_BANK_DEPOSIT);
		}
	}
	
	public class CompleteBankWithdrawListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        int bank_withdrawal = Integer.parseInt(((TextView)bank_withdraw_dialog_.findViewById(R.id.bank_amount)).getText().toString());
	        game_info.bank_ -= bank_withdrawal;
	        game_info.cash_ += bank_withdrawal;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
			dealer_data_.close();
			refreshDisplay();
			dismissDialog(DIALOG_BANK_DEPOSIT);
		}
	}
	
	// Response to a change in location.
	public class ChangeLocationListener implements View.OnClickListener {
		public ChangeLocationListener(int new_location) {
			location_ = new_location;
		}
		public void onClick(View v) {
	        dealer_data_.open();
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        GameInformation game_information = new GameInformation(
	        		dealer_data_.getGameStrings());
	        game_info.location_ = location_;
	        game_info.days_left_ -= 1;
	        game_info.loan_ += (int)((double)game_info.loan_ * game_information.loan_interest_rate_);
	        game_info.bank_ += (int)((double)game_info.bank_ * game_information.bank_interest_rate_);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
	        dealer_data_.close();
			setupLocation();
			refreshDisplay();
			dismissDialog(DIALOG_SUBWAY);
		}
		int location_;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dealer_data_ = new DealerDataAdapter(this);
        dealer_data_.open();
        String game_info = dealer_data_.getGameStrings();
        dealer_data_.close();
        game_information_ = new GameInformation(game_info);
        
        // Set up the main content of the view.
        setContentView(R.layout.main_game_screen);
        
        setupLocation();
        refreshDisplay();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
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
		}
        return super.onCreateDialog(id);
    }
	
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
	
	private TextView makeInventoryHeader(String text) {
		return constructTextView(Color.WHITE, (float)16.0, Gravity.CENTER,
				LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT, (float)1.0, text);
	}
	
	private TextView makeInventoryText(int color, int gravity, String text) {
		return constructTextView(color, (float)12.0, gravity,
				LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT, (float)1.0, text);
	}
	
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
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
		dealer_data_.open();
        if(id == DIALOG_SUBWAY) {
        	RelativeLayout l = (RelativeLayout)subway_dialog_.findViewById(R.id.subway_button_layout);
        	l.removeAllViews();
        	int num_locations = game_information_.locations_.size();
	        for (int i = 0; i < num_locations; ++i) {
	        	String location_name = (String)(game_information_.locations_.keySet().toArray()[i]);
	        	Button b = new Button(this);
	        	RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(90, 30);
	        	layout_params.leftMargin = game_information_.locations_.get(location_name).get("map_x").intValue();
	        	layout_params.topMargin = game_information_.locations_.get(location_name).get("map_y").intValue();
	        	b.setLayoutParams(layout_params);
	        	b.setText(location_name);
	        	b.setOnClickListener(new ChangeLocationListener(i));
	        	l.addView(b);
	        }
        	// this may not have to do anything other than the width setting
        	WindowManager.LayoutParams dialog_params =
        		subway_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	subway_dialog_.getWindow().setAttributes(dialog_params);
        } else if (id == DIALOG_INVENTORY) {
        	LinearLayout l = (LinearLayout)inventory_dialog_.findViewById(R.id.inventory_layout);
        	l.setGravity(Gravity.CENTER);
        	l.removeAllViews();
        	l.addView(makeInventoryHeader("drugs"));
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        if (game_info.dealer_inventory_.size() == 0) {
	        	l.addView(makeInventoryText(Color.GRAY, Gravity.CENTER, "(none)"));
	        } else {
		        Iterator<String> drug_names = game_info.dealer_inventory_.keySet().iterator();
		        while (drug_names.hasNext()) {
		        	String drug_string = drug_names.next();
		        	l.addView(makeInventoryRow(Color.BLUE, drug_string,
		        			game_info.dealer_inventory_.get(drug_string).toString()));
		        }
	        }
	        l.addView(makeInventoryHeader("guns"));
        	if (game_info.dealer_guns_.size() == 0) {
	        	l.addView(makeInventoryText(Color.GRAY, Gravity.CENTER, "(none)"));
	        } else {
		        Iterator<String> gun_names = game_info.dealer_guns_.keySet().iterator();
		        while (gun_names.hasNext()) {
		        	String gun_string = gun_names.next();
		        	l.addView(makeInventoryRow(Color.RED, gun_string,
		        			game_info.dealer_guns_.get(gun_string).toString()));
		        }
	        }
	        l.addView(makeInventoryHeader("money"));
	        l.addView(makeInventoryRow(Color.GREEN, "In hand",
	        		Integer.toString(game_info.cash_)));
	        l.addView(makeInventoryRow(Color.GREEN, "In bank",
	        		Integer.toString(game_info.bank_)));
	        l.addView(makeInventoryRow(Color.GREEN, "Owed",
	        		"(" + Integer.toString(game_info.loan_) + ")"));
	        l.addView(makeInventoryRow(Color.GREEN, "Total",
	        		Integer.toString(game_info.cash_ + game_info.bank_ - game_info.loan_)));
	        l.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	    			dismissDialog(DIALOG_INVENTORY);
	        	}
	        });
        	
        	// this may not have to do anything other than the width setting
        	WindowManager.LayoutParams dialog_params =
        		inventory_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	inventory_dialog_.getWindow().setAttributes(dialog_params);
        } else if (id == DIALOG_DRUG_BUY) {
        	// Determine how many of the drug the user could buy
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_name))).setText("Buy " + dialog_drug_name_);
	        float drug_price = game_info.location_inventory_.get(dialog_drug_name_);
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_price))).setText("$" + Float.toString(drug_price));
	        int max_num_drugs = (int)(game_info.cash_ / game_info.location_inventory_.get(dialog_drug_name_));
	        max_num_drugs = Math.min(max_num_drugs, game_info.space_);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(max_num_drugs);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(max_num_drugs);
	        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_quantity))).setText(Integer.toString(max_num_drugs));
	        ((LinearLayout)drug_buy_dialog_.findViewById(R.id.drug_buy_layout)).setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_DRUG_BUY);
	        	}
	        });
	        ((ImageView)(drug_buy_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new CompleteSaleListener(dialog_drug_name_));
	        drug_buy_dialog_.findViewById(R.id.drug_quantity_slide).invalidate();
        } else if (id == DIALOG_DRUG_SELL) {
        	// Determine how many of the drug the user could sell
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_name))).setText("Sell " + dialog_drug_name_);
	        float drug_price = game_info.location_inventory_.get(dialog_drug_name_);
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_price))).setText("$" + Float.toString(drug_price));
	        int drug_quantity = game_info.dealer_inventory_.get(dialog_drug_name_).intValue();
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(drug_quantity);
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(drug_quantity);
	        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_quantity))).setText(Integer.toString(drug_quantity));
	        ((LinearLayout)drug_sell_dialog_.findViewById(R.id.drug_sell_layout)).setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_DRUG_SELL);
	        	}
	        });
	        ((ImageView)(drug_sell_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new CompleteBuyListener(dialog_drug_name_));
        } else if (id == DIALOG_LOAN_SHARK) {
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
        	int max_loan = Math.min(game_info.cash_, game_info.loan_);
	        ((SeekBar)(loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setMax(max_loan);
	        ((SeekBar)(loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setProgress(max_loan);
	        ((TextView)(loan_shark_dialog_.findViewById(R.id.loan_amount))).setText(Integer.toString(max_loan));
	        ((LinearLayout)loan_shark_dialog_.findViewById(R.id.loan_shark_layout)).setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_LOAN_SHARK);
	        	}
	        });
	        ((ImageView)(loan_shark_dialog_.findViewById(R.id.loan_icon))).setOnClickListener(
	        		new CompleteLoanListener());
        } else if (id == DIALOG_BANK_DEPOSIT) {
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setMax(game_info.cash_);
	        ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(game_info.cash_);
	        ((TextView)(bank_deposit_dialog_.findViewById(R.id.bank_amount))).setText(Integer.toString(game_info.cash_));
	        ((LinearLayout)bank_deposit_dialog_.findViewById(R.id.bank_deposit_layout)).setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_BANK_DEPOSIT);
	        	}
	        });
	        ((ImageView)(bank_deposit_dialog_.findViewById(R.id.bank_icon))).setOnClickListener(
	        		new CompleteBankDepositListener());
        } else if (id == DIALOG_BANK_WITHDRAW) {
	        CurrentGameInformation game_info = new CurrentGameInformation(
	        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
	        ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setMax(game_info.bank_);
	        ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(game_info.bank_);
	        ((TextView)(bank_withdraw_dialog_.findViewById(R.id.bank_amount))).setText(Integer.toString(game_info.bank_));
	        ((LinearLayout)bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_layout)).setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		dismissDialog(DIALOG_BANK_WITHDRAW);
	        	}
	        });
	        ((ImageView)(bank_withdraw_dialog_.findViewById(R.id.bank_icon))).setOnClickListener(
	        		new CompleteBankWithdrawListener());
        }
        dealer_data_.close();
    }
	
	public LinearLayout makeButton(int background_resource,
			int image_resource, String main_string, String secondary_string) {
		LinearLayout new_button = new LinearLayout(this);
		new_button.setOrientation(LinearLayout.VERTICAL);
		new_button.setGravity(Gravity.CENTER_HORIZONTAL);
		new_button.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		new_button.setBackgroundResource(background_resource);
    	ImageView button_image = new ImageView(this);
    	button_image.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
    	button_image.setImageResource(image_resource);
    	button_image.setScaleType(ScaleType.FIT_CENTER);
    	new_button.addView(button_image);
    	TextView main_text = new TextView(this);
    	main_text.setLayoutParams(new LinearLayout.LayoutParams(
    			48,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	main_text.setGravity(Gravity.CENTER);
    	main_text.setHorizontallyScrolling(true);
    	main_text.setTextColor(Color.WHITE);
    	main_text.setText(main_string);
    	new_button.addView(main_text);
    	TextView secondary_text = new TextView(this);
    	secondary_text.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	secondary_text.setHorizontallyScrolling(true);
    	secondary_text.setTextColor(Color.GREEN);
    	secondary_text.setText(secondary_string);
    	new_button.addView(secondary_text);
    	
    	return new_button;
	}
	
	public void pushButton(LinearLayout button) {
		button.measure(viewWidth_, viewHeight_);
		if (button.getMeasuredWidth() + total_width_added_ > viewWidth_) {
			outer_layout_.addView(current_row_);
    		current_row_ = new LinearLayout(this);
    		current_row_.setOrientation(LinearLayout.HORIZONTAL);
    		current_row_.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.FILL_PARENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
    		total_width_added_ = 0;
		}
    	total_width_added_ += button.getMeasuredWidth();
    	current_row_.addView(button);
	}
	
	public void refreshDisplay() {
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
        CurrentGameInformation game_info = new CurrentGameInformation(
        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
        GameInformation game_information = new GameInformation(
        		dealer_data_.getGameStrings());
        
        // First check if there are cops. If there are cops don't show anything other than the
        // fight related buttons and info.
        if (game_info.cops_health_ > 0) {
        	LinearLayout hardass_button = makeButton(R.drawable.btn_translucent_gray,
        			R.drawable.loan_shark, "Hardass",
        			Integer.toString(Math.min(10, game_info.cops_health_)));
        	pushButton(hardass_button);
	    	
	    	if (game_info.cops_health_ > 10) {
	    		LinearLayout deputies_button = makeButton(R.drawable.btn_translucent_gray,
	    				R.drawable.loan_shark, "Deputies",
	    				Integer.toString(game_info.cops_health_ - 10));
	    		pushButton(deputies_button);
	    	}
	    	
	    	if (game_info.dealer_guns_.size() > 0) {
	    		LinearLayout fight_button = makeButton(R.drawable.btn_translucent_green,
	    				R.drawable.loan_shark, "Fight",
	    				Integer.toString(game_info.dealer_health_));
	    		fight_button.setOnClickListener(new FightListener());
	    		pushButton(fight_button);
	    	}
	    	
	    	LinearLayout run_button = makeButton(R.drawable.btn_translucent_green,
    				R.drawable.loan_shark, "Run",
    				Integer.toString(game_info.dealer_health_));
	    	run_button.setOnClickListener(new RunListener());
	    	pushButton(run_button);
        } else {
	        Iterator<String> drug_names = game_info.location_inventory_.keySet().iterator();
	        while (drug_names.hasNext()) {
	        	String drug_name = drug_names.next();
	        	int drug_price = game_info.location_inventory_.get(drug_name).intValue();
				int drug_picture = Global.drug_icons_.get(game_information_.drugs_.get(drug_name).get("icon").intValue());
	        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_gray,
	        			drug_picture, drug_name,
	        			"$" + Integer.toString(drug_price));
	        	// Can buy
	        	if ((game_info.cash_ > drug_price) && (game_info.space_ > 0)) {
	        		// Can also sell
	        		if (game_info.dealer_inventory_.get(drug_name) != null) {
	        		  next_drug.setBackgroundResource(R.drawable.btn_translucent_blue);
	        		  next_drug.setOnLongClickListener(new DrugLongClickListener(drug_name, DIALOG_DRUG_SELL));
	        		// Can only buy
	        		} else {
	        			next_drug.setBackgroundResource(R.drawable.btn_translucent_green);
	        		}
	        		next_drug.setOnClickListener(new DrugClickListener(drug_name, DIALOG_DRUG_BUY));
	        	// Can only sell
	        	} else if (game_info.dealer_inventory_.get(drug_name) != null) {
	        	  next_drug.setBackgroundResource(R.drawable.btn_translucent_orange);
	      		  next_drug.setOnClickListener(new DrugClickListener(drug_name, DIALOG_DRUG_SELL));
	        	}
	    		pushButton(next_drug);
	        }
			
			// Add loan shark button
	        if (game_info.location_ == game_information.loan_location_) {
				LinearLayout loan_shark_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.loan_shark, "Shark", Integer.toString(game_info.loan_));
			    loan_shark_button.setOnClickListener(new BasicDialogListener(DIALOG_LOAN_SHARK));
	    		pushButton(loan_shark_button);
			}
			
			// Add bank button
	        if (game_info.location_ == game_information.bank_location_) {
				LinearLayout bank_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, "Bank", Integer.toString(game_info.bank_));
				bank_button.setOnClickListener(new BasicDialogListener(DIALOG_BANK_DEPOSIT));
				bank_button.setOnLongClickListener(new LongClickDialogListener(DIALOG_BANK_WITHDRAW));
	    		pushButton(bank_button);
			}
	        
	        // Add coat button
	        if (game_info.coat_inventory_.size() > 0) {
	        	String coat_name = game_info.coat_inventory_.keySet().iterator().next();
	        	int coat_price = game_info.coat_inventory_.get(coat_name).intValue();
				LinearLayout coat_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, coat_name, Integer.toString(coat_price));
				if (coat_price <= game_info.cash_) {
					coat_button.setOnClickListener(new BuyCoatListener());
					coat_button.setBackgroundResource(R.drawable.btn_translucent_green);
				}
	    		pushButton(coat_button);
			}
	        
	        // Add gun button
	        if (game_info.gun_inventory_.size() > 0) {
	        	String gun_name = game_info.gun_inventory_.keySet().iterator().next();
	        	int gun_price = game_info.gun_inventory_.get(gun_name).intValue();
				LinearLayout gun_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.bank, gun_name, Integer.toString(gun_price));
				if (gun_price <= game_info.cash_) {
					gun_button.setOnClickListener(new BuyGunListener());
					gun_button.setBackgroundResource(R.drawable.btn_translucent_green);
				}
	    		pushButton(gun_button);
			}
	        
			// Add subway button
			if (game_info.days_left_ > 0) {
		    	LinearLayout subway_button = makeButton(R.drawable.btn_translucent_gray,
		    			R.drawable.subway, "Subway", "[" + Integer.toString(game_info.days_left_) + "]");
		    	subway_button.setOnClickListener(new BasicDialogListener(DIALOG_SUBWAY));
	    		pushButton(subway_button);
			}
	        
	        // Add inventory button
			float factor = (float)1.0;
			String suffix = "";
			if (game_info.cash_ > 1000000000) {
				factor = (float)1000000000.0;
				suffix = " Bil";
			} else if (game_info.cash_ > 1000000) {
				factor = (float)1000000.0;
				suffix = " Mil";
			} else if (game_info.cash_ > 1000) {
				factor = (float)1000.0;
				suffix = " G's";
			}
			String human_readable_cash = "$" + (new Float(game_info.cash_ / factor)).intValue() + suffix;
	        LinearLayout inventory_button = makeButton(R.drawable.btn_translucent_gray,
	        		R.drawable.backpack, "(" + Integer.toString(game_info.space_) + ")",
	        		human_readable_cash);
	        inventory_button.setOnClickListener(new BasicDialogListener(DIALOG_INVENTORY));
    		pushButton(inventory_button);
        }
        if (total_width_added_ > 0) {
        	outer_layout_.addView(current_row_);
        }
        
        // Now display any messages that need to be shown
        LinearLayout message_layout = (LinearLayout)findViewById(R.id.message_layout);
        message_layout.removeAllViews();
        Iterator<String> messages = game_info.messages_.keySet().iterator();
        while (messages.hasNext()) {
        	String message_text = messages.next();
        	TextView next_message = new TextView(this);
        	next_message.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.WRAP_CONTENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
        	next_message.setGravity(Gravity.CENTER);
        	next_message.setTextColor(Color.GREEN);
        	next_message.setText(message_text);
        	message_layout.addView(next_message);
        }
        
        dealer_data_.close();
	}
	
	public void setupLocation() {
		// Determine the number and type of drugs available at the current location.
		dealer_data_.open();
        CurrentGameInformation game_info = new CurrentGameInformation(
        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
        game_info.messages_.clear();
        
		int num_avail_drugs = game_information_.drugs_.size();
		boolean[] drug_present = new boolean[num_avail_drugs];
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		drug_present[i] = false;
    	}
    	int num_drugs_left = num_avail_drugs;
    	String current_location = (String)game_information_.locations_.keySet().toArray()[game_info.location_];
    	int base_drugs_count = game_information_.locations_.get(current_location).
    	    get("base_drugs").intValue();
    	int drug_variance = game_information_.locations_.get(current_location).
	        get("drug_variance").intValue();
		int num_drugs_present = base_drugs_count + Global.rand_gen_.nextInt(drug_variance + 1);
		if (num_drugs_present > num_avail_drugs) {
			num_drugs_present = num_avail_drugs;
		}
		if (num_drugs_present < 1) {
			num_drugs_present = 1;
		}
    	while (num_drugs_left > num_avail_drugs - num_drugs_present) {
    		int next_drug = Global.rand_gen_.nextInt(num_drugs_left);
    		int drug_number = 0;
    		int drugs_skipped = 0;
    		while (drugs_skipped < next_drug) {
    			if (!drug_present[drug_number]) {
    				++drugs_skipped;
    			}
    			++drug_number;
    		}
    		while (drug_present[drug_number]) {
    			++drug_number;
    		}
    		drug_present[drug_number] = true;
    		--num_drugs_left;
    	}
    	
    	game_info.location_inventory_.clear();
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		if (drug_present[i]) {
    			String drug_name = (String)(game_information_.drugs_.keySet().toArray()[i]);
    			Vector<String> price_and_messages = Global.chooseDrugPrice(
    					drug_name, game_information_.drugs_.get(drug_name));
    			game_info.location_inventory_.put(drug_name, Float.parseFloat(price_and_messages.elementAt(0)));
    			for (int j = 1; j < price_and_messages.size(); ++j) {
    				game_info.messages_.put(price_and_messages.elementAt(j), (float)1.0);
    			}
    		}
    	}
    	
    	game_info.coat_inventory_.clear();
    	if (Global.rand_gen_.nextDouble() < game_information_.coat_likelihood_) {
    		int coat_number = Global.rand_gen_.nextInt(game_information_.coats_.size());
    		String coat_name = (String)game_information_.coats_.keySet().toArray()[coat_number];
    		int coat_price = (int)(game_information_.coats_.get(coat_name).get("base_price") +
    				(Global.rand_gen_.nextDouble() - 0.5) *
    				game_information_.coats_.get(coat_name).get("price_variance"));
    		game_info.coat_inventory_.put(coat_name, (float)coat_price);
    	}
    	
    	game_info.gun_inventory_.clear();
    	if (Global.rand_gen_.nextDouble() < game_information_.gun_likelihood_) {
    		int gun_number = Global.rand_gen_.nextInt(game_information_.guns_.size());
    		String gun_name = (String)game_information_.guns_.keySet().toArray()[gun_number];
    		int gun_price = (int)(game_information_.guns_.get(gun_name).get("base_price") +
    				(Global.rand_gen_.nextDouble() - 0.5) *
    				game_information_.guns_.get(gun_name).get("price_variance"));
    		game_info.gun_inventory_.put(gun_name, (float)gun_price);
    	}
    	
    	// TODO: This is weak and needs more, but for now hardass has 10 health and each deputy has 1,
    	// and Hardass has 1 firepower and each deputy has 1.
    	game_info.cops_health_ = 0;
    	if (Global.rand_gen_.nextDouble() < game_information_.cops_likelihood_) {
    		game_info.cops_health_ = 10 + Global.rand_gen_.nextInt(3);
    	}
    	
        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, game_info.serializeCurrentGameInformation());
    	dealer_data_.close();
	}
	
	Dialog subway_dialog_;
	Dialog drug_buy_dialog_;
	Dialog drug_sell_dialog_;
	Dialog inventory_dialog_;
	Dialog loan_shark_dialog_;
	Dialog bank_deposit_dialog_;
	Dialog bank_withdraw_dialog_;
	
	String dialog_drug_name_;
	
	DealerDataAdapter dealer_data_;
	

	// This function is at the end because it's gross. There is lots of replicated stuff here for
	// no good reason at all.
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
	}
	
	GameInformation game_information_;
	
	// TEMP VARIABLES UGLY CODE WARNING (but damn they're convenient)
	int viewWidth_;
    int viewHeight_;
	LinearLayout outer_layout_;
	LinearLayout current_row_;
	int total_width_added_;
}
