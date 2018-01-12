import java.util.Arrays;
import java.util.HashMap;

public class MainClass {
	
	static int counter = 0;

	
	public static void main(String[] args) {
		// all possible preference lists
		int[][] preferences = {
				{0, 1, 2},
				{0, 2, 1},
				{1, 0, 2},
				{1, 2, 0},
				{2, 0, 1},
				{2, 1, 0}
		};
		
		int[][] M = new int[3][3];
		int[][] W = new int[3][3];
		
		for (int p1 = 0; p1 < preferences.length; p1++) {
			for (int p2 = 0; p2 < preferences.length; p2++) {
				for (int p3 = 0; p3 < preferences.length; p3++) {
					for (int p4 = 0; p4 < preferences.length; p4++) {
						for (int p5 = 0; p5 < preferences.length; p5++) {
							for (int p6 = 0; p6 < preferences.length; p6++) {
								M[0] = preferences[p1].clone();
								M[1] = preferences[p2].clone();
								M[2] = preferences[p3].clone();
								W[0] = preferences[p4].clone();
								W[1] = preferences[p5].clone();
								W[2] = preferences[p6].clone();
								try {
									GaleShapleyAlgorithm(M, W);
								} catch (Exception e) {
									System.out.println("Implementation Error");
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

	}
	
	// indexOf for primitive Java arrays
	public static int indexOf(int[] array, int v) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == v) {
				return i;
			}
		}
		return -1;
	}
	
	// next free man
	public static int nextMan(int prevMan, boolean[] isEngaged) {
		for (int i= (prevMan + 1) %3 ; i < isEngaged.length; i++) {
			boolean status = isEngaged[i];
			if (!status) {
				return i;
			}
		}
		return -1;
	}
	
	// for given preference list, return next woman
	public static int nextProposal(int[] manPreferences) {
		for (int i=0; i < manPreferences.length; i++) {
			int candidate = manPreferences[i];
			if (candidate != -1) {
				return candidate;
			}
		}
		return -1;
	}
	
	// for given woman preference list and man m, returns if w accepts m or not
	public static boolean proposalResult(int[] womanPreferences, int m, int w, HashMap<Integer, Integer> wmMapping) {
		if (wmMapping.get(w) == null) {
			// return true if w is not engaged yet
			return true;
		} else {
			int fiance = wmMapping.get(w);
			// check the fiance's preference order
			return !(indexOf(womanPreferences, fiance) < indexOf(womanPreferences, m));
		}
	}
	
	
	public static void GaleShapleyAlgorithm (int[][] M, int[][] W) throws Exception {
		HashMap<Integer, Integer> mwMapping = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> wmMapping = new HashMap<Integer, Integer>();
		// I will update preference list of m after m proposes
		int[][] cloneM = new int[3][3];
		int[][] cloneW = new int[3][3];
		for (int i=0; i < 3; i++) {
			cloneM[i] = M[i].clone();
			cloneW[i] = W[i].clone();
		}
		
		boolean[] isEngaged = {false, false, false};
		int proposalCount = 0;
		int prevMan = -1;
		
		while(true) {
			int m = nextMan(prevMan, isEngaged);
			//System.out.println("Next man:" + m);
			if (m == -1) {
				// if there is no free man, break the loop
				break;
			}
			//System.out.println(m + "'s preference list: " + Arrays.toString(M[m]));
			int w = nextProposal(M[m]);
			//System.out.println("Will propose to woman " + w);
			if (w == -1) {
				// If the algorithm reaches here, then IMPLEMENTATION is WRONG for SMP! 
				throw new Exception("No woman left to marry!");
			}
			boolean accepted = proposalResult(W[w], m, w, wmMapping);
			//System.out.println("Is he accepted?: " + accepted);
			proposalCount++;
			prevMan = m;
			if (accepted) {
				if (wmMapping.get(w) != null) {
					int unluckyMan = wmMapping.get(w);
					//System.out.println("But she is engaged with man:" + unluckyMan);
					// break off the engagement
					isEngaged[unluckyMan] = false;
					mwMapping.put(unluckyMan, -1);
					//System.out.println(unluckyMan + " and " + w + " break up");
					for (int i= 0; i < M[unluckyMan].length; i++) {
						if (M[unluckyMan][i] == w) {
							M[unluckyMan][i] = -1;
							break;
						}
					}
				}
				// engage w and m
				wmMapping.put(w, m);
				mwMapping.put(m, w);
				isEngaged[m] = true;
			}
			// in order to prevent m's proposing to w again, replace w with -1 in m's preference list
			for (int i= 0; i < M[m].length; i++) {
				if (M[m][i] == w) {
					M[m][i] = -1;
					break;
				}
			}
		}
		if (proposalCount > 6) {
			counter++;
			System.out.println("Solution " + counter);
			for (int i= 0; i < 3; i++) {
				System.out.println("\tM" + i + ": " + Arrays.toString(cloneM[i]) + "  W" + i + ": " + Arrays.toString(cloneW[i]));
			}
		}
	}

}
