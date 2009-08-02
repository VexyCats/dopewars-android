package com.daverin.dopewars;

import java.util.Vector;

import com.daverin.dopewars.DopeWars.CancelEditListener;
import com.daverin.dopewars.DopeWars.ConfirmEditListener;
import com.daverin.dopewars.Global.Drug;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class DopeWarsGame extends Activity {
	
	public static final int DIALOG_SUBWAY = 1002;
	
	// Respond to a click on the subway.
	public class SubwayListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_SUBWAY);
		}
	}
	
	// Response to a change in location.
	public class ChangeLocationListener implements View.OnClickListener {
		public void onClick(View v) {
			setupLocation();
			refreshDisplay();
			dismissDialog(DIALOG_SUBWAY);
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up the main content of the view.
        setContentView(R.layout.main_game_screen);
        
        setupLocation();
        refreshDisplay();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_SUBWAY) {
        	if (subway_dialog_ == null) {
        		subway_dialog_ = new Dialog(this);
        		subway_dialog_.setContentView(R.layout.subway_layout);
        	}
        	
        	// *** So far I haven't figured how to deal with this, the emulator no likes it,
        	// *** but the phone is fine with it. So comment it out when testing, uncomment
        	// *** it when building. Life sucks sometimes.
        	//edit_dealer_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	
            return subway_dialog_;
        }
        return super.onCreateDialog(id);
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
        if(id == DIALOG_SUBWAY) {
        	((Button)subway_dialog_.findViewById(R.id.btn_bronx)).setOnClickListener(new ChangeLocationListener());
        	((Button)subway_dialog_.findViewById(R.id.btn_brooklyn)).setOnClickListener(new ChangeLocationListener());
        	((Button)subway_dialog_.findViewById(R.id.btn_central_park)).setOnClickListener(new ChangeLocationListener());
        	((Button)subway_dialog_.findViewById(R.id.btn_coney_island)).setOnClickListener(new ChangeLocationListener());
        	((Button)subway_dialog_.findViewById(R.id.btn_ghetto)).setOnClickListener(new ChangeLocationListener());
        	((Button)subway_dialog_.findViewById(R.id.btn_manhattan)).setOnClickListener(new ChangeLocationListener());
        	// this may not have to do anything other than the width setting
        	WindowManager.LayoutParams dialog_params = subway_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	subway_dialog_.getWindow().setAttributes(dialog_params);
        }
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
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	main_text.setTextColor(Color.WHITE);
    	main_text.setText(main_string);
    	new_button.addView(main_text);
    	TextView secondary_text = new TextView(this);
    	secondary_text.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	secondary_text.setTextColor(Color.GREEN);
    	secondary_text.setText(secondary_string);
    	new_button.addView(secondary_text);
    	
    	return new_button;
	}
	
	public void refreshDisplay() {
        int viewWidth = this.getResources().getDisplayMetrics().widthPixels;
        int viewHeight = this.getResources().getDisplayMetrics().heightPixels;
		LinearLayout outer_layout = (LinearLayout)findViewById(R.id.outer_layout);
		LinearLayout current_row = new LinearLayout(this);
		current_row.setOrientation(LinearLayout.HORIZONTAL);
		current_row.setLayoutParams(new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
		outer_layout.removeAllViews();
		int total_width_added = 0;
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
        dealer_data.open();
		for (int i = 0; i < dealer_data.numLocationDrugs(); ++i) {
        	// Construct the next button
			String drug_name = dealer_data.getLocationDrugName(i);
        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_blue,
        			R.drawable.weed, drug_name,
        			"$" + Integer.toString(
        					dealer_data.getLocationDrugPrice(drug_name)));
        	
        	next_drug.measure(viewWidth, viewHeight);
        	if (next_drug.getMeasuredWidth() + total_width_added > viewWidth) {
        		outer_layout.addView(current_row);
        		current_row = new LinearLayout(this);
        		current_row.setOrientation(LinearLayout.HORIZONTAL);
        		current_row.setLayoutParams(new LinearLayout.LayoutParams(
            			LinearLayout.LayoutParams.FILL_PARENT,
            			LinearLayout.LayoutParams.WRAP_CONTENT));
        		total_width_added = 0;
        	}
        	total_width_added += next_drug.getMeasuredWidth();
        	current_row.addView(next_drug);
        }
        // Add subway button
    	LinearLayout subway_button = makeButton(R.drawable.btn_translucent_blue,
    			R.drawable.subway, "Subway", " ");
    	subway_button.setOnClickListener(new SubwayListener());
    	
    	subway_button.measure(viewWidth, viewHeight);
    	if (subway_button.getMeasuredWidth() + total_width_added > viewWidth) {
    		outer_layout.addView(current_row);
    		current_row = new LinearLayout(this);
    		current_row.setOrientation(LinearLayout.HORIZONTAL);
    		current_row.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.FILL_PARENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
    		total_width_added = 0;
    	}
    	total_width_added += subway_button.getMeasuredWidth();
    	current_row.addView(subway_button);
        if (total_width_added > 0) {
        	outer_layout.addView(current_row);
        }
    	String current_dealer_name = dealer_data.getDealerName();
        String avatar_id = dealer_data.getDealerAvatar();
        String cash = dealer_data.getGameCash();
        dealer_data.close();
        ((TextView)findViewById(R.id.player_name)).setText(current_dealer_name);
        ((TextView)findViewById(R.id.player_money)).setText("$" + cash);
        ((ImageView)findViewById(R.id.avatar_image)).setImageResource(Integer.parseInt(avatar_id));
	}
	
	public void setupLocation() {
		// Determine the number and type of drugs available at the current location.
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
        dealer_data.open();
        int num_avail_drugs = dealer_data.numAvailableDrugs();
		boolean[] drug_present = new boolean[num_avail_drugs];
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		drug_present[i] = false;
    	}
    	int num_drugs_left = num_avail_drugs;
    	int base_drugs_count = dealer_data.drugCountForLocation("Brooklyn");
    	int drug_variance = dealer_data.drugVarianceForLocation("Brooklyn");
		int num_drugs_present = base_drugs_count +
		        Global.rand_gen_.nextInt(drug_variance + 1);
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
    	
    	dealer_data.clearLocationDrugs();
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		if (drug_present[i]) {
    			String drug_name = dealer_data.getAvailableDrugName(i);
    			int drug_price = dealer_data.chooseDrugPrice(drug_name);
    			dealer_data.addDrugAtCurrentLocation(drug_name, drug_price);
    		}
    	}
    	dealer_data.close();
	}
	
	Dialog subway_dialog_;
}
