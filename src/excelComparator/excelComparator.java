package excelComparator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * This is a small tools that compares two CSV files and creates two external
 * files where one contains the matching (clean) records, and the other contains the
 * mismatch (dirty) records.
 * */
public class excelComparator {
	
	/*
	 * This takes in 3 command-line arguments, namely [file1, file2, size of record]
	 * The file types MUST be CSV or Excel
	 * */
	public static void main(String[] args){
		String file1 = args[0];
		String file2 = args[1];
		int size = Integer.parseInt(args[2]);
		
		System.out.println("Will compare '"+file1+"' and '"+file2+"' of data of length "+size+".");
		new excelComparator(file1,file2,size);
	}
	
	// This compares the two files and creates 4 external CSV files
	// clean_data_forwards.csv
	// clean_data_backwards.csv
	// dirty_data_forwards.csv
	// dirty_data_backwards.csv
	public excelComparator(String f1, String f2, int size) {
		BufferedReader br1 = null, br2 = null;
		String line1 = "", line2 = "";
		String[][] csv1 = buildCSVData(br1, f1, line1, size);
		String[][] csv2 = buildCSVData(br2, f2, line2, size);
		compareExcelFiles(csv1, csv2, size, "_forwards");
		compareExcelFiles(csv2, csv1, size, "_backwards");
	}
	
	// Compare the CSV/Excel files
	private void compareExcelFiles(String[][] a, String[][] b, int s, String name){
		try {
			int mismatches = 0;
			FileWriter clean = new FileWriter("clean_data"+name+".csv");
			FileWriter dirty = new FileWriter("dirty_data"+name+".csv");
			clean.append(mergeLine(a[0]));
			dirty.append(mergeLine(a[0]));
			for(int i = 1; i < a.length; i++){
				boolean match = true;
				for(int j = 1; j < b.length; j++){
					for(int k = 0; k < s; k++){
						if((a[i][k] == null && b[j][k] == null) || (a[i][k] != null && b[j][k] != null && a[i][k].equals(b[j][k]))){
							if(match && k == s-1){
								System.out.println("Match found at A("+i+","+k+") and B("+j+","+k+")");
							}
						}else{
							match = false;
						}
					}
					if(match == true){
						clean.append(mergeLine(a[i]));
						i++;
					}
				}
				if(match != true){
					mismatches++;
					dirty.append(mergeLine(a[i]));
				}
			}
			clean.flush(); dirty.flush();
			clean.close(); dirty.close();
			System.out.println("There were "+mismatches+" mismatches in these two files...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Prepare records for new CSV file
	private String mergeLine(String[] strings) {
		String data = "";
		for(int i = 0; i < strings.length; i++){
			if(i == 0 && i != strings.length-1){
				data = strings[i]+",";
			}else if(i != 0 && i == strings.length-1){
				data = data + strings[i]+"\n";
			}else{
				data = data + strings[i]+ ",";
			}
		}
		return data;
	}

	// Change CSV/Excel file into 2D Array
	private String[][] buildCSVData(BufferedReader br, String f, String line, int size){
		String[] data;
		ArrayList<String[]> values = new ArrayList<String[]>();
		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				data = setSize(size, line.split(","));
				values.add(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[][] csv = makeCSV(values, size);
		return csv;
	}
	
	// Change ArrayList into 2D Array
	private String[][] makeCSV(ArrayList<String[]> values, int size) {
		int y = values.size();
		int x = size;
		String[][] c = new String[y][x];
		for(int i = 0; i < y; i++){
			for(int j = 0; j < x; j++){
				c[i][j] = values.get(i)[j];
			}
		}
		return c;
	}

	// Set minimum size for records in new files to prevent trimming
	private String[] setSize(int size, String[] data) {
		String[] d = new String[size];
		for(int i = 0; i < size; i++){
			if(i <= data.length-1){
				d[i] = data[i];
			}
		}
		return d;
	}
}
