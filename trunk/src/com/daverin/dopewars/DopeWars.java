package com.daverin.dopewars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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
	public static final int DIALOG_DOWNLOAD_GAME = 1002;
	
	
	public class ResumeGameListener implements View.OnClickListener {
		public void onClick(View v) {
			dealer_data_.open();
			String game_info = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO);
			CurrentGameInformation current_game = new CurrentGameInformation(game_info);
			current_game.do_initial_setup_ = 0;
			dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
					current_game.serializeCurrentGameInformation());
			dealer_data_.close();
			Global.loadIcons();
			Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
	}
	
	public class ClearInProgressGameListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			dealer_data_.open();
			dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID, Integer.toString(-1));
			dealer_data_.close();
			updateUserInterface();
			return true;
		}
	}
	
	public class LoadGameSettingsListener implements View.OnLongClickListener {
		public LoadGameSettingsListener(int game) {
			game_ = game;
		}
		
		@Override
		public boolean onLongClick(View v) {
			dialog_game_ = game_;
			showDialog(DIALOG_DOWNLOAD_GAME);
			return true;
		}
		
		int game_;
	}
	
	public class DownloadGameListener implements View.OnClickListener {
		public DownloadGameListener(int game, String game_type) {
			game_ = game;
			game_type_ = game_type;
		}
		
		@Override
		public void onClick(View v) {
			try {
				String new_game_settings_string;
				if (game_type_.equals("classic mode")) {
					new_game_settings_string = "http://dopewars-android-2.appspot.com/game-settings-g1";
				} else if (game_type_.equals("extended mode")) {
					new_game_settings_string = "http://dopewars-android-2.appspot.com/game-settings-g2";
				} else {
					new_game_settings_string = "";
				}
	    		HttpURLConnection conn =
	    			(HttpURLConnection)(new URL(new_game_settings_string)).openConnection();
	    		conn.setDoInput(true);
	    		conn.setDoOutput(true);
	    		conn.connect();
	    		int response_code = conn.getResponseCode();
	    		if (response_code != -1) {
	    			if (conn.getInputStream() != null) {
	    	          BufferedReader reader = new BufferedReader(new InputStreamReader(
	    	        		conn.getInputStream()));
	    	          String settings = reader.readLine();
	    	          GameInformation g = new GameInformation(settings);
	    	          dealer_data_.open();
	    	          dealer_data_.setGameString(game_, settings);
	    	          dealer_data_.close();
	    	        }
	    	    }
	    	    conn.disconnect();
	    	    updateUserInterface();
    		} catch (IOException e) {
    			String a = e.getMessage();
    		}
			dismissDialog(DIALOG_DOWNLOAD_GAME);
		}
		
		int game_;
		String game_type_;
	}
	
	// This listener is attached to the game start buttons. For now it's a
	// hard-coded setup, eventually it should init from game settings from
	// xml files and stuff.
	public class GameStartListener implements View.OnClickListener {
		public GameStartListener(int game) {
			game_ = game;
		}
		
		public void onClick(View v) {
	        // A check that the dealer name and avatar id is set
			dealer_data_.open();
	        
	        // Clear the inventory, start with a standard amount of cash
	        // and debt. This stuff will eventually be determined by game settings.
	        GameInformation game_information = new GameInformation(dealer_data_.getGameString(game_));
	        String game_info_string = game_information.serialized_starting_game_info_;
	        game_info_string = game_info_string.replaceAll("##", "&&");
	        CurrentGameInformation game_info = new CurrentGameInformation(game_info_string);
	        game_info.do_initial_setup_ = 1;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_info.serializeCurrentGameInformation());
	        
	        // *** I'm not sure where the best place for this is yet ***
	        Global.loadIcons();
	        
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID, Integer.toString(game_));
	        dealer_data_.close();
	        
    		Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
		
		int game_;
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
        } else if (id == DIALOG_DOWNLOAD_GAME) {
        	if (download_game_dialog_ == null) {
        		download_game_dialog_ = new Dialog(this);
        		// Requesting no title has to happen before setting the content view.
        		download_game_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		download_game_dialog_.setContentView(R.layout.download_game_layout);
        		classic_mode_button_ = new Button(this);
        		classic_mode_button_.setText("Classic Mode");
        		((LinearLayout)download_game_dialog_.findViewById(R.id.download_layout)).addView(classic_mode_button_);
        		extended_mode_button_ = new Button(this);
        		extended_mode_button_.setText("Extended Mode");
        		((LinearLayout)download_game_dialog_.findViewById(R.id.download_layout)).addView(extended_mode_button_);
        	}
        	
        	return download_game_dialog_;
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
        } else if (id == DIALOG_DOWNLOAD_GAME) {
    		classic_mode_button_.setOnClickListener(new DownloadGameListener(dialog_game_, "classic mode"));
    		extended_mode_button_.setOnClickListener(new DownloadGameListener(dialog_game_, "extended mode"));
    		WindowManager.LayoutParams dialog_params =
        		download_game_dialog_.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	download_game_dialog_.getWindow().setAttributes(dialog_params);
        }
    }
	
	public void updateUserInterface() {
		// Set the interface elements according to the latest entry in the database.
		dealer_data_.open();
        String name = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_NAME);
        String avatar = dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
        
        String game_mode_1_name = new GameInformation(dealer_data_.getGameString(0)).game_id_;
        String game_mode_2_name = new GameInformation(dealer_data_.getGameString(1)).game_id_;
        String game_mode_3_name = new GameInformation(dealer_data_.getGameString(2)).game_id_;
        String game_mode_4_name = new GameInformation(dealer_data_.getGameString(3)).game_id_;
        
        int in_progress_game_id = Integer.parseInt(
        		dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID));
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
	    
	    ((LinearLayout)findViewById(R.id.edit_dealer)).setOnClickListener(new EditListener());
        if (in_progress_game_id == 0) {
            ((Button)findViewById(R.id.game_mode_1)).setText(game_mode_1_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_1)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_1)).setOnLongClickListener(new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_1)).setText(game_mode_1_name);
            ((Button)findViewById(R.id.game_mode_1)).setOnClickListener(new GameStartListener(0));
            ((Button)findViewById(R.id.game_mode_1)).setOnLongClickListener(new LoadGameSettingsListener(0));
        }
        if (in_progress_game_id == 1) {
            ((Button)findViewById(R.id.game_mode_2)).setText(game_mode_2_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_2)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_2)).setOnLongClickListener(new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_2)).setText(game_mode_2_name);
            ((Button)findViewById(R.id.game_mode_2)).setOnClickListener(new GameStartListener(1));
            ((Button)findViewById(R.id.game_mode_2)).setOnLongClickListener(new LoadGameSettingsListener(1));
        }
        if (in_progress_game_id == 2) {
            ((Button)findViewById(R.id.game_mode_3)).setText(game_mode_3_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_3)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_3)).setOnLongClickListener(new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_3)).setText(game_mode_3_name);
            ((Button)findViewById(R.id.game_mode_3)).setOnClickListener(new GameStartListener(2));
            ((Button)findViewById(R.id.game_mode_3)).setOnLongClickListener(new LoadGameSettingsListener(2));
        }
        if (in_progress_game_id == 3) {
            ((Button)findViewById(R.id.game_mode_4)).setText(game_mode_4_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_4)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_4)).setOnLongClickListener(new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_4)).setText(game_mode_4_name);
            ((Button)findViewById(R.id.game_mode_4)).setOnClickListener(new GameStartListener(3));
            ((Button)findViewById(R.id.game_mode_4)).setOnLongClickListener(new LoadGameSettingsListener(3));
        }
	}
	
	Dialog edit_dealer_dialog_;
	Dialog download_game_dialog_;
	Button classic_mode_button_;
	Button extended_mode_button_;
	DealerDataAdapter dealer_data_;
	int dialog_game_;
}

