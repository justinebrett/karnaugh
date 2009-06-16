package edu.montclair.kmapview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class KarnaughMapView extends View {
	private Activity a;

	private static Formula formula = new Formula();
	// TAG for logging
	final String TAG = "KarnaughMapView";

	// These value maps hold the base 10 value of each cell's coordinate.
	// Coordinates are later converted into a byte array and passed onto
	// the Formula class.

	static private int[][] valueMap2Variables = { { 0, 1 }, { 2, 3 } };
	static private int[][] valueMap3Variables = { { 0, 1 }, { 2, 3 }, { 6, 7 },
			{ 4, 5 } };
	static private int[][] valueMap4Variables = { { 0, 1, 3, 2 },
			{ 4, 5, 7, 6 }, { 12, 13, 15, 14 }, { 8, 9, 11, 10 } };

	// TODO This has to come from a bundle
	static int valueMap[][] = valueMap4Variables;
	int cellUsage[][] = new int[valueMap.length][valueMap[0].length];

	// Cell measurement data...
	// Space between cells
	private float cellSpacing = 1;
	private float labelCellPadding = 10;

	// For cell labeling
	String cellString = new String();
	FontMetrics fm = new FontMetrics();
	static final int lowercase = 97;
	static final int uppercase = 65;

	// Paint objects used to draw the Karnaugh Map
	// These objects are initialized in the initKarnaughMapView method
	// which retrieves colors from the local colors.xml resource.
	private Paint labelCellTextPaint;
	private Paint labelCellBackgroundPaint;
	private Paint cellBackgroundPaint;
	private Paint cellEnabledBackgroundPaint;

	// *************************************************************************
	// Constructors

	// Constructor required for in-code creation
	public KarnaughMapView(Context context) {
		super(context);
		initKarnaughMapView();
	}

	// Constructor required for inflation from resource file
	public KarnaughMapView(Context context, AttributeSet ats, int defaultStyle) {
		super(context, ats, defaultStyle);
		initKarnaughMapView();
	}

	// Constructor required for inflation from resource file
	public KarnaughMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initKarnaughMapView();
	}

	// *************************************************************************
	// 

	// TODO This must be modified in order to
	public void setNumberOfVariables(int _numberOfVariables) {
		Rect r = new Rect();

		if (_numberOfVariables == 2)
			valueMap = valueMap2Variables;
		else if (_numberOfVariables == 3)
			valueMap = valueMap3Variables;
		else
			valueMap = valueMap4Variables;

		r.left = 0;
		r.top = 0;
		r.right = this.getHeight();
		r.bottom = this.getWidth();

		invalidate(r);
		invalidate(r);
	}

	// *************************************************************************
	//

	// Initialize Paint objects, etc.
	protected void initKarnaughMapView() {
		Resources r = this.getResources();

		setFocusable(true);
		setClickable(true);

		labelCellTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		labelCellTextPaint.setColor(r.getColor(R.color.label_cell_text_color));
		labelCellTextPaint.setTextSize(15);
		labelCellTextPaint.setTextAlign(Paint.Align.CENTER);

		labelCellBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		labelCellBackgroundPaint.setColor(r
				.getColor(R.color.label_cell_background_color));

		cellBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cellBackgroundPaint.setColor(r.getColor(R.color.cell_background_color));

		cellEnabledBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cellEnabledBackgroundPaint.setColor(r
				.getColor(R.color.cell_enabled_background_color));

		fm = labelCellTextPaint.getFontMetrics();

		// Set all cell usage flags to 0 (disabled)
		this.initializeCellUsage();
	}

	// *************************************************************************
	// Drawing Function

	@Override
	protected void onDraw(Canvas canvas) {

		Rect r = new Rect();

		// Creates the cells for the Karnaugh Map by looping through
		// valueMap[][].
		// Grid is created left -> right, top -> down.
		for (int verticalCell = 0; verticalCell < valueMap.length; verticalCell++) {
			for (int horizontalCell = 0; horizontalCell < valueMap[verticalCell].length; horizontalCell++) {

				if (verticalCell == 0) {
					r = this
							.getXAxisLabelCellRect(horizontalCell, verticalCell);
					canvas.drawRect(r, labelCellBackgroundPaint);
					cellString = KarnaughMapView.createXLabel(verticalCell,
							horizontalCell, valueMap.length);
					canvas.drawText(cellString, (r.right + r.left) / 2,
							(r.top + r.bottom) / 2 - (fm.ascent + fm.descent)
									/ 2, labelCellTextPaint);
				}

				if (horizontalCell == 0) {
					r = this
							.getYAxisLabelCellRect(horizontalCell, verticalCell);
					canvas.drawRect(r, labelCellBackgroundPaint);
					cellString = KarnaughMapView.createYLabel(verticalCell,
							horizontalCell, valueMap.length);
					canvas.drawText(cellString, (r.right + r.left) / 2,
							(r.top + r.bottom) / 2 - (fm.ascent + fm.descent)
									/ 2, labelCellTextPaint);
				}

				r = this.getCellRect(horizontalCell, verticalCell);
				if (cellUsage[verticalCell][horizontalCell] == 0)
					canvas.drawRect(r, cellBackgroundPaint);
				else
					canvas.drawRect(r, cellEnabledBackgroundPaint);
			}
		}

		canvas.save();
	}

	// *************************************************************************
	// Methods used to calculate cell coordinates.

	private float getLabelCellWidth() {
		final Rect r = new Rect();
		labelCellTextPaint.getTextBounds("WW", 0, 2, r);
		return 30;
		// return Math.abs(r.top);
	}

	// Comment this
	private Rect getXAxisLabelCellRect(int valueMapPosX, int valueMapPosY) {
		Rect r = new Rect();

		r.left = (int) (valueMapPosX * this.getCellWidth() + cellSpacing + this
				.getLabelCellWidth());
		r.right = (int) (valueMapPosX * this.getCellWidth()
				+ this.getCellWidth() + this.getLabelCellWidth());
		r.top = (int) (valueMapPosY * this.getCellWidth() + cellSpacing);
		r.bottom = (int) (valueMapPosY * this.getCellWidth() + this
				.getLabelCellWidth());

		return r;
	}

	// Comment this
	private Rect getYAxisLabelCellRect(int valueMapPosX, int valueMapPosY) {
		Rect r = new Rect();

		r.left = (int) (valueMapPosX * this.getCellWidth() + cellSpacing);
		r.right = (int) (valueMapPosX * this.getCellWidth() + this
				.getLabelCellWidth());
		r.top = (int) (valueMapPosY * this.getCellWidth()
				+ this.getLabelCellWidth() + cellSpacing);
		r.bottom = (int) (valueMapPosY * this.getCellWidth()
				+ this.getCellWidth() + this.getLabelCellWidth());

		return r;
	}

	// Calculates the size of cells so they are evenly divided within the grid.
	private float getCellWidth() {
		return (this.getHeight() - (valueMap.length * cellSpacing) - (this
				.getLabelCellWidth()))
				/ valueMap.length;
	}

	// Comment this
	private Rect getCellRect(float valueMapPosX, float valueMapPosY) {
		Rect r = new Rect();

		r.left = (int) (valueMapPosX * this.getCellWidth() + cellSpacing + this
				.getLabelCellWidth());
		r.right = (int) (valueMapPosX * this.getCellWidth()
				+ this.getCellWidth() + this.getLabelCellWidth());
		r.top = (int) (valueMapPosY * this.getCellWidth() + cellSpacing + this
				.getLabelCellWidth());
		r.bottom = (int) (valueMapPosY * this.getCellWidth()
				+ this.getCellWidth() + this.getLabelCellWidth());

		return r;
	}

	// *************************************************************************
	// Methods to generate cell labels

	public static String createYLabel(int verticalCell, int horizontalCell,
			int size) {
		String str = "";

		// TODO change valueMap2Variables to valueMap
		byte[] array = Formula.convertIntToByteArray(
				KarnaughMapView.valueMap[verticalCell][horizontalCell], size);

		if (size % 2 == 1)
			size = size / 2 + 1;
		else
			size = size / 2;

		if (verticalCell == 0 && horizontalCell == 0) {
			for (int i = 0; i < size; i++)
				str = str + (char) (i + lowercase);
			return str;
		} else {
			for (int j = 0; j < size; j++) {
				if (array[j] == 0)
					str = str + (char) (j + lowercase);
				else
					str = str + (char) (j + uppercase);
			}
		}
		return str;
	}

	private static String createXLabel(int verticalCell, int horizontalCell,
			int size) {
		String str = "";
		int newSize;

		// TODO change valueMap2Variables to valueMap
		byte[] array = Formula.convertIntToByteArray(
				KarnaughMapView.valueMap[verticalCell][horizontalCell], size);

		if (size % 2 == 1)
			newSize = size / 2 + 1;
		else
			newSize = size / 2;

		if (verticalCell == 0 && horizontalCell == 0) {
			for (int i = newSize; i < size; i++)
				str = str + (char) (i + lowercase);
			return str;
		} else {
			for (int j = newSize; j < size; j++) {
				if (array[j] == 0)
					str = str + (char) (j + lowercase);
				else
					str = str + (char) (j + uppercase);
			}
		}
		return str;
	}

	// *************************************************************************
	// View measurement methods

	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
		int measuredHeight = measureHeight(hMeasureSpec);
		int measuredWidth = measureWidth(wMeasureSpec);

		// MUST make this call to setMeasureDimension
		// or you will cause a runtime exception when
		// the control is laid out.
		setMeasuredDimension(measuredHeight, measuredWidth);
	}

	// Set view height according to parent restrictions
	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = 100;

		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			// If your control can fit within these bounds return that view.
			result = specSize;
		}

		return result;
	}

	// Set view width according to parent restrictions
	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = 100;

		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			// If your control can fit within these bounds return that view.
			result = specSize;
		}

		return result;
	}

	// *************************************************************************
	// Input event methods

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		// Return true if the event was handled.
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
		// Return true if the event was handled.
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// Get the type of action this event represents
		int actionPerformed = event.getAction();
		// Return true if the event was handled.
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Rect r = new Rect();
		// Get the type of action this event represents
		int actionPerformed = event.getAction();

		float xPos = (int) event.getX();
		float yPos = (int) event.getY();

		TextView tv = (TextView) a.findViewById(R.id.SimplifiedExpressionText);
		KarnaughMapView kmv = (KarnaughMapView) a.findViewById(R.id.kmapView);

		// TODO This was a quick and dirty way of figuring out which cell our
		// motion event took place within. Most likely a better way to do this.
		if (actionPerformed == MotionEvent.ACTION_UP) {
			for (int verticalCell = 0; verticalCell < valueMap.length; verticalCell++) {
				for (int horizontalCell = 0; horizontalCell < valueMap[verticalCell].length; horizontalCell++) {
					r = this.getCellRect(horizontalCell, verticalCell);
					if (isInBounds(r, xPos, yPos)) {
						// Toggle cell usage. 0 = disabled, 1 = enabled
						if (cellUsage[verticalCell][horizontalCell] == 0) {
							formula.addTerm(Formula.convertIntToByteArray(
									valueMap[verticalCell][horizontalCell],
									valueMap.length));
							invalidate(r);
							cellUsage[verticalCell][horizontalCell] = 1;
							invalidate(r);
						} else {
							formula.removeTerm(Formula.convertIntToByteArray(
									valueMap[verticalCell][horizontalCell],
									valueMap.length));
							invalidate(r);
							cellUsage[verticalCell][horizontalCell] = 0;
							invalidate(r);
						}
						formula.reduceToPrimeImplicants();
						formula.reducePrimeImplicantsToSubset();
						tv.setText(formula.toString());
					}
				}
			}
		}

		// Return true if the event was handled.
		return true;
	}

	private boolean isInBounds(Rect r, float xPos, float yPos) {
		if (xPos >= r.left && xPos <= r.right && yPos >= r.top
				&& yPos <= r.bottom)
			return true;
		else
			return false;
	}

	private void initializeCellUsage() {
		for (int verticalCell = 0; verticalCell < valueMap.length; verticalCell++) {
			for (int horizontalCell = 0; horizontalCell < valueMap[verticalCell].length; horizontalCell++) {
				cellUsage[verticalCell][horizontalCell] = 0;
			}
		}
	}

	public int[][] getCellUsage() {
		return cellUsage;
	}

	public void setActivity(Activity _a) {
		a = _a;
	}
}