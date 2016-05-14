package experiment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.util.Iterator;
import java.util.List;

import learn.*;
import lambda.*;
import parser.*;
import KB.*;

public class DemoUI {
	
	private SingleTest test;
	private query qr;
	
	private JFrame frame;
	private JTextField txt_input;
	private JTextArea txt_entries;
	private JTextField txt_res;
	private JTextArea txt_KB;
	private JScrollPane scroll;
	private JScrollPane KBscroll;
	private JTree tree;
	private DefaultTreeCellRenderer renderer;
	
	private mxGraph graph;
	private Object parent;
	private mxGraphComponent graphComponent;	
	
	private JButton btn_parse;
	private JPanel inputPanel;
	private JPanel midPanel;
	private JPanel lfPanel;
	private JPanel KBPanel;
	private JPanel treePanel;
	
	private int[] xx;
	
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
	
	
	private String nodeSpan(DefaultMutableTreeNode node) {
		String res = node.toString();
		int index = res.indexOf(":");
		res = res.substring(0, index);
		return res;
	}
	
	private Object paintNode(DefaultMutableTreeNode dad,int y) {
		String dadName = nodeSpan(dad);
		//System.out.println(dadName + dad.isLeaf());
		int mx = xx[y];
		int my = (y+1)*100;
		Object daddy = graph.insertVertex(parent, null, dadName, mx, my, 180,
				30);
		if (dad.isLeaf()) return daddy;
		for (int i=0; i<dad.getChildCount(); i++) {
			DefaultMutableTreeNode son = (DefaultMutableTreeNode) dad.getChildAt(i);
			Object myson = paintNode(son, y+1);
			xx[y+1] += 200;
			graph.insertEdge(parent, null, null, myson, daddy);
		}
		return daddy;
	}

	private void paintTree(DefaultMutableTreeNode root) {
		graph = new mxGraph();
		parent = graph.getDefaultParent();
		
		graph.getModel().beginUpdate();
		try
		{
			paintNode(root, 0);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		graph.setCellsEditable(false);

		graphComponent = new mxGraphComponent(graph);
		
		scroll = new JScrollPane(tree);
		tree.setCellRenderer(renderer); 		
		midPanel.removeAll();
		midPanel.add(txt_entries, "South");
		midPanel.add(scroll, "Center");
		//treescroll = new JScrollPane(graphComponent);
		//treePanel.add(treescroll);
		treePanel.removeAll();
		treePanel.add(graphComponent);
		frame.setVisible(true);
		return;
	}
	
	private void parse() {
		for (int i=0; i<10; i++) {
			xx[i] = 20;
		}
		
		String words = txt_input.getText();
		Exp res = test.getSem(words);

		//System.out.println(res);
		if (res == null) {
			txt_entries.setText("Null");
			txt_res.setText("No result");
			return;
		}
		String spq = qr.convert(res);
		String KBres = qr.execute(spq);
		//System.out.println(KBres);
		txt_KB.setText(KBres);
		txt_entries.setText("使用词条：\n");
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
		
		paintTree(root);

		return;
	}
	
	public DemoUI(){
		test = new SingleTest();
		qr = new query();
		
		xx = new int[10];
		for (int i=0; i<10; i++) {
			xx[i] = 20;
		}
		
		frame = new JFrame("Test");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBorder(new TitledBorder("请输入语句"));
		midPanel = new JPanel(new BorderLayout());
		midPanel.setBorder(new TitledBorder("过程"));
		KBPanel = new JPanel(new BorderLayout());
		KBPanel.setBorder(new TitledBorder("结果"));
		treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(new TitledBorder("解析树"));
		lfPanel = new JPanel(new BorderLayout());
		lfPanel.setBorder(new TitledBorder("逻辑表达式"));
		
		renderer=new DefaultTreeCellRenderer(); 
		renderer.setLeafIcon(new ImageIcon("")); 
		renderer.setClosedIcon(new ImageIcon("")); 
		renderer.setOpenIcon(new ImageIcon("")); 
		
		tree = new JTree();
		txt_input = new JTextField();
		txt_entries = new JTextArea();
		txt_res = new JTextField();
		txt_KB = new JTextArea();
		KBscroll = new JScrollPane(txt_KB);

		txt_res.setEditable(false);
		txt_entries.setEditable(false);
		txt_KB.setEditable(false);
		btn_parse = new JButton("Parse");
	
		inputPanel.add(txt_input, "Center");
		inputPanel.add(btn_parse, "East");
		midPanel.add(txt_entries, "South");
		lfPanel.add(txt_res);
		KBPanel.add(KBscroll);
		
		frame.setLayout(null);
		frame.add(midPanel);
		frame.add(treePanel);
		frame.add(KBPanel);
		frame.add(inputPanel);
		frame.add(lfPanel);
		inputPanel.setBounds(0, 0, 720, 50);
		midPanel.setBounds(0, 50, 720, 300);
		lfPanel.setBounds(0, 350, 720, 50);
		KBPanel.setBounds(0, 400, 720, 300);
		treePanel.setBounds(720, 0, 560, 700);

		frame.setSize(1280, 720);
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
		new DemoUI();
	}

}
