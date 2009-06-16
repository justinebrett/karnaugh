package edu.montclair.kmapview;

import android.app.Activity;
import android.os.Bundle;

public class KMapView extends Activity {
	// This activity is sent a Bundle by the KarnaughMap activity which
	// includes the number of variables the user would like to use.
	// This method pulls the number of variables from that bundle and
	// returns it.
	private int getNumberOfVariables() {
		Bundle extras = getIntent().getExtras();
		return extras.getInt("numvars");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kmap);

		KarnaughMapView kmv = (KarnaughMapView) this
				.findViewById(R.id.kmapView);
		kmv.setNumberOfVariables(this.getNumberOfVariables());
		kmv.setActivity(KMapView.this);
	}
}