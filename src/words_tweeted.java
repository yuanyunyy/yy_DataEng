import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class words_tweeted {

	private FileInputStream input;
	private int count;
	private Queue[] pqHub;
	private HashMap[] hmHub;
	private HashMap<String, word> hmResult;
	private Queue<String> pqResult;
	private int[] lineNum;

	/*
	 * Separate the large input file into small files then sort them in
	 * alphabetical order, so that it is scalable for memory and time by setting
	 * up different parameters. The larger paramDocSize is, the less small txts
	 * generate. The trade off here is, if the paramDOcSize is large, sorting
	 * for each small txt will take longer, but the final result will take
	 * faster. The larger paramWriteSpeed is, the fewer times for writing to txt
	 * will be, but it will consume more memory. The larger paramUpdateSpeed is,
	 * the fewer times for reading from txt, which will take less time but
	 * consume more memory space.
	 */
	private int paramDocSize;
	private int paramWriteSpeed;
	private int paramUpdateSpeed;

	public words_tweeted(String filename, int ds, int ws, int us)
			throws FileNotFoundException {
		input = new FileInputStream(filename);
		count = 0;
		paramDocSize = ds;
		paramWriteSpeed = ws;
		paramUpdateSpeed = us;
	}

	public void generateResult() {
		this.BuildSmallFiles();
		this.SortFiles();
	}

	// Separate the large input txt into ordered small txts, so that the memory
	// space is sufficient.
	public void BuildSmallFiles() {
		try {
			int a = input.read(); // a = -1, input stop
			while (a != -1) {
				count++;
				File writename = new File("output/output_" + count + ".txt");
				writename.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(
						writename));
				// Use a priority queue to sort, and a HashMap to reduce
				// duplicate.
				Queue<String> pqDoc = new PriorityQueue<String>();
				HashMap<String, word> full = new HashMap<String, word>();
				for (int i = 0; i < paramDocSize && a != -1; i++) {
					StringBuilder obj = new StringBuilder();
					obj.append((char) a);
					a = input.read();
					while (a != '\n' && a != ' ' && a != -1) {
						obj.append((char) a);
						a = input.read();
					}
					String Value = obj.toString();
					if (full.containsKey(Value)) {
						full.get(Value).addFreq(1);
					} else {
						word tmp = new word(Value, 1);
						full.put(Value, tmp);
						pqDoc.add(Value);
					}
					a = input.read();
				}
				// write to a txt.
				while (!pqDoc.isEmpty()) {
					String tmpWord = pqDoc.poll();
					Integer tmpFreq = full.get(tmpWord).getFreq();
					Integer tmpDocID = full.get(tmpWord).getDocID();
					out.write(tmpWord + "\t" + tmpFreq + "\t" + "\n");
				}
				out.flush();
				out.close();
			}
			// Initialization
			pqHub = new PriorityQueue[count];
			hmHub = new HashMap[count];
			pqResult = new PriorityQueue<String>();
			hmResult = new HashMap<String, word>();
			lineNum = new int[count];
			for (int i = 0; i < count; i++) {
				lineNum[i] = 11;
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	// Read the small docs and sort; generate final result to a txt.
	public void SortFiles() {
		try {
			// Initialization
			File writename = new File("output/ft1.txt");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			for (int i = 0; i < count; i++) {
				pqHub[i] = new PriorityQueue<String>();
				hmHub[i] = new HashMap<String, word>();
				this.UpdatePQ(i + 1);// update_pq
				this.UpdateResult(i + 1);
			}
			int speed = 0;
			// Sort and write to txt
			while (!pqResult.isEmpty()) {
				String topKey = (String) pqResult.poll();
				word topWord = (word) hmResult.get(topKey);
				hmResult.remove(topKey);
				this.UpdateResult(topWord.getDocID());
				out.write(topKey + "\t" + topWord.getFreq() + "\n");
				out.flush();
				speed++;
				if (speed == paramWriteSpeed) {
					out.flush();
					speed = 0;
				}
			}
			out.flush();
			out.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	// When sorting the small txts, keep a global priority queue updated and
	// write it into output.txt.
	public void UpdateResult(Integer DocID) {
		if (pqHub[DocID - 1].isEmpty()) {
			if (lineNum[DocID - 1] > 0) {
				UpdatePQ(DocID);
			} else {
				return;
			}
		}
		String newKey = (String) pqHub[DocID - 1].poll();
		word newWord = (word) hmHub[DocID - 1].get(newKey);
		hmHub[DocID - 1].remove(newKey);
		if (hmResult.containsKey(newKey)) {
			word old = (word) hmResult.get(newKey);
			old.setFreq(old.getFreq() + newWord.getFreq());
			hmResult.put(newKey, old);
			this.UpdateResult(DocID);
		} else {
			pqResult.add(newKey);
			hmResult.put(newKey, newWord);
		}
		return;
	}

	// Keep an array for each small txt's top N words for global PQ's sorting.
	public void UpdatePQ(Integer DocID) {
		try {
			String filename = "output/output_" + DocID + ".txt";
			FileInputStream inputs = new FileInputStream(filename);
			int a = inputs.read();
			int CurrentLine = 1;
			while (a != -1) {
				if ((char) a == '\n') {
					CurrentLine++;
					a = inputs.read();
					if (a == -1) {
						lineNum[DocID - 1] = -1;
						break;
					}
				} else if (CurrentLine < lineNum[DocID - 1] - paramUpdateSpeed
						&& a != -1) {
					a = inputs.read();
				} else if (CurrentLine >= lineNum[DocID - 1] - paramUpdateSpeed
						&& CurrentLine < lineNum[DocID - 1]) {
					word tmp;
					StringBuilder objWord = new StringBuilder();
					StringBuilder objFreq = new StringBuilder();
					while (a != '\t') {
						objWord.append((char) a);
						a = inputs.read();
					}
					String Word = objWord.toString();
					a = inputs.read();
					pqHub[DocID - 1].add(Word);
					while (a != '\t') {
						objFreq.append((char) a);
						a = inputs.read();
					}
					int freq = Integer.parseInt(objFreq.toString());
					tmp = new word(Word, DocID);
					tmp.setFreq(freq);
					hmHub[DocID - 1].put(Word, tmp);
					a = inputs.read();
				} else {
					if (lineNum[DocID - 1] > 0) {
						lineNum[DocID - 1] = lineNum[DocID - 1] + 10;
					}
					break;
				}
			}
			inputs.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
