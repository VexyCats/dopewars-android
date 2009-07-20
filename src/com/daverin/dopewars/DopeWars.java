package com.daverin.dopewars;

import java.util.Vector;

import com.daverin.dopewars.Global.Drug;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class DopeWars extends Activity {
	
	public static final int DIALOG_EDIT_DEALER = 1001;
	
	// Respond to a click on a game button by starting a game Activity.
	public class GameStartListener implements View.OnClickListener {
		public void onClick(View v) {
			Global.base_drug_count_ = 6;
			Global.drug_count_variance_ = 2;
			Global.available_drugs_ = new Vector<Drug>();
			Drug d = new Drug();
			d.drug_name_ = "Weed";
			d.base_price_ = 400;
			d.range_ = 200;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.10;
			d.outlier_low_multiplier_ = 4;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Acid";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Ludes";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Heroin";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Cocaine";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Shrooms";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Speed";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
			d = new Drug();
			d.drug_name_ = "Hashish";
			d.base_price_ = 1500;
			d.range_ = 400;
			d.outlier_high_ = false;
			d.outlier_low_ = true;
			d.outlier_low_probability_ = 0.20;
			d.outlier_low_multiplier_ = 3;
			Global.available_drugs_.add(d);
    		Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
	}
	
	// Respond to a click on the dealer name by showing the dealer editing
	// dialog.
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
	// by stripping out the data from the dialog and setting a new database
	// entry to the dealer info.
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
	        String last_name = dealer_data.getMostRecentDealerName();
	        String last_avatar = dealer_data.getMostRecentAvatar();
        	if (!last_name.equals(new_dealer_name) ||
        			(!last_avatar.equals(avatar_id))) {
	        	dealer_data.setMostRecentDealerInfo(
	        			new_dealer_name, avatar_id);
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
        		edit_dealer_dialog_.setContentView(R.layout.edit_dealer);
            	((Button)edit_dealer_dialog_.findViewById(R.id.confirm_edit)).setOnClickListener(new ConfirmEditListener());
            	((Button)edit_dealer_dialog_.findViewById(R.id.cancel_edit)).setOnClickListener(new CancelEditListener());
        	}
        	
        	// *** So far I haven't figured how to deal with this, the emulator no likes it,
        	// *** but the phone is fine with it. So comment it out when testing, uncomment
        	// *** it when building. Life sucks sometimes.
        	//edit_dealer_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	
            return edit_dealer_dialog_;
        }
        return super.onCreateDialog(id);
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
        if(id == DIALOG_EDIT_DEALER) {
        	DealerDataAdapter dealer_data = new DealerDataAdapter(this);
	        dealer_data.open();
        	String current_dealer_name = dealer_data.getMostRecentDealerName();
            String avatar_id = dealer_data.getMostRecentAvatar();
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
	    String name = dealer_data.getMostRecentDealerName();
	    String avatar = dealer_data.getMostRecentAvatar();
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