package edu.montclair.kmapview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainScreen extends Activity implements OnClickListener {
	// onClick handler used by the buttons found in the main view which allow
	// the user to select the number of variables to work with
	public void onClick(View v) {
		// Starts the Kmap activity
		Intent i = new Intent(this, KMapView.class);

		switch (v.getId()) {
		case R.id.numberOfVariables2:
			i.putExtra("numvars", 2);
			startActivity(i);
			break;
		case R.id.numberOfVariables3:
			i.putExtra("numvars", 3);
			startActivity(i);
			break;
		case R.id.numberOfVariables4:
			i.putExtra("numvars", 4);
			startActivity(i);
			break;
		case R.id.about:
			i = new Intent(this, About.class);
			startActivity(i);
			break;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set the onClickListeners for each of the three buttons found on the
		// main view.
		View numberOfVariables2Button = this
				.findViewById(R.id.numberOfVariables2);
		numberOfVariables2Button.setOnClickListener(this);

		View numberOfVariables3Button = this
				.findViewById(R.id.numberOfVariables3);
		numberOfVariables3Button.setOnClickListener(this);

		View numberOfVariables4Button = this
				.findViewById(R.id.numberOfVariables4);
		numberOfVariables4Button.setOnClickListener(this);

		View aboutButton = this.findViewById(R.id.about);
		aboutButton.setOnClickListener(this);
	}
}