package com.daverin.dopewars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DopeWars2 extends Activity {
	
	public class NewGameListener implements View.OnClickListener {
		public void onClick(View v) {
	        // This activity represents the playable game
    		Intent i = new Intent(v.getContext(), DopeWars2Game.class);
       		startActivityForResult(i, 0);
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dopewars2_main_menu);
        
        connectButtonListeners();
    }
	
	// This adds button listeners to the new game and saved game buttons.
	private void connectButtonListeners() {
		((Button)findViewById(R.id.dopewars2_new_game)).setOnClickListener(
        		new NewGameListener());
		// TODO: implement the resume game functionality
		//((Button)findViewById(R.id.dopewars2_resume_game)).setOnClickListener(
        //		new ResumeGameListener());
	}
}
