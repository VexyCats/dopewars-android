package com.daverin.dopewars;

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
	
	public static final int DIALOG_SUBWAY = 1002;
	public static final int DIALOG_DRUG_BUY = 1003;
	public static final int DIALOG_INVENTORY = 1004;
	public static final int DIALOG_DRUG_SELL = 1005;
	
	// Respond to a click on the subway.
	public class SubwayListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_SUBWAY);
		}
	}
	
	// Respond to a click on the inventory button.
	public class InventoryListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_INVENTORY);
		}
	}
	
	public class BuyDrugListener implements View.OnClickListener {
		public BuyDrugListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public void onClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(DIALOG_DRUG_BUY);
		}
		String drug_name_;
	}
	
	public class SellDrugListener implements View.OnClickListener {
		public SellDrugListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public void onClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(DIALOG_DRUG_SELL);
		}
		String drug_name_;
	}
	
	public class SellDrugListenerLong implements View.OnLongClickListener {
		public SellDrugListenerLong(String drug_name) {
			drug_name_ = drug_name;
		}
		public boolean onLongClick(View v) {
			dialog_drug_name_ = drug_name_;
			showDialog(DIALOG_DRUG_SELL);
			return true;
		}
		String drug_name_;
	}
	
	public class CompleteSaleListener implements View.OnClickListener {
		public CompleteSaleListener(String drug_name) {
			drug_name_ = drug_name;
		}
		public void onClick(View v) {
	        dealer_data_.open();
	        String location_inventory = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_LOCATION_INVENTORY);
	        int drug_price = Integer.parseInt(Global.parseAttribute(drug_name_, location_inventory));
	        int drug_quantity = Integer.parseInt(((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).getText().toString());
	        String game_info = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
	        int old_game_cash = Integer.parseInt(Global.parseAttribute("cash", game_info));
	        String new_game_info = Global.setAttribute(
	        		"cash", Integer.toString(old_game_cash - drug_quantity * drug_price), game_info);
	        String current_inventory = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY);
	        String current_drug_amount = Global.parseAttribute(drug_name_, current_inventory);
	        int current_space = Integer.parseInt(Global.parseAttribute("space", game_info));
	        current_space = current_space - drug_quantity;
	        new_game_info = Global.setAttribute("space", Integer.toString(current_space), new_game_info);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, new_game_info);
	        String new_drug_quantity = Integer.toString(drug_quantity);
	        if (!current_drug_amount.equals("")) {
	        	new_drug_quantity = Integer.toString(drug_quantity + Integer.parseInt(current_drug_amount));
	        }
	        String new_inventory = Global.setAttribute(drug_name_, new_drug_quantity, current_inventory);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY, new_inventory);
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
	        String location_inventory = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_LOCATION_INVENTORY);
	        int drug_price = Integer.parseInt(Global.parseAttribute(drug_name_, location_inventory));
	        int drug_quantity = Integer.parseInt(((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).getText().toString());
	        String game_info = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
	        int old_game_cash = Integer.parseInt(Global.parseAttribute("cash", game_info));
	        String new_game_info = Global.setAttribute(
	        		"cash", Integer.toString(old_game_cash + drug_quantity * drug_price), game_info);
	        String current_inventory = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY);
	        String current_drug_amount = Global.parseAttribute(drug_name_, current_inventory);
	        int max_space = Integer.parseInt(Global.parseAttribute("max_space", game_info));
	        int current_space = Integer.parseInt(Global.parseAttribute("space", game_info));
	        current_space = current_space + drug_quantity;
	        if (current_space > max_space) {
	        	current_space = max_space;
	        }
	        new_game_info = Global.setAttribute("space", Integer.toString(current_space), new_game_info);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, new_game_info);
	        int new_drug_quantity = Integer.parseInt(current_drug_amount) - drug_quantity;
	        String new_inventory = current_inventory;
	        if (new_drug_quantity == 0) {
	        	new_inventory = Global.removeAttribute(drug_name_, current_inventory);
		        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY, new_inventory);
	        } else {
		        new_inventory = Global.setAttribute(drug_name_, Integer.toString(new_drug_quantity), current_inventory);
		        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY, new_inventory);
	        }
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY, new_inventory);
	        dealer_data_.close();
	        refreshDisplay();
			dismissDialog(DIALOG_DRUG_SELL);
		}
		String drug_name_;
	}
	
	// Response to a change in location.
	public class ChangeLocationListener implements View.OnClickListener {
		public ChangeLocationListener(String new_location) {
			location_ = new_location;
		}
		public void onClick(View v) {
	        dealer_data_.open();
	        String game_info = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
	        String new_game_info = Global.setAttribute("location", location_, game_info);
	        int days_left = Integer.parseInt(Global.parseAttribute("days_left", new_game_info)) - 1;
	        new_game_info = Global.setAttribute("days_left", Integer.toString(days_left), new_game_info);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO, new_game_info);
	        dealer_data_.close();
			setupLocation();
			refreshDisplay();
			dismissDialog(DIALOG_SUBWAY);
		}
		String location_;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dealer_data_ = new DealerDataAdapter(this);
        
        // Set up the main content of the view.
        setContentView(R.layout.main_game_screen);
        
        setupLocation();
        refreshDisplay();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_SUBWAY) {
        	if (subway_dialog_ == null) {
        		subway_dialog_ = new Dialog(this);
        		subway_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		subway_dialog_.setContentView(R.layout.subway_layout);
        	}
            return subway_dialog_;
        } else if (id == DIALOG_INVENTORY) {
        	if (inventory_dialog_ == null) {
        		inventory_dialog_ = new Dialog(this);
        		inventory_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		inventory_dialog_.setContentView(R.layout.inventory_layout);
        	}
        	return inventory_dialog_;
        } else if (id == DIALOG_DRUG_BUY) {
        	if (drug_buy_dialog_ == null) {
        		drug_buy_dialog_ = new Dialog(this);
        		drug_buy_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		drug_buy_dialog_.setContentView(R.layout.drug_buy_layout);
                ((SeekBar)drug_buy_dialog_.findViewById(R.id.drug_quantity_slide)).setOnSeekBarChangeListener(
                		new SeekBar.OnSeekBarChangeListener() {
        			@Override
        			public void onProgressChanged(SeekBar seekBar, int progress,
        					boolean fromTouch) {
        				((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).setText(Integer.toString(progress));
        			}
        			@Override
        			public void onStartTrackingTouch(SeekBar seekBar) {}
        			@Override
        			public void onStopTrackingTouch(SeekBar seekBar) {}
                	
                });
        	}
        	
        	return drug_buy_dialog_;
        } else if (id == DIALOG_DRUG_SELL) {
        	if (drug_sell_dialog_ == null) {
        		drug_sell_dialog_ = new Dialog(this);
        		drug_sell_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		drug_sell_dialog_.setContentView(R.layout.drug_sell_layout);
                ((SeekBar)drug_sell_dialog_.findViewById(R.id.drug_quantity_slide)).setOnSeekBarChangeListener(
                		new SeekBar.OnSeekBarChangeListener() {
        			@Override
        			public void onProgressChanged(SeekBar seekBar, int progress,
        					boolean fromTouch) {
        				((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).setText(Integer.toString(progress));
        			}
        			@Override
        			public void onStartTrackingTouch(SeekBar seekBar) {}
        			@Override
        			public void onStopTrackingTouch(SeekBar seekBar) {}
                	
                });
        	}
        	
        	return drug_sell_dialog_;
        }
        return super.onCreateDialog(id);
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
		dealer_data_.open();
        if(id == DIALOG_SUBWAY) {
        	RelativeLayout l = (RelativeLayout)subway_dialog_.findViewById(R.id.subway_button_layout);
        	l.removeAllViews();
	        int num_locations = dealer_data_.getNumLocations();
	        for (int i = 0; i < num_locations; ++i) {
	        	String location_name = dealer_data_.getLocationName(i);
	        	String location_attributes = dealer_data_.getLocationAttributes(location_name);
	        	Button b = new Button(this);
	        	RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(90, 30);
	        	layout_params.leftMargin = Integer.parseInt(Global.parseAttribute(
	        			"map_x", location_attributes));
	        	layout_params.topMargin = Integer.parseInt(Global.parseAttribute(
	        			"map_y", location_attributes));
	        	b.setLayoutParams(layout_params);
	        	b.setText(location_name);
	        	b.setOnClickListener(new ChangeLocationListener(location_name));
	        	l.addView(b);
	        }
        	// this may not have to do anything other than the width setting
        	WindowManager.LayoutParams dialog_params =
        		subway_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	subway_dialog_.getWindow().setAttributes(dialog_params);
        } else if (id == DIALOG_INVENTORY) {
        	LinearLayout l = (LinearLayout)inventory_dialog_.findViewById(R.id.main_layout);
        	l.removeAllViews();
        	String drug_list = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INVENTORY);
        	int num_drugs = Global.attributeCount(drug_list);
        	for (int i = 0; i < num_drugs; ++i) {
        		String[] drug_info = Global.getAttribute(i, drug_list);
        		LinearLayout next_drug = new LinearLayout(this);
        		next_drug.setOrientation(LinearLayout.HORIZONTAL);
        		next_drug.setLayoutParams(new LinearLayout.LayoutParams(
            			LinearLayout.LayoutParams.WRAP_CONTENT,
            			LinearLayout.LayoutParams.WRAP_CONTENT));
        		TextView drug_name = new TextView(this);
        		drug_name.setLayoutParams(new LinearLayout.LayoutParams(
            			LinearLayout.LayoutParams.WRAP_CONTENT,
            			LinearLayout.LayoutParams.WRAP_CONTENT));
        		drug_name.setText(drug_info[0]);
        		next_drug.addView(drug_name);
        		TextView drug_quantity = new TextView(this);
        		drug_quantity.setLayoutParams(new LinearLayout.LayoutParams(
            			LinearLayout.LayoutParams.WRAP_CONTENT,
            			LinearLayout.LayoutParams.WRAP_CONTENT));
        		drug_quantity.setText(drug_info[1]);
        		next_drug.addView(drug_quantity);
        		l.addView(next_drug);
        	}
        } else if (id == DIALOG_DRUG_BUY) {
        	// Determine how many of the drug the user could buy
	        String game_info = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_GAME_INFO);
	        int cash = Integer.parseInt(Global.parseAttribute("cash", game_info));
	        int space = Integer.parseInt(Global.parseAttribute("space", game_info));
	        String inventory = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_LOCATION_INVENTORY);
	        int drug_price = Integer.parseInt(Global.parseAttribute(dialog_drug_name_, inventory));
	        int max_num_drugs = cash / drug_price;
	        max_num_drugs = Math.min(max_num_drugs, space);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(max_num_drugs);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(max_num_drugs);
	        ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setIndeterminate(false);
	        ((ImageView)(drug_buy_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new CompleteSaleListener(dialog_drug_name_));
        } else if (id == DIALOG_DRUG_SELL) {
        	// Determine how many of the drug the user could sell
        	String inventory = dealer_data_.getDealerString(
        			DealerDataAdapter.KEY_DEALER_GAME_INVENTORY);
        	int drug_quantity = Integer.parseInt(Global.parseAttribute(dialog_drug_name_, inventory));
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(drug_quantity);
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(drug_quantity);
	        ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setIndeterminate(false);
	        ((ImageView)(drug_sell_dialog_.findViewById(R.id.drug_icon))).setOnClickListener(
	        		new CompleteBuyListener(dialog_drug_name_));
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
        dealer_data_.open();
        String game_info = dealer_data_.getDealerString(
        		DealerDataAdapter.KEY_DEALER_GAME_INFO);
        String location_inventory = dealer_data_.getDealerString(
        		DealerDataAdapter.KEY_DEALER_LOCATION_INVENTORY);
        int numLocationDrugs = Global.attributeCount(location_inventory);
        int cash = Integer.parseInt(Global.parseAttribute("cash", game_info));
        int space = Integer.parseInt(Global.parseAttribute("space", game_info));
        String current_inventory = dealer_data_.getDealerString(
        		DealerDataAdapter.KEY_DEALER_GAME_INVENTORY);
		for (int i = 0; i < numLocationDrugs; ++i) {
        	// Construct the next button
			String[] drug = Global.getAttribute(i, location_inventory);
			int drug_price = Integer.parseInt(drug[1]);
			String current_drug_amount = Global.parseAttribute(drug[0], current_inventory);
        	LinearLayout next_drug = makeButton(R.drawable.btn_translucent_gray,
        			R.drawable.weed, drug[0],
        			"$" + Integer.toString(drug_price));
        	// Can buy
        	if ((cash > drug_price) && (space > 0)) {
        		// Can also sell
        		if (!current_drug_amount.equals("")) {
        		  next_drug.setBackgroundResource(R.drawable.btn_translucent_blue);
        		  next_drug.setOnLongClickListener(new SellDrugListenerLong(drug[0]));
        		// Can only buy
        		} else {
        			next_drug.setBackgroundResource(R.drawable.btn_translucent_green);
        		}
        		next_drug.setOnClickListener(new BuyDrugListener(drug[0]));
        	// Can only sell
        	} else if (!current_drug_amount.equals("")) {
        	  next_drug.setBackgroundResource(R.drawable.btn_translucent_orange);
      		  next_drug.setOnClickListener(new SellDrugListener(drug[0]));
        	}
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
		int days_left = Integer.parseInt(Global.parseAttribute("days_left", game_info));
		if (days_left > 0) {
	    	LinearLayout subway_button = makeButton(R.drawable.btn_translucent_gray,
	    			R.drawable.avatar1, "Subway", "[" + Integer.toString(days_left) + "]");
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
		}
        
        // Add inventory button
        LinearLayout inventory_button = makeButton(R.drawable.btn_translucent_gray,
        		R.drawable.avatar1, "Inventory", " ");
        inventory_button.setOnClickListener(new InventoryListener());
        inventory_button.measure(viewWidth, viewHeight);
    	if (inventory_button.getMeasuredWidth() + total_width_added > viewWidth) {
    		outer_layout.addView(current_row);
    		current_row = new LinearLayout(this);
    		current_row.setOrientation(LinearLayout.HORIZONTAL);
    		current_row.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.FILL_PARENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
    		total_width_added = 0;
    	}
    	total_width_added += inventory_button.getMeasuredWidth();
    	current_row.addView(inventory_button);
        if (total_width_added > 0) {
        	outer_layout.addView(current_row);
        }
        
        String current_dealer_name = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_NAME);
        String avatar_id = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
        dealer_data_.close();
        ((TextView)findViewById(R.id.player_name)).setText(current_dealer_name);
        ((TextView)findViewById(R.id.player_money)).setText("$" + Integer.toString(cash));
        ((ImageView)findViewById(R.id.avatar_image)).setImageResource(Integer.parseInt(avatar_id));
	}
	
	public void setupLocation() {
		// Determine the number and type of drugs available at the current location.
		dealer_data_.open();
        int num_avail_drugs = dealer_data_.numAvailableDrugs();
		boolean[] drug_present = new boolean[num_avail_drugs];
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		drug_present[i] = false;
    	}
    	int num_drugs_left = num_avail_drugs;
    	String game_info = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
    	String current_location = Global.parseAttribute("location", game_info);
    	String location_attributes = dealer_data_.getLocationAttributes(current_location);
    	int base_drugs_count = Integer.parseInt(Global.parseAttribute(
    			"base_drugs",location_attributes));
    	int drug_variance = Integer.parseInt(Global.parseAttribute(
    			"drug_variance",location_attributes));
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
    	
    	String location_drugs = "";
    	for (int i = 0; i < num_avail_drugs; ++i) {
    		if (drug_present[i]) {
    			String drug_name = dealer_data_.getDrugName(i);
    			if (location_drugs != "") {
    				location_drugs += "|";
    			}
    			location_drugs += drug_name + ":" + Global.chooseDrugPrice(dealer_data_.getDrugAttributes(drug_name));
    		}
    	}
    	dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_LOCATION_INVENTORY, location_drugs);
    	dealer_data_.close();
	}
	
	Dialog subway_dialog_;
	Dialog drug_buy_dialog_;
	Dialog drug_sell_dialog_;
	Dialog inventory_dialog_;
	
	String dialog_drug_name_;
	
	DealerDataAdapter dealer_data_;
}
