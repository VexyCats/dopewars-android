package com.daverin.dopewars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DopeWars2 extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dopewars2_main_menu);
        ConnectButtonListeners();
    }
	
	// To start a new game, save a blank string as the "saved game", which
	// will cause loading that game to re-initialize everything.
	private class NewGameListener implements View.OnClickListener {
		public void onClick(View v) {
			DealerDataAdapter dealer_data =
				new DealerDataAdapter(v.getContext());
			dealer_data.open();
			dealer_data.setGameString("");
			dealer_data.close();
    		Intent i = new Intent(v.getContext(), DopeWars2Game.class);
       		startActivityForResult(i, 0);
		}
	}
	
	// To resume the current game, just start the game activity and it will
	// automatically load whatever's in the database as the "saved game".
	private class ResumeGameListener implements View.OnClickListener {
		public void onClick(View v) {
    		Intent i = new Intent(v.getContext(), DopeWars2Game.class);
       		startActivityForResult(i, 0);
		}
	}
	
	// Check whether a saved game exists or not.  A saved game exists if the
	// game in the saved game slot has any days left.
	private boolean SavedGameExists() {
		DealerDataAdapter dealer_data = new DealerDataAdapter(this);
		dealer_data.open();
		GameState game_state = new GameState(dealer_data.getGameString());
		dealer_data.close();
		return (game_state.days_left_ > 0);
	}
	
	// This adds button listeners to the new game and saved game buttons.
	private void ConnectButtonListeners() {
		((Button)findViewById(R.id.dopewars2_new_game)).setOnClickListener(
        		new NewGameListener());
		boolean resume_game_button_active = SavedGameExists();
		((Button)findViewById(R.id.dopewars2_resume_game)).setEnabled(
				resume_game_button_active);
		if (resume_game_button_active) {
			((Button)findViewById(R.id.dopewars2_resume_game)).
					setOnClickListener(new ResumeGameListener());
		}
	}
}
