/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.utils.FlexoFileChooserUtils;

public class TestFileChoosing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final JFrame dialog = new JFrame();

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton openButton1 = new JButton("JFileChooser- open");
		openButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				// chooser.setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
				chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_a_prj_directory"));
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setFileFilter(FlexoFileChooserUtils.PROJECT_FILE_FILTER);
				chooser.setFileView(FlexoFileChooserUtils.PROJECT_FILE_VIEW);
				chooser.showOpenDialog(dialog);
			}
		});

		JButton openButton2 = new JButton("FileDialog- open");
		openButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(dialog);
				// fileDialog.setFilenameFilter(filter)
				// fileDialog.set
				try {
					// fileDialog.setDirectory(AdvancedPrefs.getLastVisitedDirectory().getCanonicalPath());
				} catch (Throwable t) {
				}
				fileDialog.setVisible(true);

			}
		});

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(openButton1);
		controlPanel.add(openButton2);

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(controlPanel, BorderLayout.CENTER);

		dialog.setPreferredSize(new Dimension(1000, 800));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();
		dialog.setVisible(true);
		dialog.getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.TRUE);
		dialog.setVisible(true);
		// Editor.main(null);
	}

	final static String WINDOW_MODIFIED = "windowModified";

	public static class Editor extends JFrame implements DocumentListener, ActionListener {

		final static String WINDOW_MODIFIED = "windowModified";

		JEditorPane jp;
		JMenuBar jmb;
		JMenu file;
		JMenuItem save;

		public Editor(String title) {
			super(title);
			jp = new JEditorPane();
			jp.getDocument().addDocumentListener(this);
			getContentPane().add(jp);
			jmb = new JMenuBar();
			file = new JMenu("File");
			save = new JMenuItem("Save");
			save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			save.addActionListener(this);
			file.add(save);
			jmb.add(file);
			setJMenuBar(jmb);
			setSize(400, 600);
			setVisible(true);
		}

		// doChange() and actionPerformed() handle the "windowModified" state
		public void doChange() {
			getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.TRUE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// save functionality here...
			getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.FALSE);
		}

		// DocumentListener implementations
		@Override
		public void changedUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			doChange();
		}

		public static void main(String[] args) {
			new Editor("test");
		}
	}
}
