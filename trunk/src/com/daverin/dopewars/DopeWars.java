/**
 * This Activity represents the Main Menu for DopeWars 2. The main menu has four game buttons and
 * one dealer button. The game buttons behave as follows:
 * 
 * If the currently in progress game was started from that button, then:
 *   - a click will resume the in progress game
 *   - a hold will clear the in progress game
 * 
 * Otherwise:
 *   - a click will start a new game from those settings
 *   - a hold will bring up a dialog to change the settings assigned to that button
 * 
 * The settings for buttons all come from network communication. There is a small group of
 * hard-coded locations for canonical game types, but an arbitrary URL can be provided to load any
 * desired game settings.
 * 
 * The dealer button, when clicked, allows the user to edit the name and avatar of their current
 * dealer. The name and avatar are used when posting high scores, etc.
 */
package com.daverin.dopewars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	// There are only two dialogs for this activity, one to edit the name and avatar of the dealer
	// and one to change the game settings for one of the game buttons.
	public static final int DIALOG_EDIT_DEALER = 1001;
	public static final int DIALOG_DOWNLOAD_GAME = 1002;
	
	// These are strings used to identify options.
	public static final String CLASSIC_MODE = "Classic Mode";
	public static final String CLASSIC_MODE_LOCATION =
		"http://dopewars-android-2.appspot.com/game-settings-g1-classic";
	public static final String EXTENDED_MODE = "Extended Mode";
	public static final String EXTENDED_MODE_LOCATION =
		"http://dopewars-android-2.appspot.com/game-settings-g1-extended";
	
	// When the currently saved game was started from a button, a click on the button will be sent
	// to this handler, which resumes the saved game without any of the initialization that
	// normally happens when starting a game.
	public class ResumeGameListener implements View.OnClickListener {
		public void onClick(View v) {
			
			// Set the special run-time-only flag to avoid doing initialization when starting the
			// game.
			dealer_data_.open();
			GameInformation game_info = new GameInformation("");
			game_info.setCurrentGameInformation(
					dealer_data_.getDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO));
			game_info.do_initial_setup_ = 0;
			dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
					game_info.getCurrentGameInformation());
			dealer_data_.close();
			
			// This activity represents the playable game.
			Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
	}
	
	// When the currently saved game was started from a button, a long click on the button will be
	// sent to this handler, which clears the saved game.
	public class ClearInProgressGameListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			
			// An easy way to clear the game info is to set the id associated with the current
			// game to an invalid value (-1) so it won't match any of the buttons and the game
			// can't be resumed.
			dealer_data_.open();
			dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID,
					Integer.toString(-1));
			dealer_data_.close();
			updateUserInterface();
			return true;
		}
	}
	
	// When the button is not associated with the currently saved game, a click on the button will
	// start a new game using the settings associated with that button.
	public class GameStartListener implements View.OnClickListener {
		public GameStartListener(int game) {
			game_ = game;
		}
		
		public void onClick(View v) {
			
			// Reset the game's initial state according to the initial state specified by the
			// stored game settings, making sure that the run time flag for initializing the
			// game is set.
			dealer_data_.open();
	        GameInformation game_information = new GameInformation(
	        		dealer_data_.getGameString(game_));
	        String game_info_string = game_information.serialized_starting_game_info_;
	        game_info_string = game_info_string.replaceAll("##", "&&");
	        game_information.setCurrentGameInformation(game_info_string);
	        game_information.do_initial_setup_ = 1;
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_INFO,
	        		game_information.getCurrentGameInformation());
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_GAME_ID,
	        		Integer.toString(game_));
	        dealer_data_.close();
	        
	        // This activity represents the playable game
    		Intent i = new Intent(v.getContext(), DopeWarsGame.class);
       		startActivityForResult(i, 0);
		}
		
		int game_;
	}
	
	// When the button is not associated with the currently saved game, a long press on the button
	// will open up a dialog to change the game settings associated with the button.
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
	
	// After a long click on a button to change the game settings, the dialog that comes up has
	// two types of buttons, pre-set buttons and one button to load settings from an entered
	// URL. They all use this listener, but the pre-set buttons will have a non-empty
	// game_location string, while the button for an entered URL will have an empty game_location
	// and this listener is responsible for retrieving the URL from the dialog.
	public class DownloadGameListener implements View.OnClickListener {
		public DownloadGameListener(String game_location) {
			game_location_ = game_location;
		}
		
		@Override
		public void onClick(View v) {
			try {
				String new_game_settings_string;
				if (game_location_.equals(CLASSIC_MODE)) {
					new_game_settings_string = CLASSIC_MODE_LOCATION;
				} else if (game_location_.equals(EXTENDED_MODE)) {
					new_game_settings_string = EXTENDED_MODE_LOCATION;
				} else {
					new_game_settings_string = 
						((Button)download_game_dialog_.findViewById(R.id.custom_url))
						.getText().toString();
				}
				
				// TODO: revisit these settings to tweak this connection to act nice.
	    		
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
	    	          
	    	          // TODO: need a verification function that isn't a crash :P
	    	          GameInformation g = new GameInformation(settings);
	    	          
	    	          dealer_data_.open();
	    	          dealer_data_.setGameString(dialog_game_, settings);
	    	          dealer_data_.close();
	    	        }
	    	    }
	    	    conn.disconnect();
	    	    updateUserInterface();
    		} catch (IOException e) {
    			// TODO: learn android logging and make sure this is logged nicely
    		}
			dismissDialog(DIALOG_DOWNLOAD_GAME);
		}
		
		String game_location_;
	}

	// A click on the dealer button will cause the edit dealer dialog to open.
	public class EditListener implements View.OnClickListener {
		public void onClick(View v) {
			showDialog(DIALOG_EDIT_DEALER);
		}
	}

	// A click on the cancel button in the edit dealer dialog will just hide the dialog without
	// effecting any changes.
	public class CancelEditListener implements View.OnClickListener {
		public void onClick(View v) {
			dismissDialog(DIALOG_EDIT_DEALER);
		}
	}
	
	// A click on the confirm button in the edit dealer dialog will load the new dealer name and
	// avatar into the database.
	public class ConfirmEditListener implements View.OnClickListener {
		public void onClick(View v) {
			String new_dealer_name = ((TextView)edit_dealer_dialog_.
					findViewById(R.id.dealer_name)).getText().toString();
			
			// TODO: There's got to be a way to do this with a radio group?
			String avatar_id = "0";
			if (((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar2)).isChecked()) {
				avatar_id = "1";
			} else if (((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar3)).isChecked()) {
				avatar_id = "2";
			}
			
			// Enter the new name and avatar into the database.
			dealer_data_.open();
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_NAME, new_dealer_name);
	        dealer_data_.setDealerString(DealerDataAdapter.KEY_DEALER_AVATAR_NAME, avatar_id);
        	dealer_data_.close();
        	
        	// Close the dialog and update the interface so the new name and avatar show up.
			dismissDialog(DIALOG_EDIT_DEALER);
			updateUserInterface();
		}
	}
	
	// When the main menu activity is created there just needs to be a connection to the database
	// and an update of the UI.
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dealer_data_ = new DealerDataAdapter(this);
        setContentView(R.layout.main_menu);
		updateUserInterface();
    }
	
	// The dialogs are kept around as member functions as soon as they're created once.
	@Override
    protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_EDIT_DEALER:
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
		case DIALOG_DOWNLOAD_GAME:
			if (download_game_dialog_ == null) {
        		download_game_dialog_ = new Dialog(this);
        		
        		// Requesting no title has to happen before setting the content view.
        		download_game_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        		download_game_dialog_.setContentView(R.layout.download_game_layout);
            	((Button)download_game_dialog_.findViewById(R.id.classic_mode_button)).
            	    setOnClickListener(new DownloadGameListener(CLASSIC_MODE));
            	((Button)download_game_dialog_.findViewById(R.id.extended_mode_button)).
        	    	setOnClickListener(new DownloadGameListener(EXTENDED_MODE));
            	((Button)download_game_dialog_.findViewById(R.id.custom_mode_button)).
        	    	setOnClickListener(new DownloadGameListener(""));
        	}
        	return download_game_dialog_;
		default:
			return super.onCreateDialog(id);
	    }
	}

	// Dialog preparation for display, this is where the data on dialogs is inserted.
	// TODO: This is the only place I've been able to set the dialog width since the cupcake
	// release, there might be better/more static ways, especially when donut comes around.
	@Override
    protected void onPrepareDialog(int id, Dialog d) {
		Dialog current_dialog = null;
		switch(id) {
		case DIALOG_EDIT_DEALER:
			current_dialog = edit_dealer_dialog_;
			// Populate the dialog with the current dealer name and avatar.
			dealer_data_.open();
	        String current_dealer_name = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_NAME);
	        String avatar_id = dealer_data_.getDealerString(
	        		DealerDataAdapter.KEY_DEALER_AVATAR_NAME);
            dealer_data_.close();
        	((TextView)edit_dealer_dialog_.findViewById(R.id.dealer_name)).setText(
        			current_dealer_name);
        	
        	// TODO: find a nicer way to handle avatar id's like this.
	        if (avatar_id.equals("1")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar2)).setChecked(true);
			} else if (avatar_id.equals("2")) {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar3)).setChecked(true);
			} else {
				((RadioButton)edit_dealer_dialog_.findViewById(R.id.avatar1)).setChecked(true);
			}
        	break;
		case DIALOG_DOWNLOAD_GAME:
			current_dialog = download_game_dialog_;
        	break;
		}
		if (current_dialog != null) {
			WindowManager.LayoutParams dialog_params =
				current_dialog.getWindow().getAttributes();
        	dialog_params.width = WindowManager.LayoutParams.FILL_PARENT;
        	current_dialog.getWindow().setAttributes(dialog_params);
		}
    }
	
	// Update the user interface to reflect the current state of the data. This covers the dealer
	// name and avatar, the names of the game settings that are attached to each button, and
	// which of the buttons, if any, is connected to the in-progress game.
	private void updateUserInterface() {
		// Get all relevant information out of the database.
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
        
        // Set the dealer name and avatar. TODO: Handle the avatar id's better.
	    ((TextView)findViewById(R.id.dealer_name)).setText(name);
	    if (avatar.equals("1")) {
	    	((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar2);
		} else if (avatar.equals("2")) {
			((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar3);
		} else {
			((ImageView)findViewById(R.id.avatar_image)).setImageResource(R.drawable.avatar1);
		}
	    ((LinearLayout)findViewById(R.id.edit_dealer)).setOnClickListener(new EditListener());
	    
	    // Set the appropriate text and listeners for each of the buttons. The text and listeners
	    // depend on whether or not the button is attached to the in-progress game.
	    // TODO: code repetition is ugly
        if (in_progress_game_id == 0) {
            ((Button)findViewById(R.id.game_mode_1)).setText(
            		game_mode_1_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_1)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_1)).setOnLongClickListener(
            		new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_1)).setText(game_mode_1_name);
            ((Button)findViewById(R.id.game_mode_1)).setOnClickListener(new GameStartListener(0));
            ((Button)findViewById(R.id.game_mode_1)).setOnLongClickListener(
            		new LoadGameSettingsListener(0));
        }
        if (in_progress_game_id == 1) {
            ((Button)findViewById(R.id.game_mode_2)).setText(
            		game_mode_2_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_2)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_2)).setOnLongClickListener(
            		new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_2)).setText(game_mode_2_name);
            ((Button)findViewById(R.id.game_mode_2)).setOnClickListener(new GameStartListener(1));
            ((Button)findViewById(R.id.game_mode_2)).setOnLongClickListener(
            		new LoadGameSettingsListener(1));
        }
        if (in_progress_game_id == 2) {
            ((Button)findViewById(R.id.game_mode_3)).setText(
            		game_mode_3_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_3)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_3)).setOnLongClickListener(
            		new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_3)).setText(game_mode_3_name);
            ((Button)findViewById(R.id.game_mode_3)).setOnClickListener(new GameStartListener(2));
            ((Button)findViewById(R.id.game_mode_3)).setOnLongClickListener(
            		new LoadGameSettingsListener(2));
        }
        if (in_progress_game_id == 3) {
            ((Button)findViewById(R.id.game_mode_4)).setText(
            		game_mode_4_name + "\ntap to continue\nhold to reset");
            ((Button)findViewById(R.id.game_mode_4)).setOnClickListener(new ResumeGameListener());
            ((Button)findViewById(R.id.game_mode_4)).setOnLongClickListener(
            		new ClearInProgressGameListener());
        } else {
        	((Button)findViewById(R.id.game_mode_4)).setText(game_mode_4_name);
            ((Button)findViewById(R.id.game_mode_4)).setOnClickListener(new GameStartListener(3));
            ((Button)findViewById(R.id.game_mode_4)).setOnLongClickListener(
            		new LoadGameSettingsListener(3));
        }
	}
	
	Dialog edit_dealer_dialog_;
	Dialog download_game_dialog_;
	DealerDataAdapter dealer_data_;
	int dialog_game_;
}