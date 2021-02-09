package TAB2MXL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TabReader {
	private ArrayList<Measure> measureElements = new ArrayList<Measure>();
	private List<String> tabArray = new ArrayList<String>();
	private File inputTabFile;
	private String outputXMLFile;
	private List<ArrayList<String>> allMeasures = new ArrayList<ArrayList<String>>();
	private List<Character> guitarTuning = new ArrayList<Character>();

	public static void main(String[] args) {
		TabReader reader = new TabReader("src/main/java/TAB2MXL/StairwayHeaven.txt");

		// print to see all measures
		for (Measure m : reader.getMeasures()) {
			System.out.println(m);
		}
	}

	public TabReader(String inputFile) {
		inputTabFile = new File(inputFile);
		outputXMLFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML 3.1 Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\r\n"
				+ "<score-partwise version=\"3.1\">\r\n" + "  <work>\r\n" + "    <work-title>Good Copy</work-title>\r\n"
				+ "    </work>\r\n" + "  <part-list>\r\n" + "    <score-part id=\"P1\">\r\n"
				+ "      <part-name>Guitar</part-name>\r\n" + "      </score-part>\r\n" + "    </part-list>\r\n"
				+ "  <part id=\"P1\">";
		tabArray = readFile(inputTabFile);

		setTuning(tabArray);
		allMeasures = splitMeasure();
		makeNotes();
		
	}

	public ArrayList<Measure> getMeasures() {
		return this.measureElements;
	}

	public List<String> readFile(File inputFile) {
		List<String> tabArray = new ArrayList<String>();
		Scanner sc = null;

		try {
			sc = new Scanner(inputFile);
			while (sc.hasNextLine()) {
				tabArray.add(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

		return tabArray;
	}

	public void setTuning(List<String> tabArray) {

		for (int i = 0; i < 6; i++) {
			char tuneVal = this.tabArray.get(i).charAt(0);
			guitarTuning.add(tuneVal);
		}

		//for (int i = 0; i < this.tabArray.size(); i++) {

			//String trimmedLine = this.tabArray.get(i).substring(1, this.tabArray.get(i).length());
			//this.tabArray.set(i, trimmedLine);

		//}
	}

	public void makeMeasures(List<String> tabArray) {
		ArrayList<String> Measure = new ArrayList<String>();
		int startingIndex = 6;

		for (int j = 0; j < 6; j++) {
			String currentElement = this.tabArray.get(j);

			int index1 = currentElement.indexOf('|');
			int index2 = currentElement.indexOf('|', index1 + 1);
			String lineSegment = currentElement.substring(index1 + 1, index2);
			Measure.add(lineSegment);
			String replaceLine = currentElement.substring(index2);
			this.tabArray.set(j, replaceLine);
		}
		allMeasures.add(Measure);
		if (this.tabArray.get(0).indexOf('|', 1) != -1) {
			makeMeasures(this.tabArray);
		}

		if (this.tabArray.size() > startingIndex)
			makeMeasures(this.tabArray, startingIndex);

	}

	public void makeMeasures(List<String> tabArray, int startingIndex) {
		ArrayList<String> Measure = new ArrayList<String>();

		for (int j = startingIndex; j < startingIndex + 6; j++) {
			String currentElement = this.tabArray.get(j);

			int index1 = currentElement.indexOf('|');
			int index2 = currentElement.indexOf('|', index1 + 1);
			String lineSegment = currentElement.substring(index1 + 1, index2);
			Measure.add(lineSegment);
			String replaceLine = currentElement.substring(index2);
			this.tabArray.set(j, replaceLine);
		}
		allMeasures.add(Measure);
		if (this.tabArray.get(0).indexOf('|', 1) != -1) {
			makeMeasures(this.tabArray);
		}

		if (this.tabArray.size() > startingIndex + 6)
			makeMeasures(this.tabArray, startingIndex + 6);
	}

	public void makeNotes() {
		for (int i = 0; i < allMeasures.size(); i++) {
			Measure measure = new Measure(i + 1);
			int noteCounter = 0;
			for (int j = 0; j < 6; j++) {
				String currentLine = allMeasures.get(i).get(j);
				String temp;
				for (int k = 0; k < currentLine.length(); k++) {

					if (currentLine.charAt(k) != '-') {

						if (currentLine.charAt(k) == 'p' || currentLine.charAt(k) == 'h' || currentLine.charAt(k) == 's'
								|| currentLine.charAt(k) == '/') {

							if (currentLine.charAt(k) == 'p')
								measure.getNote(noteCounter - 1).slurStart = true;

							if (currentLine.charAt(k) == 'h')
								measure.getNote(noteCounter - 1).tieStart = true;

							if (currentLine.charAt(k) == 's' || currentLine.charAt(k) == '/')
								measure.getNote(noteCounter - 1).slideStart = true;

							continue;

						}

						if (currentLine.charAt(k + 1) != '-') {
							if (currentLine.charAt(k + 1) == '0' || currentLine.charAt(k + 1) == '1'
									|| currentLine.charAt(k) == '2' || currentLine.charAt(k + 1) == '3'
									|| currentLine.charAt(k + 1) == '4' || currentLine.charAt(k + 1) == '5'
									|| currentLine.charAt(k + 1) == '6' || currentLine.charAt(k + 1) == '7'
									|| currentLine.charAt(k + 1) == '8' || currentLine.charAt(k + 1) == '9') {
								temp = currentLine.substring(k, k + 2);
								int fret = Integer.valueOf(temp);
								Note note = new Note(j + 1, Character.toString(guitarTuning.get(j)).toUpperCase(), fret,
										k);
								measure.addNote(note);
								noteCounter++;
								k++;

								if (measure.size() > 1) {
									if (measure.getNote(noteCounter - 2).slurStart)
										note.slurStop = true;

									if (measure.getNote(noteCounter - 2).tieStart)
										note.tieStop = true;

									if (measure.getNote(noteCounter - 2).slideStart)
										note.slideStop = true;
								}

								continue;
							}
						}

						if (currentLine.charAt(k + 1) == '-' || currentLine.charAt(k + 1) == 'p'
								|| currentLine.charAt(k + 1) == 'h' || currentLine.charAt(k + 1) == 's'
								|| currentLine.charAt(k + 1) == '/') {
							temp = currentLine.substring(k, k + 1);
							int fret = Integer.valueOf(temp);
							Note note = new Note(j + 1, Character.toString(guitarTuning.get(j)).toUpperCase(), fret, k);
							measure.addNote(note);
							noteCounter++;
							if (measure.size() > 1) {
								if (measure.getNote(noteCounter - 2).slurStart)
									note.slurStop = true;

								if (measure.getNote(noteCounter - 1).tieStart)
									note.tieStop = true;

								if (measure.getNote(noteCounter - 1).slideStart)
									note.slideStop = true;
							}

						}
					}

				}
			}
			noteCounter = 0;
			measureElements.add(measure);

		}
	}

	public List<ArrayList<String>> splitMeasure(){
		List<ArrayList<String>> split = new ArrayList<ArrayList<String>>();
		HashMap<Integer, String> measure = new HashMap<Integer, String>();
		String line = "";
		int numberOfLines = Integer.valueOf(line);
		for(int i =0; i<6; i++) {
			line = tabArray.get(i);
			String[] lineArray = line.split("\\|");
			for(int j=1; j<lineArray.length;j++) {
				if (measure.containsKey(j)) {
					measure.put(j,measure.get(j)+ lineArray[j]+"\n");
				}
				else {
					measure.put(j,lineArray[j]+"\n");
				}
			}
		}
		
		for(int i=1; i <= measure.size();i++) {
			String string = measure.get(i);
			ArrayList<String> splitMeasure= new ArrayList<String>();
			for(String s : string.split("\n")) {
				splitMeasure.add(s);
			}
			split.add(splitMeasure);
		}
		
		return split;
	}
	
	public ArrayList<Integer> countBars() {
 		ArrayList<Integer> countArray = new ArrayList<>();
 		for (int i = 0; i < tabArray.size();i++) {
 			if (tabArray.get(i).contains("---") ) {
 				ArrayList<Integer> indices = new ArrayList<Integer>();
 				int index = 0;
 				while((index = tabArray.get(i).indexOf('|', index+1))>0) {
 				 indices.add(index);	
 				}
 				for (int j = 0; j < indices.size()-1;j++) {
 					String lineBefore = "";
 					lineBefore = tabArray.get(i-1);
 					int lastIndex = indices.get(j+1) > lineBefore.lastIndexOf('|') ? lineBefore.lastIndexOf('|'): indices.get(j+1);
 					lineBefore = lineBefore.substring(indices.get(j),lastIndex+1);
 					int count = 0;
 					for(String s: lineBefore.split("\\s+")) {
 						if (s.equals("|")) {
 							count++;
 						}

 					}
 					countArray.add(count);
 				}

 				break;
 			}
 		}
 		return countArray;
 	}
	
	public List<ArrayList<String>> splitMeasure2(){
		List<ArrayList<String>> split = new ArrayList<ArrayList<String>>();
		HashMap<Integer, String> measure = new HashMap<Integer, String>();
		String line = "";
		int numberOfLines = Integer.valueOf(line);
		for (int i = 0; i < 6; i++) {
			if (tabArray.contains("----")) {
				line = tabArray.get(i);
				//numberOfLines = tabArray.get(i);
				String[] lineArray = line.split("\\|");
				for(int j=1; j<numberOfLines;j++) {
					if (measure.containsKey(j)) {
						measure.put(j,measure.get(j)+ lineArray[j]+"\n");
					}
					else {
						measure.put(j,lineArray[j]+"\n");
					}
				}
			}
		}
		for(int i=1; i <= measure.size();i++) {
			String string = measure.get(i);
			ArrayList<String> splitMeasure= new ArrayList<String>();
			for(String s : string.split("\n")) {
				splitMeasure.add(s);
			}
			split.add(splitMeasure);
		}
		
		return split;
	}



}




