package experiment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.util.Iterator;
import java.util.List;

import learn.*;
import lambda.*;
import parser.*;

public class TestUI {
	
	private SingleTest test;
	
	private JFrame frame;
	private JTextField txt_input;
	private JTextArea txt_entries;
	private JTextField txt_res;
	private JScrollPane scroll;
	private JTree tree;
	private JButton btn_parse;
	private JPanel northPanel;
	private JPanel centerPanel;
	private JPanel southPanel;
	
	private void addNodes(DefaultMutableTreeNode dad, Cell cell) {
		List children = cell.getMaxChildren();
		if (children == null) {
			//System.out.println("Null children");
			return;
		}
		//Iterator i = children.iterator();
		//while (i.hasNext()) {
			List l = (List)children.get(0);
			if (l.size() == 1){
				Cell c = (Cell)l.get(0);
				DefaultMutableTreeNode son = new DefaultMutableTreeNode(c.toString());
				dad.add(son);
				addNodes(son, (Cell)c);
			}
			else if (l.size() == 2) {
				Cell c0 = (Cell)l.get(0);
				Cell c1 = (Cell)l.get(1);
				DefaultMutableTreeNode son0 = new DefaultMutableTreeNode(c0.toString());
				dad.add(son0);
				addNodes(son0, (Cell)c0);
				DefaultMutableTreeNode son1 = new DefaultMutableTreeNode(c1.toString());
				dad.add(son1);
				addNodes(son1, (Cell)c1);
			}
		//}
		return;
	}
	
	private void parse() {
		String words = txt_input.getText();
		Exp res = test.getSem(words);
		//System.out.println(res);
		if (res == null) {
			txt_entries.setText("Null");
			txt_res.setText("No result");
			return;
		}
		txt_res.setText(res.toString());
		List entries = test.ParseEntries(res);
		//System.out.println(entries);
		
		for (Object e : entries) {
			txt_entries.append(e + "\n");
		}
		
		Cell cell = test.getCell();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(cell.toString());
		
		addNodes(root, cell);
		
		tree = new JTree(root);
		scroll = new JScrollPane(tree);
		centerPanel.add(scroll, "Center");
		frame.setVisible(true);
		return;
	}
	
	public TestUI(){
		test = new SingleTest();
		
		frame = new JFrame("Test");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(new TitledBorder("请输入语句"));
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new TitledBorder("过程"));
		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("结果"));
		
		txt_input = new JTextField();
		txt_entries = new JTextArea();
		txt_res = new JTextField();
		txt_res.setEditable(false);
		txt_entries.setEditable(false);
		btn_parse = new JButton("Parse");
	
		northPanel.add(txt_input, "Center");
		northPanel.add(btn_parse, "East");
		southPanel.add(txt_res, "Center");
		frame.add(northPanel, "North");
		frame.add(centerPanel, "Center");
		frame.add(southPanel, "South");
		frame.setSize(960, 480);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2,
				(screen_height - frame.getHeight()) / 2);
		frame.setVisible(true);
		
		/**
		 * press button to parse sentence
		 */
		btn_parse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parse();
			}
		});
	}
	

	public static void main(String[] args) {		
		new TestUI();
	}

}
