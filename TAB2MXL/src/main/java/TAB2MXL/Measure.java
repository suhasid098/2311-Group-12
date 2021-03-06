package TAB2MXL;

import java.util.ArrayList;
import java.util.Collections;

public class Measure {
	private ArrayList<Note> notes;
	private int measureNumber;
	public static Attributes a;
	private int indexTotal;
	public int durationVal;


	public Measure(int measureNumber) {
		notes = new ArrayList<Note>();
		this.measureNumber = measureNumber;
	}

	public static void setAttributes(Attributes a) {
		Measure.a = a;
	}

	public void addNote(Note note) {
		notes.add(note);
	}

	@Override
	public String toString() {
		String mxl = "<measure number=\"" + measureNumber + "\">\n";

		if (measureNumber == 1) {
			mxl += a + "\n";
		}

		for (Note note : notes) {
			mxl += note + "\n";
		}

		mxl += "</measure>";

		return mxl;
	}

	public Note getNote(int index) {
		return notes.get(index);
	}

	public int size() {
		return notes.size();
	}

	public void sortArray() {
		Collections.sort(notes);

	}

	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	public int getIndexTotal() {
		return indexTotal;
	}
	
	public void setIndexTotal(int indexTotal) {
		this.indexTotal = indexTotal;
	}
}