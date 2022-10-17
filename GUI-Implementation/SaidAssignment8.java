import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class SaidAssignment8{
	public static final int NUM_BUTTONS = 9;
	private static int width = 700;
	private static int height = 350;
	private JFrame frame;
	private String[] buttonLabelsLeft = {"sort ints", "add to bst", "add to treeset", "add to priority queue", "add to hashset", "add to arraylist", "add to sorted arraylist", "add to array", "merge sort ints"};
	private String[] buttonLabelsRight = {"search sorted ints", "search bst", "search treeset", "search priority queue", "search hashset", "search arraylist", "search sorted arraylist", "search array", "search merge sorted ints"};
    private String[] labelLabelsLeft = {"no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result"};
	private String[] labelLabelsRight = {"no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result", "no Result"};
	private JButton[] buttonsLeft = new JButton[buttonLabelsLeft.length];
    private JLabel[] labelsLeft = new JLabel[labelLabelsLeft.length];
	private JButton[] buttonsRight = new JButton[buttonLabelsLeft.length];
    private JLabel[] labelsRight = new JLabel[labelLabelsLeft.length];

	private LeftMethods[] leftButtonMethods = new LeftMethods[buttonsLeft.length];
	private RightMethods[] rightButtonMethods = new RightMethods[buttonsRight.length];

	private int[] sortDataFile;
	private int[] searchDataFile;
	private int[] selectionSortData;
	private BinarySearchTree binarySearchTreeSortData;
	private TreeSet<Integer> treeSetSortData;
	private PriorityQueue<Integer> prioirityQueueSortData;
	private HashSet<Integer> hashSetSortData;
	private ArrayList<Integer> arrayListSortData;
	private ArrayList<Integer> arrayListCollectionSortData;
	private int[] sortDataFileCopy;
	private int[] mergeSortData;
	private FilesMgr filesMgr = new FilesMgr(frame);

	public enum State {
		NOTHING_LOADED {
		}, SORT_FILE_LOADED {
			public boolean leftButtonEnabled(int index) {
				return true;
			}

			public State loadSearchFile() {
				return BOTH_FILES_LOADED;
			}
		}, SEARCH_FILE_LOADED {
			public State loadSortFile(){
				return BOTH_FILES_LOADED;
			}
		}, BOTH_FILES_LOADED {
			public boolean leftButtonEnabled(int index){
				return true;
			}
			public boolean rightButtonEnabled(int index){
				return leftButtonClicked[index];
			}
			public State loadSearchFile() {
				return this;
			}
			public State loadSortFile(){
				return this;
			}
		};
		static private boolean[] leftButtonClicked = new boolean[SaidAssignment8.NUM_BUTTONS];

		static void setLeftButtonClicked(int index, boolean value) {
			leftButtonClicked[index] = value;
		}
		public State reset () { // we plan to add a Reset option to the File menu
			for(int i = 0; i < leftButtonClicked.length; i++)
				leftButtonClicked[i] = false;
			return NOTHING_LOADED;
		}
		// the following defaults are overridden is some enums
		public State loadSearchFile() {
			return SEARCH_FILE_LOADED;
		}
		public State loadSortFile() {
			return SORT_FILE_LOADED;
		}
		public boolean leftButtonEnabled(int index) {
			return false;
		}
		public boolean rightButtonEnabled(int index) {
			return false;
		}
	}
	private State state = State.NOTHING_LOADED;
		


	public static void main(String[] args) {
		// The Java Tutorial says we should: 
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SaidAssignment8().createAndShowGUI();
			}
		});
	}

	private void guiUpdate() {
		for(int i = 0; i < buttonLabelsLeft.length; i++) {
			buttonsLeft[i].setEnabled(state.leftButtonEnabled(i));
			buttonsRight[i].setEnabled(state.rightButtonEnabled(i));
		}
	}

	public void createAndAddButtons(JPanel panelInput, GridBagLayout gridBagInput, JButton[] buttons, JLabel[] labels, String[] buttonLabels, String[] labelLabels){
		for(int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton(buttonLabels[i]);
			buttons[i].addActionListener(new ButtonActionListener(buttons[i]));
			buttons[i].setEnabled(false);
			labels[i] = new JLabel(labelLabels[i]);
		}

		GridBagConstraints buttonPanelConstraints = new GridBagConstraints(); //added
        buttonPanelConstraints.weightx = 1;
		buttonPanelConstraints.weighty = 1;
		buttonPanelConstraints.gridx = 0;
        buttonPanelConstraints.fill = 0;
		buttonPanelConstraints.anchor = GridBagConstraints.LINE_START;

		for(int i = 0; i < buttons.length; i++) {
			buttonPanelConstraints.gridx = 0;
			buttonPanelConstraints.gridy = i;
			buttonPanelConstraints.gridwidth = 1;
			if(i == 0){ buttonPanelConstraints.insets = new Insets(10,10,10,10); } 
			else { buttonPanelConstraints.insets = new Insets(0,10,10,10); }
			
			gridBagInput.setConstraints(buttons[i],buttonPanelConstraints);
			panelInput.add(buttons[i]);

			buttonPanelConstraints.gridx = 1;
			buttonPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;

			gridBagInput.setConstraints(labels[i],buttonPanelConstraints);
			panelInput.add(labels[i]);
		}
	}


	void populateMenuBar(JMenuBar menuBar) {
		// create the two menus
		JMenu fileMenu = new JMenu("File");

		fileMenu.setMnemonic(KeyEvent.VK_F); // ALT-F option
		// create the menu items for the two menus
		JMenuItem fileReadSort = new JMenuItem("Read Sort File");
        JMenuItem fileReadSearch = new JMenuItem("Read Search File");
		JMenuItem fileReset = new JMenuItem("Reset");
        JMenuItem fileExit = new JMenuItem("Exit");
		

		fileExit.setMnemonic(KeyEvent.VK_E); // ALT-F, ALT-E option
        fileReadSort.setMnemonic(KeyEvent.VK_B); 
		fileReset.setMnemonic(KeyEvent.VK_R);
        fileReadSearch.setMnemonic(KeyEvent.VK_S); 

		
		
		fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK)); // CTRL-E shortcut
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		fileReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)); // CTRL-R shortcut
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		// add the two menus to the menu bar
		menuBar.add(fileMenu);
	

		// add the two menu items to the two menus
        fileMenu.add(fileReadSort);
        fileMenu.add(fileReadSearch);
		fileMenu.add(fileReset);
		fileMenu.add(fileExit);

		// add the action listeners to the menu items
        fileReadSort.addActionListener(new MenuItemActionListener(fileReadSort));
        fileReadSearch.addActionListener(new MenuItemActionListener(fileReadSearch));
		fileReset.addActionListener(new MenuItemActionListener(fileReset));
		fileExit.addActionListener(new MenuItemActionListener(fileExit));	
	}

	public void populateMethods(){
		leftButtonMethods[0] = new LeftMethods() {
			public void leftMethod(){
				selectionSortData = sortDataFile.clone();
				for(int i = 0; i < selectionSortData.length; i++){
					Integer smallest = Integer.MAX_VALUE;
            		int index = i;
					for(int j = i; j < selectionSortData.length; j++){
						if (selectionSortData[j] < smallest){
							smallest = selectionSortData[j];
							index = j;
						}
					}
					int temp = selectionSortData[i];
            		selectionSortData[i] = smallest;
            		selectionSortData[index] = temp;
				}
			}
		};
		rightButtonMethods[0] = new RightMethods() {
			public int rightMethod(){
				int counter = 0; 
				for(int i = 0; i < searchDataFile.length; i++){
					int bottom = 0;
					int top = selectionSortData.length-1;
					while(bottom <= top){
						int middle = (bottom+top)/2;
						if(searchDataFile[i] < selectionSortData[middle]){
							top = middle - 1;
						}else if(searchDataFile[i] > selectionSortData[middle]){
							bottom = middle + 1;
						} else {
							counter++;
							break;
						}
					}
				}
				return counter;
			}
		};
		leftButtonMethods[1] = new LeftMethods() {
			public void leftMethod(){
				binarySearchTreeSortData = new BinarySearchTree();
				for(int i = 0; i < sortDataFile.length; i++){
					binarySearchTreeSortData.insertNode(new Node(sortDataFile[i]));
				}
			}
		};
		rightButtonMethods[1] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(binarySearchTreeSortData.getNode(binarySearchTreeSortData.getRoot(), searchDataFile[i]) != null){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[2] = new LeftMethods() {
			public void leftMethod(){
				treeSetSortData = new TreeSet<>();
				for(int i = 0; i < sortDataFile.length; i++){
					treeSetSortData.add(sortDataFile[i]);
				}
			}
		};
		rightButtonMethods[2] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(treeSetSortData.contains(searchDataFile[i])){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[3] = new LeftMethods() {
			public void leftMethod(){
				prioirityQueueSortData = new PriorityQueue<>();
				for(int i = 0; i < sortDataFile.length; i++){
					prioirityQueueSortData.add(sortDataFile[i]);
				}
			}
		};
		rightButtonMethods[3] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(prioirityQueueSortData.contains(searchDataFile[i])){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[4] = new LeftMethods() {
			public void leftMethod(){
				hashSetSortData = new HashSet<>();
				for(int i = 0; i < sortDataFile.length; i++){
					hashSetSortData.add(sortDataFile[i]);
				}
			}
		};
		rightButtonMethods[4] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(hashSetSortData.contains(searchDataFile[i])){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[5] = new LeftMethods() {
			public void leftMethod(){
				arrayListSortData = new ArrayList<>();
				for(int i = 0; i < sortDataFile.length; i++){
					arrayListSortData.add(sortDataFile[i]);
				}
			}
		};
		rightButtonMethods[5] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(arrayListSortData.contains(searchDataFile[i])){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[6] = new LeftMethods() {
			public void leftMethod(){
				arrayListCollectionSortData = new ArrayList<>();
				for(int i = 0; i < sortDataFile.length; i++){
					arrayListCollectionSortData.add(sortDataFile[i]);
				}
				Collections.sort(arrayListCollectionSortData);
			}
		};
		rightButtonMethods[6] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					if(Collections.binarySearch(arrayListCollectionSortData, searchDataFile[i]) >= 0){
						counter++;
					}
				}
				return counter;
			}
		};
		leftButtonMethods[7] = new LeftMethods() {
			public void leftMethod(){
				sortDataFileCopy = new int[sortDataFile.length];
				for(int i = 0; i < sortDataFile.length; i++){
					sortDataFileCopy[i] = sortDataFile[i];
				}
			}
		};
		rightButtonMethods[7] = new RightMethods() {
			public int rightMethod(){
				int counter = 0;
				for(int i = 0; i < searchDataFile.length; i++){
					for(int j = 0; j < sortDataFileCopy.length; j++){
						if(searchDataFile[i] == sortDataFileCopy[j]){
							counter++;
							break;
						}
					}
				}
				return counter;
			}
		};
		leftButtonMethods[8] = new LeftMethods() {
			public void leftMethod(){
				mergeSortData = sortDataFile.clone();
				bottomUpMergeSort(mergeSortData);
			}
		};
		rightButtonMethods[8] = new RightMethods() {
			public int rightMethod(){
				int counter = 0; 
				for(int i = 0; i < searchDataFile.length; i++){
					int bottom = 0;
					int top = mergeSortData.length-1;
					while(bottom <= top){
						int middle = (bottom+top)/2;
						if(searchDataFile[i] < mergeSortData[middle]){
							top = middle - 1;
						}else if(searchDataFile[i] > mergeSortData[middle]){
							bottom = middle + 1;
						} else {
							counter++;
							break;
						}
					}
				}
				return counter;
			}
		};
	}

	public void bottomUpMergeSort(int[] unsortedArray){
		int[] workArray = unsortedArray.clone();
		for(int width = 1; width < unsortedArray.length; width = width*2){
			for(int i = 0; i < unsortedArray.length; i += (2*width)){
				bottomUpMerge(unsortedArray, i, Math.min(i+width, unsortedArray.length),Math.min(i+(2*width), unsortedArray.length), workArray);
			}
			//unsortedArray = workArray.clone();
		}
	}

	private void bottomUpMerge(int[] arrayA, int left, int right, int end, int[] arrayB){
		int i = left;
		int j = right;
		for(int k = left; k < end; k++){
			if((i < right) && ( (j >= end) || (arrayA[i] <= arrayA[j]) ) ) {
				arrayB[k] = arrayA[i];
				i = i+1;
			} else {
				arrayB[k] = arrayA[j];
				j = j+1;
			}
		}

		for (int m = left; m < end; m++ )
        	arrayA[m] = arrayB[m];
	}

	public void createAndShowGUI() {
		// create the window and specify the size and what to do when the window is closed
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));

		// specify how the program will exit when the frame is closed
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowClosingListener());
		
		// create the panel to hold the four buttons
		JPanel buttonPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(boxLayout);
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		// create a panel to hold the left buttons
		JPanel topButtonPanel = new JPanel();
		GridBagLayout topGridBagLayout = new GridBagLayout();
		topButtonPanel.setLayout(topGridBagLayout);
		topButtonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        topButtonPanel.setMinimumSize(new Dimension(330,350));


		
		//create a panel to hold the right buttons
		JPanel bottomButtonPanel = new JPanel();
		GridBagLayout bottomGridBagLayout = new GridBagLayout();
		bottomButtonPanel.setLayout(bottomGridBagLayout);
		bottomButtonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        bottomButtonPanel.setMinimumSize(new Dimension(330,350));
		
		//add the two panels to the main button panel
		buttonPanel.add(topButtonPanel);
		buttonPanel.add(bottomButtonPanel);
		





		// button stuff ------------------------------------------------------------------------------------------

		createAndAddButtons(topButtonPanel, topGridBagLayout, buttonsLeft, labelsLeft, buttonLabelsLeft, labelLabelsLeft);
		createAndAddButtons(bottomButtonPanel, bottomGridBagLayout, buttonsRight, labelsRight, buttonLabelsRight, labelLabelsRight);

								
		// create the menu bar and set it in the frame
		JMenuBar menuBar = new JMenuBar();
		populateMenuBar(menuBar);
		populateMethods();
		frame.setJMenuBar(menuBar);
        frame.setContentPane(buttonPanel);

		frame.validate();
		frame.setLocationRelativeTo(null);
		guiUpdate();
		frame.setVisible(true);
	}

	public void readData(File input, boolean sortData){
		ArrayList<Integer> values = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
			String inn;
			while((inn = reader.readLine()) != null) {
				if(inn.trim().length() > 0) { // skip empty lines
					values.add(Integer.valueOf(inn.trim())); // remove leading & training blanks before converting to int
				}
			}
			if(sortData){
				sortDataFile = new int[values.size()];
				for(int i = 0; i < sortDataFile.length; i++){
					sortDataFile[i] = values.get(i);
				}
			} else {
				searchDataFile = new int[values.size()];
				for(int i = 0; i < searchDataFile.length; i++){
					searchDataFile[i] = values.get(i);
				}
			}
			reader.close();
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	// action listener for the buttons
	class ButtonActionListener implements ActionListener {
		// the button associated with the action listener, so that we can
		// share this one class with multiple buttons
		private javax.swing.JButton btn;
		
		ButtonActionListener(JButton b)	{
			this.btn = b;
		}
		
		public void actionPerformed(ActionEvent e) {
			System.out.println("action performed on " + btn.getText() + " button");
			if(btn.getText().equalsIgnoreCase("sort ints")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[0].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[0].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(0, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to bst")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[1].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[1].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(1, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to treeset")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[2].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[2].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(2, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to priority queue")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[3].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[3].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(3, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to hashset")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[4].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[4].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(4, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to arraylist")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[5].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[5].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(5, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to sorted arraylist")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[6].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[6].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(6, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("add to array")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[7].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[7].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(7, true);
				guiUpdate();
			}else if (btn.getText().equalsIgnoreCase("merge sort ints")){
				long t0 = System.currentTimeMillis();
				leftButtonMethods[8].leftMethod();
				long t1 = System.currentTimeMillis();
				labelsLeft[8].setText(t1-t0 + " ms");
				State.setLeftButtonClicked(8, true);
				guiUpdate();
			} else if (btn.getText().equalsIgnoreCase("search sorted ints")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[0].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[0].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search bst")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[1].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[1].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search treeset")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[2].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[2].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search priority queue")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[3].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[3].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search hashset")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[4].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[4].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search arraylist")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[5].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[5].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search sorted arraylist")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[6].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[6].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search array")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[7].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[7].setText(temp + "/" + (t1-t0) + " ms");
			} else if (btn.getText().equalsIgnoreCase("search merge sorted ints")){
				long t0 = System.currentTimeMillis();
				int temp = rightButtonMethods[8].rightMethod();
				long t1 = System.currentTimeMillis();
				labelsRight[8].setText(temp + "/" + (t1-t0) + " ms");
			}

			
		}
	}
	
	// action listener for the menu items
	class MenuItemActionListener implements ActionListener {
		// the menu item associated with the action listener, so that we can
		// share this one class with multiple menu items
		private javax.swing.JMenuItem mi;
		
		MenuItemActionListener(JMenuItem m)	{
			this.mi = m;
		}
		
		public void actionPerformed(ActionEvent e) {
			System.out.println("action performed on " + mi.getText() + " menu item");
			
			// if exit is selected from the file menu, exit the program
			if(mi.getText().toLowerCase().equals("exit")) {
				exit();
			}
			
			// if color is selected from the edit menu, put a popup on the screen 
			// saying something 
			if(mi.getText().toLowerCase().equals("read sort file")) {
				File temp = filesMgr.identifyFile("sort");
				if(temp != null){
					readData(temp,true);
					state = state.loadSortFile();
				}else{
					System.out.println("Null file");
				}
				// JFileChooser temp = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				// int returnV = temp.showOpenDialog(null);

				// if (returnV == JFileChooser.APPROVE_OPTION) {
				// 	readData(temp.getSelectedFile(), true);
				// }
			}

            if(mi.getText().toLowerCase().equals("read search file")) {
				File temp = filesMgr.identifyFile("search");
				if(temp != null){
					readData(temp,false);
					state = state.loadSearchFile();
				}else{
					System.out.println("Null file");
				}
			}

			if(mi.getText().toLowerCase().equals("reset")){
				for(int i = 0; i < labelsLeft.length; i++){
					labelsLeft[i].setText("no Result");
					labelsRight[i].setText("no Result");
				}
				state = state.reset();
			}
			guiUpdate();
		}
	}
	
	// JDialog to confirm exit
	private void exit() {
		int decision = JOptionPane.showConfirmDialog(
				frame, "Do you really wish to exit?",
				"Confirmation", JOptionPane.YES_NO_OPTION);
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}		
	}

	private class WindowClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			exit();
		}
	}
}