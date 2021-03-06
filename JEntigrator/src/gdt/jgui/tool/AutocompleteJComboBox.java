package gdt.jgui.tool;
/*
 * Copyright 2016 Alexander Imas
 * This file is part of JEntigrator.

    JEntigrator is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JEntigrator is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JEntigrator.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
/**
 * This is an auto complete combo box component for the search console.
 * @author imasa
 *
 */
public class AutocompleteJComboBox extends JComboBox<String> {
static final long serialVersionUID = 1L;
String[] terms;
//Component c;
	public AutocompleteJComboBox(String[] terms){
		super();
		this.terms=terms;
	setEditable(true);
	if(terms!=null){
		DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>(terms);
		setModel(model);
	}
		
	Component c = getEditor().getEditorComponent();
	
	if ( c instanceof JTextComponent ){
			final JTextComponent tc = (JTextComponent)c;
			tc.getDocument().addDocumentListener(new DocumentListener(){
				public void update(){
					SwingUtilities.invokeLater(new Runnable(){
					@Override
						public void run() {
							List<String> founds = new ArrayList<String>(search(tc.getText()));
							Set<String> foundSet = new HashSet<String>();
							for ( String s : founds ){
								foundSet.add(s.toLowerCase());
							}
						Collections.sort(founds);
						setEditable(false);
						removeAllItems();
						if ( !foundSet.contains( tc.getText().toLowerCase()) ){
							addItem( tc.getText() );
						}
						for (String s : founds) {
								addItem(s);
							}
							setEditable(true);
							setPopupVisible(true);
							tc.requestFocus();
						}
					});
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
				    update();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					update();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					update();
				}
				
			});
			tc.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				if ( tc.getText().length() > 0 ){
						setPopupVisible(true);
					}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
				}
			});
			
		}else{
			throw new IllegalStateException("Editing component is not a JTextComponent!");
		}
	}
	private Collection<String> search(String value) {
	//	System.out.println("AutocompleteJComboBox:search value="+value+" terms="+terms.length);
		List<String> founds = new ArrayList<String>();
		if(terms==null)
			return null;
		String[] sa;
		for ( String s : terms ){
			sa=s.split(" ");
			for(String aSa:sa)
			   if ( aSa.trim().toLowerCase().startsWith(value.toLowerCase()))
			{
				founds.add(s);
			}
		}
		return founds;
	}
}


