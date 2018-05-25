package codegen.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
//import javax.swing.text.StyleConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import editor.GraphEditor;

/**
 * The root pane used by the demos. This allows both the applet and the
 * stand-alone application to share the same UI.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DemoRootPane extends JRootPane implements HyperlinkListener, SyntaxConstants {

	private RTextScrollPane scrollPane;
	public RSyntaxTextArea textArea;
	public static GraphEditor ge;

	public static String funcName ="";

	public String getFuncName() {
		return funcName;
	}

	public static void setFuncName(String funcName) {
		DemoRootPane.funcName = funcName;
	}

	public DemoRootPane(String filePath, GraphEditor ged) {
		DemoRootPane.ge = ged;
		textArea = createTextArea();
		setText(filePath);
		textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);

		scrollPane = new RTextScrollPane(textArea, true);
		getContentPane().add(scrollPane);
		ErrorStrip errorStrip = new ErrorStrip(textArea);
		getContentPane().add(errorStrip, BorderLayout.LINE_END);
		// setJMenuBar(createMenuBar());
	}

	private void addSyntaxItem(String name, String res, String style, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeSyntaxStyleAction(name, res, style));
		bg.add(item);
		menu.add(item);
	}

	private void addThemeItem(String name, String themeXml, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ThemeAction(name, themeXml));
		bg.add(item);
		menu.add(item);
	}

	/**
	 * Creates the text area for this application.
	 *
	 * @return The text area.
	 */
	
	private RSyntaxTextArea createTextArea() {
		final RSyntaxTextArea textArea = new RSyntaxTextArea(25, 70);
		textArea.setTabSize(3);
		textArea.setCaretPosition(0);
		textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setClearWhitespaceLinesEnabled(false);		
		textArea.addMouseListener(new MyActionListener(textArea));
		
		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "decreaseFontSize");
		am.put("decreaseFontSize", new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction());
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "increaseFontSize");
		am.put("increaseFontSize", new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction());
		return textArea;
	}
	
	class MyActionListener implements MouseListener{
		
		RSyntaxTextArea textArea;

		public MyActionListener(RSyntaxTextArea textArea) {
			// TODO Auto-generated constructor stub
			this.textArea = textArea;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int pos = textArea.getCaretPosition()-1;				
			String label = "static";

			String content = textArea.getText();
			
			char[] chars = content.toCharArray();
			String temp = "";
			int braketNum = 0;
			String taskName = "";
			while(true){
				if(chars[pos] == '\t'){
					pos--;
					continue;
				}
				if(chars[pos] == '(')
					braketNum = pos;
				
				if(chars[pos] == 's'){
					temp = content.substring(pos,pos+label.length());
					if(temp.equals(label)){
						taskName = content.substring(pos, braketNum).replace("double", "").replace("int", "")
								.replace("bool","").replace(label+" ", "").replace(" ", "").toLowerCase();
						break;
					}
				}
				pos--;
				if(pos <= 0)
					break;
			}
			funcName = taskName;
			ge.alarmText(funcName);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}


	/**
	 * Focuses the text area.
	 */
	void focusTextArea() {
		textArea.requestFocusInWindow();
	}

	/**
	 * Called when a hyperlink is clicked in the text area.
	 *
	 * @param e
	 *            The event.
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			if (url == null) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			} else {
				JOptionPane.showMessageDialog(this, "URL clicked:\n" + url.toString());
			}
		}
	}

	/**
	 * Sets the content in the text area to that in the specified resource.
	 *
	 * @param resource
	 *            The resource to load.
	 */
	private void setText(String resource) {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(resource));
			textArea.read(r, null);
			r.close();
			textArea.setCaretPosition(0);
			textArea.discardAllEdits();
		} catch (RuntimeException re) {
			throw re; // FindBugs
		} catch (Exception e) { // Never happens
			textArea.setText("Type here to see syntax highlighting");
		}
	}

	private class AboutAction extends AbstractAction {

		public AboutAction() {
			putValue(NAME, "About RSyntaxTextArea...");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DemoRootPane.this,
					"<html><b>RSyntaxTextArea</b> - A Swing syntax highlighting text component" + "<br>Version 2.5.8"
							+ "<br>Licensed under a modified BSD license",
					"About RSyntaxTextArea", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private class AnimateBracketMatchingAction extends AbstractAction {

		public AnimateBracketMatchingAction() {
			putValue(NAME, "Animate Bracket Matching");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setAnimateBracketMatching(!textArea.getAnimateBracketMatching());
		}

	}

	private class ChangeSyntaxStyleAction extends AbstractAction {

		private String res;
		private String style;

		public ChangeSyntaxStyleAction(String name, String res, String style) {
			putValue(NAME, name);
			this.res = res;
			this.style = style;
		}

		public void actionPerformed(ActionEvent e) {
			setText(res);
			textArea.setCaretPosition(0);
			textArea.setSyntaxEditingStyle(style);
		}

	}

	private class CodeFoldingAction extends AbstractAction {

		public CodeFoldingAction() {
			putValue(NAME, "Code Folding");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setCodeFoldingEnabled(!textArea.isCodeFoldingEnabled());
		}

	}

	private class MarkOccurrencesAction extends AbstractAction {

		public MarkOccurrencesAction() {
			putValue(NAME, "Mark Occurrences");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setMarkOccurrences(!textArea.getMarkOccurrences());
		}

	}

	private class TabLinesAction extends AbstractAction {

		private boolean selected;

		public TabLinesAction() {
			putValue(NAME, "Tab Lines");
		}

		public void actionPerformed(ActionEvent e) {
			selected = !selected;
			textArea.setPaintTabLines(selected);
		}

	}

	private class ThemeAction extends AbstractAction {

		private String xml;

		public ThemeAction(String name, String xml) {
			putValue(NAME, name);
			this.xml = xml;
		}

		public void actionPerformed(ActionEvent e) {
			InputStream in = getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/" + xml);
			try {
				Theme theme = Theme.load(in);
				theme.apply(textArea);
				// try {
				// textArea.setBackgroundImage(javax.imageio.ImageIO.read(new
				// java.io.File("D:/temp/homer.jpg")));
				// } catch (Exception ex) { ex.printStackTrace(); }
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

	private class ToggleAntiAliasingAction extends AbstractAction {

		public ToggleAntiAliasingAction() {
			putValue(NAME, "Anti-Aliasing");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setAntiAliasingEnabled(!textArea.getAntiAliasingEnabled());
		}

	}

	private class ViewLineHighlightAction extends AbstractAction {

		public ViewLineHighlightAction() {
			putValue(NAME, "Current Line Highlight");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setHighlightCurrentLine(!textArea.getHighlightCurrentLine());
		}

	}

	private class ViewLineNumbersAction extends AbstractAction {

		public ViewLineNumbersAction() {
			putValue(NAME, "Line Numbers");
		}

		public void actionPerformed(ActionEvent e) {
			scrollPane.setLineNumbersEnabled(!scrollPane.getLineNumbersEnabled());
		}

	}

	private class WordWrapAction extends AbstractAction {

		public WordWrapAction() {
			putValue(NAME, "Word Wrap");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setLineWrap(!textArea.getLineWrap());
		}

	}

}