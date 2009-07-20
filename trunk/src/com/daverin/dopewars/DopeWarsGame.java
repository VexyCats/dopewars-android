package com.daverin.dopewars;

import java.util.Vector;

import com.daverin.dopewars.Global.Drug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class DopeWarsGame extends Activity {
	
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
        	LinearLayout next_drug = new LinearLayout(this);
        	next_drug.setOrientation(LinearLayout.VERTICAL);
        	next_drug.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.WRAP_CONTENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
        	next_drug.setBackgroundResource(R.drawable.btn_translucent_blue);
        	ImageView next_drug_image = new ImageView(this);
        	next_drug_image.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
        	next_drug_image.setImageResource(R.drawable.weed);
        	next_drug_image.setScaleType(ScaleType.FIT_CENTER);
        	next_drug.addView(next_drug_image);
        	TextView next_drug_name = new TextView(this);
        	next_drug_name.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.WRAP_CONTENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
        	next_drug_name.setText(current_drugs_.elementAt(i).drug_name_);
        	next_drug.addView(next_drug_name);
        	
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
}
