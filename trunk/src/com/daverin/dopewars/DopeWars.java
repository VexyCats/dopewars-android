package com.daverin.dopewars;

import java.util.Vector;

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
			DealerDataAdapter dealer_data = new DealerDataAdapter(
					v.getContext());
	        dealer_data.open();
	        
	        // Clear the inventory, start with a standard amount of cash
	        // and debt.
	        dealer_data.setGameCash(5500);
	        dealer_data.setGameDebt(2000);
	        dealer_data.clearDealerInventory();
	        
	        // *** This part will be setup by the xml file ***
	        dealer_data.clearAvailableDrugs();
	        dealer_data.addDrug("Weed", 400, 200, false, false, (float)0.0,
	        		0, (float)0.0, 0);
	        dealer_data.addDrug("Acid", 1500, 400, false, false, (float)0.0,
	        		0, (float)0.0, 0);
	        dealer_data.addDrug("Ludes", 80, 20, false, false, (float)0.0,
	        		0, (float)0.0, 0);
	        dealer_data.addDrug("Heroin", 10000, 2000, false, false,
	        		(float)0.0, 0, (float)0.0, 0);
	        dealer_data.addDrug("Cocaine", 20000, 3000, false, false,
	        		(float)0.0, 0, (float)0.0, 0);
	        dealer_data.addDrug("Shrooms", 1000, 200, false, false,
	        		(float)0.0, 0, (float)0.0, 0);
	        dealer_data.addDrug("Speed", 110, 30, false, false, (float)0.0,
	        		0, (float)0.0, 0);
	        dealer_data.addDrug("Hashish", 180, 40, false, false, (float)0.0,
	        		0, (float)0.0, 0);
	        
	        // *** This part will also be setup by the xml file ***
	        dealer_data.clearAvailableLocations();
	        dealer_data.addLocation("Brooklyn", 6, 1, 105, 220, true, true);
	        dealer_data.addLocation("The Bronx", 8, 2, 80, 5, false, false);
	        dealer_data.addLocation("The Ghetto", 8, 2, 73, 100, false, false);
	        dealer_data.addLocation("Coney Island", 8, 2, 80, 335, false, false);
	        dealer_data.addLocation("Manhattan", 8, 2, 75, 143, false, false);
	        dealer_data.addLocation("Central Park", 8, 2, 103, 60, false, false);

	        dealer_data.setGameLocation("Brooklyn");
	        
	        dealer_data.close();
	        
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
			DealerDataAdapter dealer_data = new DealerDataAdapter(
					v.getContext());
	        dealer_data.open();
	        String last_name = dealer_data.getDealerName();
	        String last_avatar = dealer_data.getDealerAvatar();
        	if (!last_name.equals(new_dealer_name) ||
        			(!last_avatar.equals(avatar_id))) {
	        	dealer_data.setDealerInfo(new_dealer_name, avatar_id);
	        }
			dismissDialog(DIALOG_EDIT_DEALER);
			
			// Update the user interface on this page
			updateUserInterface();
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up the main content of the view.
        setContentView(R.layout.main_menu);
        ((LinearLayout)findViewById(R.id.edit_dealer)).setOnClickListener(new EditListener());
        ((Button)findViewById(R.id.classic_mode)).setOnClickListener(new GameStartListener());

        // Update the user interface on this page
		updateUserInterface();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_EDIT_DEALER) {
        	if (edit_dealer_dialog_ == null) {
        		edit_dealer_dialog_ = new Dialog(this);
        		edit_dealer_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		edit_dealer_dialog_.setContentView(R.layout.edit_dealer);
            	((Button)edit_dealer_dialog_.findViewById(R.id.confirm_edit)).setOnClickListener(new ConfirmEditListener());
            	((Button)edit_dealer_dialog_.findViewById(R.id.cancel_edit)).setOnClickListener(new CancelEditListener());
        	}
        	
            return edit_dealer_dialog_;
        }
        return super.onCreateDialog(id);
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
        if(id == DIALOG_EDIT_DEALER) {
        	DealerDataAdapter dealer_data = new DealerDataAdapter(this);
	        dealer_data.open();
        	String current_dealer_name = dealer_data.getDealerName();
            String avatar_id = dealer_data.getDealerAvatar();
            dealer_data.close();
        	((TextView)edit_dealer_dialog_.findViewById(R.id.dealer_name)).setText(current_dealer_name);
	        if (avatar_id.equals("1")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar2)).setChecked(true);
			} else if (avatar_id.equals("2")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar3)).setChecked(true);
			} else {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar1)).setChecked(true);
			}
        	WindowManager.LayoutParams dialog_params = edit_dealer_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	edit_dealer_dialog_.getWindow().setAttributes(dialog_params);
        }
    }
	
	public void updateUserInterface() {
		// Set the interface elements according to the latest entry in the database.
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
		dealer_data.open();
	    String name = dealer_data.getDealerName();
	    String avatar = dealer_data.getDealerAvatar();
	    dealer_data.close();
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
}