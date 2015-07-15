import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class median_unique {

	/*
	 * The possible values of unique words for each tweet are finite and
	 * limited. Use an array to store such distribution. The maximum chara of
	 * each tweet is 140. So it is fine to set the size of any number bigger
	 * than 140. Here Set it as 1000 for safety and convenience.
	 */
	private int[] sum = new int[1000];
	private FileInputStream input;
	private int total = 0;
	private double median;

	public median_unique(String filename) throws FileNotFoundException {
		input = new FileInputStream(filename);

	}

	// Calculate and update the median number of unique words per tweet.
	public void Calculate() {
		try {
			int a = input.read();
			// read the txt. when a = -1, input stop
			File writename = new File("output/ft2.txt");
			// set the absolute path of output file. path is subject to change.
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			// Analyze each each and figure out the number of unique words for
			// each tweet.
			while (a != -1) {
				HashMap<String, Integer> result = new HashMap();
				while (a != '\n' && a != -1) {
					StringBuilder obj = new StringBuilder();
					while (a != '\n' && a != ' ' && a != -1) {
						obj.append((char) a);
						a = input.read();
					}
					String key = obj.toString();
					if (!result.containsKey(key)) {
						result.put(key, null);
					}
					if (a != '\n') {
						a = input.read();
					}
				}
				total++;
				this.InsertNew(result.size());
				// Update the median every time read a tweet.
				median = this.CalculateMedian(total);
				out.write(String.format("%.1f", median) + "\n");
				out.flush();
				a = input.read();
			}
			out.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	// Maintain an array to store the distribution of overall unique word
	// distribution.
	public void InsertNew(int loc) {
		for (int i = loc; i < sum.length; i++) {
			sum[i]++;
		}
	}

	// method of calculating median.
	public double CalculateMedian(int total) {
		int frontLoc = 0;
		int frontValue = 0;
		int backLoc = 0;
		int backValue = 0;
		if (total % 2 == 0) {
			frontLoc = total / 2;
			backLoc = frontLoc + 1;
		} else {
			frontLoc = total / 2 + 1;
			backLoc = frontLoc;
		}
		int i = 0;
		for (i = 1; i < sum.length; i++) {
			if (frontLoc > sum[i - 1] && frontLoc <= sum[i]) {
				frontValue = i;
				break;
			}
		}
		for (i = frontValue; i < sum.length; i++) {
			if (backLoc > sum[i - 1] && backLoc <= sum[i]) {
				backValue = i;
				break;
			}
		}

		return ((double) frontValue + (double) backValue) / 2;
	}

}
