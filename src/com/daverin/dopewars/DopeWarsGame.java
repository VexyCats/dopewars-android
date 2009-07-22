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
        
        // Initialize variables
        current_drugs_ = new Vector<Drug>();
        current_drug_prices_ = new Vector<Integer>();
        
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
        for (int i = 0; i < current_drugs_.size(); ++i) {
        	// Construct the next button
        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_blue,
        			R.drawable.weed, current_drugs_.elementAt(i).drug_name_,
        			"$" + Integer.toString(current_drug_prices_.elementAt(i)));
        	
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
	}
	
	public void setupLocation() {
		// Determine the number and type of drugs available at the current location.
		boolean[] drug_present = new boolean[Global.available_drugs_.size()];
    	for (int i = 0; i < Global.available_drugs_.size(); ++i) {
    		drug_present[i] = false;
    	}
    	int num_drugs_left = Global.available_drugs_.size();
		int num_drugs_present = Global.base_drug_count_ +
		        Global.rand_gen_.nextInt(Global.drug_count_variance_);
		if (num_drugs_present > Global.available_drugs_.size()) {
			num_drugs_present = Global.available_drugs_.size();
		}
		if (num_drugs_present < 1) {
			num_drugs_present = 1;
		}
    	while (num_drugs_left > Global.available_drugs_.size() - num_drugs_present) {
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
    	
    	current_drugs_.clear();
    	current_drug_prices_.clear();
    	for (int i = 0; i < Global.available_drugs_.size(); ++i) {
    		if (drug_present[i]) {
    			current_drugs_.add(Global.available_drugs_.elementAt(i));
    			current_drug_prices_.add(chooseDrugPrice(Global.available_drugs_.elementAt(i)));
    		}
    	}
	}
	
	public Integer chooseDrugPrice(Drug d) {
		int price = (int)(d.base_price_ - (d.range_ / 2.0) + Global.rand_gen_.nextDouble() * d.range_);
		if (d.outlier_high_) {
			if (Global.rand_gen_.nextDouble() < d.outlier_high_probability_) {
				price = (int)(price * d.outlier_high_multiplier_);
			}
		} else if (d.outlier_low_) {
			if (Global.rand_gen_.nextDouble() < d.outlier_low_probability_) {
				price = (int)(price / d.outlier_low_multiplier_);
				if (price < 1) {
					price = 1;
				}
			}
		}
		return new Integer(price);
	}
	
	public Vector<Drug> current_drugs_;
	public Vector<Integer> current_drug_prices_;
	
	Dialog subway_dialog_;
}
