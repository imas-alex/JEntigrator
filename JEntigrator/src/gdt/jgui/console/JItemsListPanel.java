package gdt.jgui.console;
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
/**
 * This abstract class is a super class for list consoles
 * which contains instances of the JItemPanel class. 
 * @author imasa
 *
 */
public  abstract class JItemsListPanel extends JPanel implements JContext{
	JPanel panel;
	/**
	 * The default constructor.
	 */
	public JItemsListPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		
		add(scrollPane);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scrollPane.getViewport().add(panel);
	}
	private static final long serialVersionUID = 1L;
    private Logger LOGGER=Logger.getLogger(JItemsListPanel.class.getName());
    protected JMainConsole console;
    JMenuItem selectItem;
    JMenuItem unselectItem;
    protected JMenu menu;
    protected String locator$;
   /**
    * Get the basic context menu for lists.
    */
    @Override
	public JMenu getContextMenu() {
	   menu=new JMenu("Context");
	   menu.setName("Context");
	   selectItem = new JMenuItem("Select all");
	   selectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		      JItemPanel[] ipa=getItems();
		      if(ipa!=null){
		    	  for(JItemPanel ip:ipa)
		    		  ip.setChecked(true);
		      }
			}
		} );
		menu.add(selectItem);
		 unselectItem = new JMenuItem("Unselect all");
		   unselectItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
			      JItemPanel[] ipa=getItems();
			      if(ipa!=null){
			    	  for(JItemPanel ip:ipa)
			    		  ip.setChecked(false);
			      }
				}
			} );
			menu.add(unselectItem);
		return menu;
		
	}
/**
 * Put items into list.    
 * @param ipa the item panel array.
 */
    public void putItems(JItemPanel[] ipa) {
    	panel.removeAll();
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		 if(ipa!=null)
			 for(JItemPanel aIpl:ipa){
				 aIpl.revalidate();
				 aIpl.repaint();
				// System.out.println("ItemsListPanel:putItems:aIpl="+aIpl.getLocator());
				 panel.add(aIpl);
			 }
		
		 
	}
    /**
     * Get items of the list.
     * @return the array of the item panels.
     */
   public JItemPanel[] getItems(){
	 
	  try{
		  int cnt=panel.getComponentCount();
		  JItemPanel[] ipa=new JItemPanel[cnt];
		  for(int i=0;i<cnt;i++)
			  ipa[i]=(JItemPanel)panel.getComponent(i);
		  return ipa;
	  }catch(Exception e){
		  LOGGER.severe(e.toString());
		  return null;
	  }
   }
  /**
   * List items  titles.
   * @return array of items titles.
   */
   public String[] listItems(){
	   JItemPanel[] ipa=getItems();
	   if(ipa==null)
		   return null;
	   String[] sa=new String[ipa.length];
	   for(int i=0;i<ipa.length;i++)
		   sa[i]=ipa[i].getLocator();
	   return sa;
   }
   protected String[] listSelectedItems(){
	   JItemPanel[] ipa=getItems();
	   if(ipa==null)
		   return null;
	   ArrayList<String>sl=new ArrayList<String>();
	   for(int i=0;i<ipa.length;i++)
		   if(ipa[i].isChecked())
		      sl.add(ipa[i].getLocator());
	   return sl.toArray(new String[0]);
   }
   protected boolean hasSelectedItems(){

	   JItemPanel[] ipa=getItems();
	   if(ipa==null)
		   return false;
	   for(int i=0;i<ipa.length;i++)
		   if(ipa[i].isChecked())
		      return true;
	   return false;
   }
   /**
    * Get the panel to put in the main console.
    * 
    */
@Override
public JPanel getPanel() {
	return this;
}
/**
 * Create the list panel.
 * @param console the main console.
 * @param locator$ the locator string.
 * @return the list panel.
 */
public  JContext instantiate(JMainConsole console,String locator$){
	this.console=console;
	this.locator$=locator$;
	return this;
}

public static class ItemPanelComparator implements Comparator<JItemPanel>{
    @Override
    public int compare(JItemPanel o1, JItemPanel o2) {
    	try{
    		return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    	}catch(Exception e){
    		return 0;
    	}
    }
}
}
