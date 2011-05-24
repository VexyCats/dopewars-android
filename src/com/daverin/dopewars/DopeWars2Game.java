package com.daverin.dopewars;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class DopeWars2Game extends Activity {

	// When the dialog is created, there's not much to do other than set the
	// view to the main game screen layout.  onCreate doesn't have to do
	// any game state setup because onResume does that, and onResume always
	// follows onCreate.
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game_screen);
        ((Button)findViewById(R.id.subway_button)).setOnClickListener(
        		new SubwayClickListener());
        ((Button)findViewById(R.id.bank_deposit_button)).setOnClickListener(
        		new BankDepositListener());
        ((Button)findViewById(R.id.bank_withdraw_button)).setOnClickListener(
        		new BankWithdrawListener());
        ((Button)findViewById(R.id.loan_shark_button)).setOnClickListener(
        		new PayLoanSharkClickListener());
    }

	// onResume is the key method for restoring saved data.  This will go to
	// the local game database and retrieve the stored game data to use with
	// the game state, then refresh the display according to that game state.
	@Override
	public void onResume() {
		super.onResume();
		game_state_ = getGameState();
        refreshDisplay();
	}

	// onPause is the key method for saving game data against phone actions.
	// As soon as anything causes the process to pause (turning the phone
	// screen off, going to the home view, etc), the onPause method will
	// save out the current game state.
	//
	// TODO: with enough saving on data changes, is this even necessary?
	@Override
	public void onPause() {
		super.onPause();
		saveGameState();
	}

	// This is to handle view refresh when the phone's orientation is changed.
	//
	// TODO: this may not end up being necessary, depending on the final
	//       decisions about view
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshDisplay();
	}
	
	// The game relies a lot on dialogs to interact with the user, so the
	// handler to create dialogs is important.  Each dialog in the game has
	// its own initialization function.
	@Override
    protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_DRUG_BUY:
			return GetDrugBuyDialog();
		case DIALOG_DRUG_SELL:
			return GetDrugSellDialog();
		case DIALOG_SUBWAY:
			return GetSubwayDialog();
		case DIALOG_PAY_LOAN_SHARK:
			return GetPayLoanSharkDialog();
		case DIALOG_BANK_DEPOSIT:
			return GetBankDepositDialog();
		case DIALOG_BANK_WITHDRAW:
			return GetBankWithdrawDialog();
		case DIALOG_HARDASS:
			return GetHardassDialog();
		}
			
        return super.onCreateDialog(id);
	}
	
	// The game relies a lot on dialogs to interact with the user, so the
	// handler to prepare dialogs is important.  Each dialog has its own
	// preparation function.
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
		switch(id) {
		case DIALOG_SUBWAY:
			PrepareSubwayDialog();
	        break;
		case DIALOG_DRUG_BUY:
			PrepareDrugBuyDialog();
	        break;
		case DIALOG_DRUG_SELL:
			PrepareDrugSellDialog();
	        break;
		case DIALOG_PAY_LOAN_SHARK:
			PreparePayLoanSharkDialog();
			break;
		case DIALOG_BANK_DEPOSIT:
			PrepareBankDepositDialog();
			break;
		case DIALOG_BANK_WITHDRAW:
			PrepareBankWithdrawDialog();
		case DIALOG_HARDASS:
			PrepareHardassDialog();
		}
	}
	
	// TODO: The drug buy dialog is under development.
	private Dialog GetDrugBuyDialog() {
		if (drug_buy_dialog_ == null) {
    		drug_buy_dialog_ = new Dialog(this);
    		drug_buy_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		drug_buy_dialog_.setContentView(R.layout.drug_buy_layout);
            ((SeekBar)drug_buy_dialog_.findViewById(R.id.drug_quantity_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).setText(
    						Integer.toString(progress));
    			}
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
		return drug_buy_dialog_;
	}
	
	// TODO: The drug buy dialog is under development.
	private void PrepareDrugBuyDialog() {
        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_name))).setText(
        		"Buy " + game_state_.drugs_.elementAt(dialog_drug_index_).drug_name_);
        float drug_buy_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_price))).setText(
        		"$" + Float.toString(drug_buy_price));
        int max_num_drugs = (int)(game_state_.cash_ / drug_buy_price);

        max_num_drugs = Math.min(max_num_drugs, game_state_.max_space_);
        
        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
        		max_num_drugs);
        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
        		max_num_drugs);
        
        ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_quantity))).setText(
        		Integer.toString(max_num_drugs));
        
        ((Button)(drug_buy_dialog_.findViewById(R.id.drug_buy_cancel))).setOnClickListener(
        		new CancelDialogListener(DIALOG_DRUG_BUY));
       
        ((Button)(drug_buy_dialog_.findViewById(R.id.drug_buy_confirm)))
        		.setOnClickListener(new BuyDrugsListener());
	}

	// TODO: The drug sell dialog is under development.
	private Dialog GetDrugSellDialog() {
		if (drug_sell_dialog_ == null) {
    		drug_sell_dialog_ = new Dialog(this);
    		drug_sell_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		drug_sell_dialog_.setContentView(R.layout.drug_sell_layout);
            ((SeekBar)drug_sell_dialog_.findViewById(R.id.drug_quantity_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).setText(
    						Integer.toString(progress));
    			}
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
    	}
		return drug_sell_dialog_;
	}

	// TODO: The drug sell dialog is under development.
	private void PrepareDrugSellDialog() {
		((TextView)(drug_sell_dialog_.findViewById(R.id.drug_name))).setText(
        		"Sell " + game_state_.drugs_.elementAt(dialog_drug_index_).drug_name_);
        float drug_sell_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_price))).setText(
        		"$" + Float.toString(drug_sell_price));
        int drug_quantity = game_state_.dealer_drugs_.elementAt(dialog_drug_index_);
        
        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
        		drug_quantity);
        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
        		drug_quantity);
        
        ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_quantity))).setText(
        		Integer.toString(drug_quantity));
        
        ((Button)(drug_sell_dialog_.findViewById(R.id.drug_sell_cancel))).setOnClickListener(
        		new CancelDialogListener(DIALOG_DRUG_SELL));
       
        ((Button)(drug_sell_dialog_.findViewById(R.id.drug_sell_confirm)))
        		.setOnClickListener(new SellDrugsListener());
	}

	// TODO: The subway dialog is under development.
	private Dialog GetSubwayDialog() {
		if (subway_dialog_ == null) {
			subway_dialog_ = new Dialog(this);
    		subway_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		subway_dialog_.setContentView(R.layout.simple_subway_layout);
    		subway_dialog_.findViewById(R.id.brooklyn).setOnClickListener(
    				new ChangeLocationListener(0));
    		subway_dialog_.findViewById(R.id.the_bronx).setOnClickListener(
    				new ChangeLocationListener(1));
    		subway_dialog_.findViewById(R.id.central_park).setOnClickListener(
    				new ChangeLocationListener(2));
    		subway_dialog_.findViewById(R.id.coney_island).setOnClickListener(
    				new ChangeLocationListener(3));
    		subway_dialog_.findViewById(R.id.the_ghetto).setOnClickListener(
    				new ChangeLocationListener(4));
    		subway_dialog_.findViewById(R.id.manhattan).setOnClickListener(
    				new ChangeLocationListener(5));
    		subway_dialog_.findViewById(R.id.queens).setOnClickListener(
    				new ChangeLocationListener(6));
    		subway_dialog_.findViewById(R.id.staten_island).setOnClickListener(
    				new ChangeLocationListener(7));
		}
		return subway_dialog_;
	}
	
	// TODO: The subway dialog is under development.
	private void PrepareSubwayDialog() {
		subway_dialog_.findViewById(R.id.brooklyn).setVisibility(
				game_state_.location_ == 0 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.the_bronx).setVisibility(
				game_state_.location_ == 1 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.central_park).setVisibility(
				game_state_.location_ == 2 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.coney_island).setVisibility(
				game_state_.location_ == 3 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.the_ghetto).setVisibility(
				game_state_.location_ == 4 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.manhattan).setVisibility(
				game_state_.location_ == 5 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.queens).setVisibility(
				game_state_.location_ == 6 ? View.GONE : View.VISIBLE);
		subway_dialog_.findViewById(R.id.staten_island).setVisibility(
				game_state_.location_ == 7 ? View.GONE : View.VISIBLE);
	}
	
	// TODO: The subway dialog is under development
	public class ChangeLocationListener implements View.OnClickListener {
		public ChangeLocationListener(int new_location) {
			location_ = new_location;
		}
		public void onClick(View v) {
			game_state_.location_ = location_;
			// TODO: handle remaining time/end of game, or actually, that may
			// be something that gets handled in refreshDisplay(), because
			// really all that's going to happen here is not showing the
			// subway and instead showing an end game button, which will
			// have its own handling.
			game_state_.days_left_ -= 1;
			
			// Apply loan shark interest when moving locations.
			game_state_.loan_ +=
				game_state_.loan_interest_rate_ * game_state_.loan_;
			
			// Apply bank interest when moving locations.
			game_state_.bank_ += 
	            game_state_.bank_interest_rate_ * game_state_.bank_;
			
			game_state_.SetupNewLocation();
			
			refreshDisplay();
			dismissDialog(DIALOG_SUBWAY);
		}
		int location_;
	}
	
	// TODO: The loan shark dialogs are under development.
	private Dialog GetPayLoanSharkDialog() {
		if (pay_loan_shark_dialog_ == null) {
			pay_loan_shark_dialog_ = new Dialog(this);
			pay_loan_shark_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pay_loan_shark_dialog_.setContentView(R.layout.loan_shark_pay_layout);
            ((SeekBar)pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)pay_loan_shark_dialog_.findViewById(R.id.loan_amount)).setText(
    						Integer.toString(progress));
    			}
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
            ((Button)pay_loan_shark_dialog_.findViewById(R.id.loan_refresh)).setOnClickListener(
            		new RefreshLoanListener());
		}
		return pay_loan_shark_dialog_;
	}
	
	// TODO: The loan shark dialogs are under development.
	private void PreparePayLoanSharkDialog() {
        int max_loan_payment = Math.min(game_state_.loan_, game_state_.cash_);
        ((SeekBar)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setMax(
        		max_loan_payment);
        ((SeekBar)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setProgress(
        		max_loan_payment);
        
        ((TextView)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount))).setText(
        		Integer.toString(max_loan_payment));
        
        ((Button)(pay_loan_shark_dialog_.findViewById(R.id.loan_pay_cancel))).setOnClickListener(
        		new CancelDialogListener(DIALOG_PAY_LOAN_SHARK));
       
        ((Button)(pay_loan_shark_dialog_.findViewById(R.id.loan_pay_confirm)))
        		.setOnClickListener(new PayLoanSharkListener());
        
        boolean refresh_button_visible = !game_state_.DealerHasDrugs()
        	&& game_state_.cash_ < 2000
        	&& game_state_.bank_ < 2000;
        pay_loan_shark_dialog_.findViewById(R.id.loan_refresh).setVisibility(
        		refresh_button_visible ? View.VISIBLE : View.GONE);
	}
	
	// TODO: The banking dialogs are under development.
	private Dialog GetBankDepositDialog() {
		if (bank_deposit_dialog_ == null) {
			bank_deposit_dialog_ = new Dialog(this);
			bank_deposit_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
			bank_deposit_dialog_.setContentView(R.layout.bank_deposit_layout);
            ((SeekBar)bank_deposit_dialog_.findViewById(R.id.bank_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)bank_deposit_dialog_.findViewById(R.id.bank_amount)).setText(
    						Integer.toString(progress));
    			}
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
		}
		return bank_deposit_dialog_;
	}
	
	// TODO: The banking dialogs are under development.
	private void PrepareBankDepositDialog() {
        int max_bank_deposit = game_state_.cash_;
        ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
        		max_bank_deposit);
        ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
        		max_bank_deposit);
        
        ((TextView)(bank_deposit_dialog_.findViewById(R.id.bank_amount))).setText(
        		Integer.toString(max_bank_deposit));
        
        ((Button)(bank_deposit_dialog_.findViewById(R.id.bank_deposit_cancel))).setOnClickListener(
        		new CancelDialogListener(DIALOG_BANK_DEPOSIT));
       
        ((Button)(bank_deposit_dialog_.findViewById(R.id.bank_deposit_confirm)))
        		.setOnClickListener(new BankDepositConfirmListener());
	}
	
	// TODO: The banking dialogs are under development.
	private Dialog GetBankWithdrawDialog() {
		if (bank_withdraw_dialog_ == null) {
			bank_withdraw_dialog_ = new Dialog(this);
			bank_withdraw_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
			bank_withdraw_dialog_.setContentView(R.layout.bank_withdraw_layout);
            ((SeekBar)bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide)).
            		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    			public void onProgressChanged(SeekBar seekBar, int progress,
    					boolean fromTouch) {
    				((TextView)bank_withdraw_dialog_.findViewById(R.id.bank_amount)).setText(
    						Integer.toString(progress));
    			}
    			public void onStartTrackingTouch(SeekBar seekBar) {}
    			public void onStopTrackingTouch(SeekBar seekBar) {}
            	
            });
		}
		return bank_withdraw_dialog_;
	}
	
	// TODO: The banking dialogs are under development.
	private void PrepareBankWithdrawDialog() {
        int max_bank_deposit = game_state_.bank_;
        ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
        		max_bank_deposit);
        ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
        		max_bank_deposit);
        
        ((TextView)(bank_withdraw_dialog_.findViewById(R.id.bank_amount))).setText(
        		Integer.toString(max_bank_deposit));
        
        ((Button)(bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_cancel))).setOnClickListener(
        		new CancelDialogListener(DIALOG_BANK_WITHDRAW));
       
        ((Button)(bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_confirm)))
        		.setOnClickListener(new BankWithdrawConfirmListener());
	}
	
	// TODO: The hardass dialogs are under development.
	private Dialog GetHardassDialog() {
		if (hardass_dialog_ == null) {
			hardass_dialog_ = new Dialog(this);
			hardass_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
			hardass_dialog_.setContentView(R.layout.hardass_layout);
			((Button)hardass_dialog_.findViewById(R.id.hardass_fight)).setOnClickListener(
					new FightHardassListener());
			((Button)hardass_dialog_.findViewById(R.id.hardass_run)).setOnClickListener(
					new RunFromHardassListener());
			((Button)hardass_dialog_.findViewById(R.id.hardass_do_nothing)).setOnClickListener(
					new DoNothingWithHardassListener());
		}
		return hardass_dialog_;
	}
	
	private class FightHardassListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: fight logic
			game_state_.hardass_deputies_ = 0;
			game_state_.hardass_health_ = Math.max(game_state_.hardass_health_ - 5, 0);
			game_state_.health_ -= 1;
			dismissDialog(DIALOG_HARDASS);
			refreshDisplay();
		}
	}
	
	private class RunFromHardassListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: run logic
		    if (rand_gen_.nextFloat() < 0.5) {
		    	game_state_.hardass_deputies_ = 0;
		    	game_state_.hardass_health_ = 0;
		    } else {
		    	game_state_.health_ -= 1;
		    }
			dismissDialog(DIALOG_HARDASS);
			refreshDisplay();
		}
	}
	
	private class DoNothingWithHardassListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO: stand there logic
			game_state_.health_ -= 1;
			dismissDialog(DIALOG_HARDASS);
			refreshDisplay();
		}
	}
	
	// TODO: The hardass dialogs are under development.
	private void PrepareHardassDialog() {
		String warning_string = "Officer Hardass and " +
				Integer.toString(game_state_.hardass_deputies_) +
				" of his deputies are attacking!  Your health: " +
				Integer.toString(game_state_.health_);
        
        ((TextView)(hardass_dialog_.findViewById(R.id.hardass_message))).setText(
        		warning_string);
        hardass_dialog_.findViewById(R.id.hardass_fight).setVisibility(
        		game_state_.guns_ > 0 ? View.VISIBLE : View.GONE);
	}
	
	// The dialogs available in the game include moving from place to place on the subway,
	// buying and selling drugs, looking at your inventory, the loan shark, and the bank.
	public static final int DIALOG_SUBWAY = 2002;
	public static final int DIALOG_DRUG_BUY = 2003;
	public static final int DIALOG_INVENTORY = 2004;
	public static final int DIALOG_DRUG_SELL = 2005;
	public static final int DIALOG_PAY_LOAN_SHARK = 2006;
	public static final int DIALOG_TAKE_LOAN_SHARK = 2007;
	public static final int DIALOG_BANK_DEPOSIT = 2008;
	public static final int DIALOG_BANK_WITHDRAW = 2009;
	public static final int DIALOG_END_GAME = 2010;
	public static final int DIALOG_HARDASS = 2011;
	
	private class CancelDialogListener implements View.OnClickListener {
		public CancelDialogListener(int dialog_id) {
			dialog_id_ = dialog_id;
		}
		public void onClick(View v) {
			dismissDialog(dialog_id_);
		}
		int dialog_id_;
	}
	
	private class BuyDrugsClickListener implements View.OnClickListener {
		public BuyDrugsClickListener(int drug_index) {
			drug_index_ = drug_index;
		}
		public void onClick(View v) {
			dialog_drug_index_ = drug_index_;
			showDialog(DIALOG_DRUG_BUY);
		}
		int drug_index_;
	}
	
	private class SellDrugsClickListener implements View.OnClickListener {
		public SellDrugsClickListener(int drug_index) {
			drug_index_ = drug_index;
		}
		public void onClick(View v) {
			dialog_drug_index_ = drug_index_;
			showDialog(DIALOG_DRUG_SELL);
		}
		int drug_index_;
	}
	
	public class SubwayClickListener implements View.OnClickListener {
		public SubwayClickListener() {
		}
		public void onClick(View v) {
			showDialog(DIALOG_SUBWAY);
		}
	}
	
	private class PayLoanSharkClickListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_PAY_LOAN_SHARK);
		}
	}
	
	private class PayLoanSharkListener implements View.OnClickListener {
		public void onClick(View v) {
			int loan_payment_amount = Integer.parseInt(((TextView)pay_loan_shark_dialog_.findViewById(
	        		R.id.loan_amount)).getText().toString());
			game_state_.cash_ -= loan_payment_amount;
			game_state_.loan_ -= loan_payment_amount;
			refreshDisplay();
			dismissDialog(DIALOG_PAY_LOAN_SHARK);
		}
	}
	
	private class RefreshLoanListener implements View.OnClickListener {
		public void onClick(View v) {
			game_state_.cash_ += 2000;
			game_state_.loan_ += 5500;
			refreshDisplay();
			dismissDialog(DIALOG_PAY_LOAN_SHARK);
		}
	}
	
	private class BankDepositListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_BANK_DEPOSIT);
		}
	}
	
	private class BankWithdrawListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_BANK_WITHDRAW);
		}
	}
	
	private class BankDepositConfirmListener implements View.OnClickListener {
		public void onClick(View v) {
			int bank_deposit_amount = Integer.parseInt(((TextView)bank_deposit_dialog_.findViewById(
	        		R.id.bank_amount)).getText().toString());
			game_state_.cash_ -= bank_deposit_amount;
			game_state_.bank_ += bank_deposit_amount;
			refreshDisplay();
			dismissDialog(DIALOG_BANK_DEPOSIT);
		}
	}

	private class BankWithdrawConfirmListener implements View.OnClickListener {
		public void onClick(View v) {
			int bank_withdraw_amount = Integer.parseInt(((TextView)bank_withdraw_dialog_.findViewById(
	        		R.id.bank_amount)).getText().toString());
			game_state_.cash_ += bank_withdraw_amount;
			game_state_.bank_ -= bank_withdraw_amount;
			refreshDisplay();
			dismissDialog(DIALOG_BANK_WITHDRAW);
		}
	}
	
	private class BuyDrugsListener implements View.OnClickListener {
		public void onClick(View v) {
			int drug_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
	        int drug_quantity = Integer.parseInt(((TextView)drug_buy_dialog_.findViewById(
	        		R.id.drug_quantity)).getText().toString());
	        game_state_.cash_ -= drug_quantity * drug_price;
	        game_state_.max_space_ -= drug_quantity;
	        game_state_.dealer_drugs_.setElementAt(
	        		game_state_.dealer_drugs_.elementAt(dialog_drug_index_) +
	        	    drug_quantity, dialog_drug_index_);
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_BUY);
		}
	}
	
	private class SellDrugsListener implements View.OnClickListener {
		public void onClick(View v) {
	        int drug_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
	        int drug_quantity = Integer.parseInt(((TextView)drug_sell_dialog_.findViewById(
	        		R.id.drug_quantity)).getText().toString());
	        game_state_.cash_ += drug_quantity * drug_price;
	        game_state_.max_space_ += drug_quantity;
	        game_state_.dealer_drugs_.setElementAt(
	        		game_state_.dealer_drugs_.elementAt(dialog_drug_index_) -
	        	    drug_quantity, dialog_drug_index_);
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_SELL);
		}
	}
	
	private GameState getGameState() {
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
		dealer_data.open();
		GameState game_state = new GameState(dealer_data.getGameString());
		dealer_data.close();
		return game_state;
	}
	
	private void saveGameState() {
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
		dealer_data.open();
		dealer_data.setGameString(game_state_.SerializeGame());
		dealer_data.close();
	}
	
	// TODO: this is very incomplete, because we haven't worked out what the
	// target layout is going to be, so for now, simplicity rules.
	private LinearLayout constructDrugLayout(int drug_index) {
		LinearLayout drug_layout = new LinearLayout(this);
		drug_layout.setOrientation(LinearLayout.HORIZONTAL);
		drug_layout.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		
		TextView drug_name = new TextView(this);
		drug_name.setTextColor(Color.WHITE);
		drug_name.setTextSize(14);
		drug_name.setGravity(Gravity.LEFT);
		drug_name.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		drug_name.setText(game_state_.drugs_.elementAt(drug_index).drug_name_);
		
		TextView drug_price = new TextView(this);
		drug_price.setTextColor(Color.WHITE);
		drug_price.setTextSize(14);
		drug_price.setGravity(Gravity.LEFT);
		drug_price.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		drug_price.setText(Integer.toString(game_state_.drug_price_.elementAt(drug_index)));
		
		Button buy_button = new Button(this);
		buy_button.setTextSize(14);
		buy_button.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		buy_button.setText("Buy");
		buy_button.setOnClickListener(new BuyDrugsClickListener(drug_index));
		
		Button sell_button = new Button(this);
		sell_button.setTextSize(14);
		sell_button.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		sell_button.setText("Sell");
		sell_button.setOnClickListener(new SellDrugsClickListener(drug_index));
		
		drug_layout.addView(drug_name);
		drug_layout.addView(drug_price);
		drug_layout.addView(buy_button);
		drug_layout.addView(sell_button);
		
		return drug_layout;
	}
	
	private void AddAllAvailableDrugs() {
		LinearLayout drug_layout = (LinearLayout)findViewById(R.id.drug_list);
		drug_layout.removeAllViews();
		for (int i = 0; i < game_state_.drugs_.size(); ++i) {
			if (game_state_.drug_price_.elementAt(i) > 0) {
				drug_layout.addView(constructDrugLayout(i));
			}
		}
	}
	
	private void refreshDisplay() {
		((TextView)findViewById(R.id.location_name)).setText(
				game_state_.locations_.elementAt(game_state_.location_).
				location_name_);
		
		AddAllAvailableDrugs();
		
		((TextView)findViewById(R.id.dealer_cash)).setText(
				"c: " + Integer.toString(game_state_.cash_));
		((TextView)findViewById(R.id.dealer_bank)).setText(
				"b: " + Integer.toString(game_state_.bank_));
		((TextView)findViewById(R.id.dealer_loan)).setText(
				"l: " + Integer.toString(game_state_.loan_));
		((TextView)findViewById(R.id.dealer_health)).setText(
				"h: " + Integer.toString(game_state_.health_));
		((TextView)findViewById(R.id.dealer_space)).setText(
				"s: " + Integer.toString(game_state_.max_space_));
		((TextView)findViewById(R.id.dealer_guns)).setText(
				"g: " + Integer.toString(game_state_.guns_));
		((TextView)findViewById(R.id.dealer_days_left)).setText(
				"d: " + Integer.toString(game_state_.days_left_));
		
		// Hide the bank and loan shark if not in Brooklyn.
		((LinearLayout)findViewById(R.id.bank_row)).setVisibility(
				game_state_.location_ == 0 ? View.VISIBLE : View.GONE);
		((Button)findViewById(R.id.loan_shark_button)).setVisibility(
				game_state_.location_ == 0 ? View.VISIBLE : View.GONE);
		
		// Check for the end of the game.
		if (game_state_.days_left_ <= 0) {
			((Button)findViewById(R.id.subway_button)).setText("End Game");
			// TODO: change the listener to something that ends the game
		}
		
		// Check for officer hardass.  If he's present, then bring up the
		// fight or flight dialog.
		if (game_state_.hardass_health_ > 0) {
		  showDialog(DIALOG_HARDASS);
		}
		
/*
        	// TODO: the order of these changes sometimes, it might be nicer if the order were
        	//       more deterministic
	        Iterator<String> drug_names =
	        	game_information_.location_drugs_.keySet().iterator();
	        while (drug_names.hasNext()) {
	        	String drug_name = drug_names.next();
	        	int drug_price = game_information_.location_drugs_.get(
	        			drug_name).intValue();
	        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_gray,
	        			R.drawable.weed, drug_name, "$" + Integer.toString(drug_price));
	        	
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
	        String current_location = GameInformation_old.LOCATION_NAMES[game_information_.location_];
	        if (GameInformation_old.LOCATION_HAS_SHARK[game_information_.location_]) {
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
	        if (GameInformation_old.LOCATION_HAS_BANK[game_information_.location_]) {
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
        */
	}
	
	// When moving from one location to another (advancing one turn) this resets all the drugs
	// that are available and processes all the random events that can happen on a turn-by-turn
	// basis.
	/*
	public void setupLocation() {
        //game_information_.game_messages_.clear();
        //game_information_.fight_messages_.clear();
        
        // First determine how many drugs are available at the current location.
        // TODO: This is broken, the order doesn't work like this.
        String current_location = GameInformation_old.LOCATION_NAMES[game_information_.location_];
        int base_drugs_count = GameInformation_old.LOCATION_BASE_DRUGS[game_information_.location_];
        int drug_variance = GameInformation_old.LOCATION_DRUG_VARIANCE[game_information_.location_];
		int num_drugs_present = base_drugs_count + rand_gen_.nextInt(drug_variance + 1);
		if (num_drugs_present < 1) {
			num_drugs_present = 1;
		}
		
        // Now determine which of the drugs are available by making a list including all the
		// possible drugs and removing random elements until the limit is met.
		int num_avail_drugs = GameInformation_old.DRUG_NAMES.length;
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
			String drug_name = GameInformation_old.DRUG_NAMES[drugs_present.elementAt(i)];
			
			// This will set the price of the drug and also create messages that shown be shown
			// relating to the drug.
			Vector<String> price_and_messages = chooseDrugPrice(drug_name, drugs_present.elementAt(i));
					//drug_name, game_information_.drugs_.get(drug_name));
			game_information_.location_drugs_.put(drug_name,
					Integer.parseInt(price_and_messages.elementAt(0)));
			for (int j = 1; j < price_and_messages.size(); ++j) {
				//game_information_.game_messages_.add(
				//		price_and_messages.elementAt(j));
			}
		}

		// Determine if any coats are present and add them to the location inventory if they are.
		// TODO: support multiple coats
		// TODO: more price options
		game_information_.location_coats_.clear();
    	if (rand_gen_.nextDouble() < game_information_.coat_likelihood_) {
    		int coat_number = rand_gen_.nextInt(GameInformation_old.COAT_NAMES.length);
    		String coat_name = GameInformation_old.COAT_NAMES[coat_number];
    		int coat_price = (int)(GameInformation_old.COAT_BASE_PRICE[coat_number] +
    				(rand_gen_.nextDouble() - 0.5) *
    				GameInformation_old.COAT_PRICE_VARIANCE[coat_number]);
    		game_information_.location_coats_.put(coat_name, coat_price);
    	}
    	
    	// Determine if any guns are present and add them to the location inventory if they are.
    	// TODO: support multiple guns
    	// TODO: more price options
    	game_information_.location_guns_.clear();
    	if (rand_gen_.nextDouble() < game_information_.gun_likelihood_) {
    		int gun_number = rand_gen_.nextInt(GameInformation_old.GUN_NAMES.length);
    		String gun_name = GameInformation_old.GUN_NAMES[gun_number];
    		int gun_price = (int)(GameInformation_old.GUN_BASE_PRICE[gun_number] +
    				(rand_gen_.nextDouble() - 0.5) *
    				GameInformation_old.GUN_PRICE_VARIANCE[gun_number]);
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
    		fight_message += "are attacking!";
    		game_information_.fight_messages_.add(fight_message);
    	}
	}

	// Given a set of drug attributes this will determine a random price within the parameters
	// of that drug. It will return a price and also other messages about the price (notification
	// messages that the drug price is unusually high or low).
	// TODO: handle custom messages for drugs
//	public Vector<String> chooseDrugPrice(String name, Drug drug) {
	public Vector<String> chooseDrugPrice(String name, int drug_index) {
		Vector<String> price_and_messages = new Vector<String>();
		int base_price = GameInformation_old.DRUG_BASE_PRICE[drug_index];
		int price_variance = GameInformation_old.DRUG_PRICE_VARIANCE[drug_index];
		double low_probability = GameInformation_old.DRUG_LOW_PROB[drug_index];
		double low_multiplier = GameInformation_old.DRUG_LOW_MULT[drug_index];
		double high_probability = GameInformation_old.DRUG_HIGH_PROB[drug_index];
		double high_multiplier = GameInformation_old.DRUG_HIGH_MULT[drug_index];
		int price = (int)(base_price - price_variance / 2.0 +
				rand_gen_.nextDouble() * price_variance);
		// Check for price jumps
		if (rand_gen_.nextFloat() < low_probability) {
			price = (int)(price * low_multiplier);
			price_and_messages.add(name + " is being sold at very low prices!");
		} else if (rand_gen_.nextFloat() < high_probability) {
			price = (int)(price * high_multiplier);
			price_and_messages.add(name + " is being sold at very high prices!");
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
	*/
	/*
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
	*/
	
	Dialog subway_dialog_;
	Dialog drug_buy_dialog_;
	Dialog drug_sell_dialog_;
	Dialog inventory_dialog_;
	Dialog loan_shark_dialog_;
	Dialog bank_deposit_dialog_;
	Dialog bank_withdraw_dialog_;
	Dialog end_game_dialog_;
	Dialog pay_loan_shark_dialog_;
	Dialog hardass_dialog_;

	String dialog_drug_name_;
	int dialog_drug_index_;
	
	GameState game_state_;
	
	int viewWidth_;
    int viewHeight_;
	LinearLayout outer_layout_;
	LinearLayout current_row_;
	int total_width_added_;
	
	int currently_selected_length_;
	int currently_selected_game_type_;
	
	// Random number generator for this activity.
    public static Random rand_gen_ = new Random();
}