/*
GameInformation game_information = new GameInformation("");
game_information.drugs_.clear();
HashMap<String, Float> weed_info = new HashMap<String, Float>();
weed_info.put("base_price", (float)400);
weed_info.put("price_variance", (float)200);
weed_info.put("icon", (float)0);
weed_info.put("low_probability", (float)0.1);
weed_info.put("low_multiplier", (float)0.5);
game_information.drugs_.put("Weed", weed_info);
HashMap<String, Float> acid_info = new HashMap<String, Float>();
acid_info.put("base_price", (float)1500);
acid_info.put("price_variance", (float)400);
acid_info.put("icon", (float)1);
game_information.drugs_.put("Acid", acid_info);
HashMap<String, Float> ludes_info = new HashMap<String, Float>();
ludes_info.put("base_price", (float)80);
ludes_info.put("price_variance", (float)20);
ludes_info.put("icon", (float)2);
game_information.drugs_.put("Ludes", ludes_info);
HashMap<String, Float> heroin_info = new HashMap<String, Float>();
heroin_info.put("base_price", (float)10000);
heroin_info.put("price_variance", (float)2000);
heroin_info.put("icon", (float)3);
heroin_info.put("high_probability", (float)0.1);
heroin_info.put("high_multiplier", (float)0.5);
game_information.drugs_.put("Heroin", heroin_info);
HashMap<String, Float> cocaine_info = new HashMap<String, Float>();
cocaine_info.put("base_price", (float)20000);
cocaine_info.put("price_variance", (float)3000);
cocaine_info.put("icon", (float)4);
cocaine_info.put("high_probability", (float)0.1);
cocaine_info.put("high_multiplier", (float)2.0);
game_information.drugs_.put("Cocaine", cocaine_info);
HashMap<String, Float> shrooms_info = new HashMap<String, Float>();
shrooms_info.put("base_price", (float)1000);
shrooms_info.put("price_variance", (float)200);
shrooms_info.put("icon", (float)5);
game_information.drugs_.put("Shrooms", shrooms_info);
HashMap<String, Float> speed_info = new HashMap<String, Float>();
speed_info.put("base_price", (float)110);
speed_info.put("price_variance", (float)30);
speed_info.put("icon", (float)6);
game_information.drugs_.put("Speed", speed_info);
HashMap<String, Float> hash_info = new HashMap<String, Float>();
hash_info.put("base_price", (float)180);
hash_info.put("price_variance", (float)40);
hash_info.put("icon", (float)7);
hash_info.put("low_probability", (float)0.1);
hash_info.put("low_multiplier", (float)0.5);
game_information.drugs_.put("Hashish", hash_info);

game_information.coats_.clear();
HashMap<String, Float> gucci_info = new HashMap<String, Float>();
gucci_info.put("additional_space", (float)10);
gucci_info.put("base_price", (float)2000);
gucci_info.put("price_variance", (float)200);
gucci_info.put("space_factor", (float)0.2);
game_information.coats_.put("Gucci", gucci_info);
HashMap<String, Float> dg_info = new HashMap<String, Float>();
dg_info.put("additional_space", (float)20);
dg_info.put("base_price", (float)4000);
dg_info.put("price_variance", (float)400);
dg_info.put("space_factor", (float)0.4);
game_information.coats_.put("D&G", dg_info);

game_information.guns_.clear();
HashMap<String, Float> baretta_info = new HashMap<String, Float>();
baretta_info.put("firepower", (float)6.0);
baretta_info.put("base_price", (float)500);
baretta_info.put("price_variance", (float)0);
baretta_info.put("space", (float)5);
game_information.guns_.put("Baretta", baretta_info);
HashMap<String, Float> satnite_info = new HashMap<String, Float>();
satnite_info.put("firepower", (float)8.0);
satnite_info.put("base_price", (float)1000);
satnite_info.put("price_variance", (float)0);
satnite_info.put("space", (float)8);
game_information.guns_.put("Saturday Night Special", satnite_info);

game_information.locations_.clear();
HashMap<String, Float> brooklyn_info = new HashMap<String, Float>();
brooklyn_info.put("base_drugs", (float)6.0);
brooklyn_info.put("drug_variance", (float)1.0);
brooklyn_info.put("map_x", (float)105.0);
brooklyn_info.put("map_y", (float)220.0);
brooklyn_info.put("has_bank", (float)1.0);
brooklyn_info.put("has_loan_shark", (float)1.0);
game_information.locations_.put("Brooklyn", brooklyn_info);
HashMap<String, Float> bronx_info = new HashMap<String, Float>();
bronx_info.put("base_drugs", (float)8.0);
bronx_info.put("drug_variance", (float)2.0);
bronx_info.put("map_x", (float)80.0);
bronx_info.put("map_y", (float)5.0);
game_information.locations_.put("The Bronx", bronx_info);
HashMap<String, Float> ghetto_info = new HashMap<String, Float>();
ghetto_info.put("base_drugs", (float)8.0);
ghetto_info.put("drug_variance", (float)2.0);
ghetto_info.put("map_x", (float)73.0);
ghetto_info.put("map_y", (float)100.0);
game_information.locations_.put("The Ghetto", ghetto_info);
HashMap<String, Float> coney_island_info = new HashMap<String, Float>();
coney_island_info.put("base_drugs", (float)8.0);
coney_island_info.put("drug_variance", (float)2.0);
coney_island_info.put("map_x", (float)80.0);
coney_island_info.put("map_y", (float)335.0);
game_information.locations_.put("Coney Island", coney_island_info);
HashMap<String, Float> manhattan_info = new HashMap<String, Float>();
manhattan_info.put("base_drugs", (float)8.0);
manhattan_info.put("drug_variance", (float)2.0);
manhattan_info.put("map_x", (float)75.0);
manhattan_info.put("map_y", (float)143.0);
game_information.locations_.put("Manhattan", manhattan_info);
HashMap<String, Float> central_park_info = new HashMap<String, Float>();
central_park_info.put("base_drugs", (float)8.0);
central_park_info.put("drug_variance", (float)2.0);
central_park_info.put("map_x", (float)103.0);
central_park_info.put("map_y", (float)60.0);
game_information.locations_.put("Central Park", central_park_info);

game_information.loan_interest_rate_ = (float)0.1;
game_information.bank_interest_rate_ = (float)0.05;
game_information.coat_likelihood_ = (float)0.3;
game_information.gun_likelihood_ = (float)0.3;
game_information.cops_likelihood_ = (float)0.1;
*/