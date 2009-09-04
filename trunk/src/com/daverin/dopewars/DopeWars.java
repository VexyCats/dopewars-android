package com.daverin.dopewars;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class DopeWars extends Activity {
	
	// The dialogs available from the main window, for now it's just the
	// dialog to edit the current dealer.
	public static final int DIALOG_EDIT_DEALER = 1001;
	
	// This listener is attached to the game start buttons. For now it's a
	// hard-coded setup, eventually it should init from game settings from
	// xml files and stuff.
	public class GameStartListener implements View.OnClickListener {
		public void onClick(View v) {
	        // A check that the dealer name and avatar id is set
			dealer_data_.open();
	        String dealer_name = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_NAME);
	        if (dealer_name.equals("")) {
	        	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_NAME, "Guest");
	        }
	        String avatar_id = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
	        if (avatar_id.equals("")) {
	        	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME, "0");
	        }
	        
	        // Clear the inventory, start with a standard amount of cash
	        // and debt. This stuff will eventually be determined by game settings.
	        String initial_game_state = "cash:55000|debt:2000|location:Brooklyn|space:100|" +
	        	"max_space:100|days_left:5|loan:5500|bank:0|loan_location:Brooklyn|" +
	        	"bank_location:Brooklyn";
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		initial_game_state);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY,
	        		"");
	        
	        // *** I'm not sure where the best place for this is yet ***
	        Global.loadIcons();
	        
	        // *** This part will be setup by the xml file ***
	        dealer_data_.clearAvailableDrugs();
	        dealer_data_.addDrug("Weed", "base_price:400|price_variance:200|icon:weed|low_probability:0.1|low_multiplier:0.5");
	        dealer_data_.addDrug("Acid", "base_price:1500|price_variance:400|icon:acid");
	        dealer_data_.addDrug("Ludes", "base_price:80|price_variance:20|icon:ludes");
	        dealer_data_.addDrug("Heroin", "base_price:10000|price_variance:2000|icon:heroin|high_probability:0.1|high_multiplier:2.0");
	        dealer_data_.addDrug("Cocaine", "base_price:20000|price_variance:3000|icon:cocaine|high_probability:0.1|high_multiplier:2.0");
	        dealer_data_.addDrug("Shrooms", "base_price:1000|price_variance:200|icon:shrooms");
	        dealer_data_.addDrug("Speed", "base_price:110|price_variance:30|icon:speed");
	        dealer_data_.addDrug("Hashish", "base_price:180|price_variance:40|icon:hashish|low_probability:0.1|low_multiplier:0.5");
	        
	        // *** This part will also be setup by the xml file ***
	        dealer_data_.clearAvailableLocations();
	        dealer_data_.addLocation("Brooklyn",
	        		"base_drugs:6|drug_variance:1|map_x:105|map_y:220|has_bank:true|" +
	        		"has_loan_shark:true");
	        dealer_data_.addLocation("The Bronx", "base_drugs:8|drug_variance:2|map_x:80|map_y:5");
	        dealer_data_.addLocation("The Ghetto", "base_drugs:8|drug_variance:2|" +
	        		"map_x:73|map_y:100");
	        dealer_data_.addLocation("Coney Island", "base_drugs:8|drug_variance:2|" +
	        		"map_x:80|map_y:335");
	        dealer_data_.addLocation("Manhattan", "base_drugs:8|drug_variance:2|" +
	        		"map_x:75|map_y:143");
	        dealer_data_.addLocation("Central Park", "base_drugs:8|drug_variance:2|" +
	        		"map_x:103|map_y:60");
	        
	        dealer_data_.close();
	        
    		Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
	}
	
	// This listener just opens the edit dealer dialog.
	public class EditListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_EDIT_DEALER);
		}
	}

	// Respond to a click on the cancel button in the edit dealer dialog by
	// hiding the dialog.
	public class CancelEditListener implements View.OnClickListener {
		public void onClick(View v) {
			dismissDialog(DIALOG_EDIT_DEALER);
		}
	}
	
	// Clicking the confirm edit button in the edit dealer dialog is handled
	// by stripping out the data from the dialog and setting the dealer
	// data in the database appropriately.
	public class ConfirmEditListener implements View.OnClickListener {
		public void onClick(View v) {
			// Retrieve the newly entered name and avatar id.
			String new_dealer_name = ((TextView)edit_dealer_dialog_.
					findViewById(R.id.dealer_name)).getText().toString();
			String avatar_id = "0";
			if (((RadioButton)edit_dealer_dialog_.findViewById(
					R.id.avatar2)).isChecked()) {
				avatar_id = "1";
			} else if (((RadioButton)edit_dealer_dialog_.findViewById(
					R.id.avatar3)).isChecked()) {
				avatar_id = "2";
			}
			
			// Enter the newly entered name and avatar id into the database
			// only if they differ from the last entry.
			dealer_data_.open();
	        String last_name = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_NAME);
	        String last_avatar = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
	        if (!last_name.equals(new_dealer_name)) {
	        	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_NAME,
	        			new_dealer_name);
	        }
        	if (!last_avatar.equals(avatar_id)) {
	        	dealer_data_.setDealerString(
	        			DealerDataAdapter.KEY_DEALER_AVATAR_NAME, avatar_id);
	        }
        	dealer_data_.close();
			dismissDialog(DIALOG_EDIT_DEALER);
			
			// Update the user interface on this page
			updateUserInterface();
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create the database for this activity, and make sure that there is a dealer name and
        // avatar name, later these two fields are assumed and they have no meaningful default.
        dealer_data_ = new DealerDataAdapter(this);
        dealer_data_.open();
        String dealer_name = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_NAME);
        if (dealer_name.equals("")) {
        	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_NAME, "Guest");
        }
        String avatar_name = dealer_data_.getDealerString(
        		DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
        if (avatar_name.equals("")) {
        	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME, "0");
        }
        dealer_data_.close();
        
        // Set up the main content of the view.
        setContentView(R.layout.main_menu);
        ((LinearLayout)findViewById(R.id.edit_dealer)).setOnClickListener(new EditListener());
        ((Button)findViewById(R.id.classic_mode)).setOnClickListener(new GameStartListener());
		updateUserInterface();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_EDIT_DEALER) {
        	if (edit_dealer_dialog_ == null) {
        		edit_dealer_dialog_ = new Dialog(this);
        		// Requesting no title has to happen before setting the content view.
        		edit_dealer_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		edit_dealer_dialog_.setContentView(R.layout.edit_dealer);
            	((Button)edit_dealer_dialog_.findViewById(R.id.confirm_edit)).setOnClickListener(
            			new ConfirmEditListener());
            	((Button)edit_dealer_dialog_.findViewById(R.id.cancel_edit)).setOnClickListener(
            			new CancelEditListener());
        	}
        	
            return edit_dealer_dialog_;
        }
        return super.onCreateDialog(id);
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
        if(id == DIALOG_EDIT_DEALER) {
        	dealer_data_.open();
	        String current_dealer_name = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_NAME);
	        String avatar_id = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
            dealer_data_.close();
        	((TextView)edit_dealer_dialog_.findViewById(R.id.dealer_name)).setText(
        			current_dealer_name);
	        if (avatar_id.equals("1")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar2)).setChecked(true);
			} else if (avatar_id.equals("2")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar3)).setChecked(true);
			} else {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar1)).setChecked(true);
			}
        	WindowManager.LayoutParams dialog_params =
        		edit_dealer_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	edit_dealer_dialog_.getWindow().setAttributes(dialog_params);
        }
    }
	
	public void updateUserInterface() {
		// Set the interface elements according to the latest entry in the database.
		dealer_data_.open();
        String name = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_NAME);
        String avatar = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
        dealer_data_.close();
	    if (name.length() > 0 || avatar.length() > 0) {
	    	((TextView)findViewById(R.id.dealer_name)).setText(name);
	        if (avatar.equals("1")) {
				((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar2);
			} else if (avatar.equals("2")) {
				((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar3);
			} else {
				((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar1);
			}
	    }
	}
	
	Dialog edit_dealer_dialog_;
	DealerDataAdapter dealer_data_;
}