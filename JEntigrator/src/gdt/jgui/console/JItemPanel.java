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
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import org.apache.commons.codec.binary.Base64;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;
import java.util.logging.Logger;
import java.awt.event.ActionListener;

import javax.swing.Timer;
/**
 * This class represents the general-purpose graphical item
 * in the list console. It contains a title, an icon and a 
 * locator , which will be executed by the click on the item.
 * @author imasa
 *
 */
public class JItemPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(JItemPanel.class.getName());
	
	protected String locator$;
	protected String icon$;
	protected String title$;
    
	protected JMainConsole console;
	protected JCheckBox checkbox=null;
	protected JLabel title ;
	protected Timer timer	;
	protected JPopupMenu popup;
	boolean debug=false;
	
/**
 * The constructor.
 * @param console the main console.
 * @param locator$ the item's locator.
 */
	public JItemPanel(JMainConsole console,String locator$){
		if(debug)
			System.out.println("JItemPanel:locator="+locator$);
		this.console=console;
		this.locator$=locator$;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		try{
			this.console=console;
			this.locator$=locator$;
	          Properties locator=Locator.toProperties(locator$);
	          if(Locator.LOCATOR_TRUE.equals(locator.getProperty(Locator.LOCATOR_CHECKABLE))){
	        	  checkbox = new JCheckBox();
	      		  add(checkbox);
	      		if(Locator.LOCATOR_TRUE.equals(locator.getProperty(Locator.LOCATOR_CHECKED)))
	      			if(checkbox!=null)
	      			checkbox.setSelected(true);
	          }
	          title$=locator.getProperty(Locator.LOCATOR_TITLE);
	          if(title$!=null){
	        	 title = new JLabel(title$, JLabel.LEFT);
	        	 title.setText(title$);
	        	 title.setOpaque(true);
	        	 title.addMouseListener(new MousePopupListener());
	      		title.setAlignmentX(Component.LEFT_ALIGNMENT);
	      		add(title,BorderLayout.WEST );
	      		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	      		Entigrator entigrator=console.getEntigrator(entihome$);
	      		icon$=JConsoleHandler.getIcon(entigrator,locator$);
	          if(icon$!=null){
	        	  byte[] ba=Base64.decodeBase64(icon$);
	        	  ImageIcon icon = new ImageIcon(ba);
	        	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	        	  icon.setImage(image);
	        	  title.setIcon(icon); 
	          }
	          }else
	        	  LOGGER.info("title is null");
	       
		}catch(Exception e){
			LOGGER.severe(e.toString());
			
		}
	}
	protected void createTimer(){
		timer	= new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				title.setBackground(JItemPanel.this.getBackground());
				String locator$=JItemPanel.this.locator$;
				JConsoleHandler.execute(JItemPanel.this.console,locator$ );
				timer.stop();
			}
          });
	}
	/**
	 * The default constructor.
	 */
	public JItemPanel(){
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		title = new JLabel(title$, JLabel.LEFT);
		 title.addMouseListener(new JItemPanel.MousePopupListener());
  		title.setAlignmentX(Component.LEFT_ALIGNMENT);
  		add(title,BorderLayout.WEST );
	}
	/**
	 * Create the item panel.
	 * @param console the main console.
	 * @param locator$ the item's locator.
	 * @return the item panel.
	 */
	public JItemPanel instantiate(JMainConsole console,String locator$){
		try{
			this.console=console;
			this.locator$=locator$;
			this.removeAll();
	          Properties locator=Locator.toProperties(locator$);
	          if(Locator.LOCATOR_TRUE.equals(locator.getProperty(Locator.LOCATOR_CHECKABLE))){
	        	  checkbox = new JCheckBox();
	      		  add(checkbox);
	      		if(Locator.LOCATOR_TRUE.equals(locator.getProperty(Locator.LOCATOR_CHECKED)))
	      			if(checkbox!=null)
	      			checkbox.setSelected(true);
	          }
	          title = new JLabel(title$, JLabel.LEFT);
	          title$=locator.getProperty(Locator.LOCATOR_TITLE);
	          if(title$!=null){
	        	 title.setText(title$); 
	        	 title.setOpaque(true);
	        	 title.addMouseListener(new MousePopupListener());
	      		title.setAlignmentX(Component.LEFT_ALIGNMENT);
	      		add(title,BorderLayout.WEST );
	      	  String	entihome$=locator.getProperty(Entigrator.ENTIHOME);	
	          Entigrator entigrator=console.getEntigrator(entihome$);
	          icon$=JConsoleHandler.getIcon(entigrator,locator$);
	      	  if(icon$!=null){
	        	  byte[] ba=Base64.decodeBase64(icon$);
	        	  ImageIcon icon = new ImageIcon(ba);
	        	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	        	  icon.setImage(image);
	        	  title.setIcon(icon); 
	          }
	          }else
		        	  LOGGER.info("title is null");
	       	}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return this;
	}
	public JItemPanel instantiate(Entigrator entigrator,String locator$){
		try{
			this.locator$=locator$;
			this.removeAll();
			title$=Locator.getProperty(locator$,Locator.LOCATOR_TITLE);
            icon$=JConsoleHandler.getIcon(entigrator,locator$);
	       
	}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return this;
	}
/**
 * Get item's locator. 
 * @return the locator.
 */
	public String getLocator(){
	if(locator$==null)
		return null;
	if(checkbox!=null)
		if(checkbox.isSelected())
		   locator$=Locator.append(locator$,Locator.LOCATOR_CHECKED , Locator.LOCATOR_TRUE);
		else
			locator$=Locator.append(locator$,Locator.LOCATOR_CHECKED , Locator.LOCATOR_FALSE);
	return locator$;
}
public void setLocator(String locator$){
	this.locator$=locator$;
}
	/**
	 * Get item's title.
	 * @return the item title.
	 */
public String getTitle(){
	title.setText(title$);
	return title$;
}
/**
 * Check if the item is checkable.
 * @return true if checkable false otherwise.
 */
public boolean isCheckable(){
	if(checkbox!=null)
		return true;
	else
		return false;
}
/**
 * Check if the item is checked.
 * @return true if checked false otherwise.
 */
public boolean isChecked(){
	if(checkbox==null)
		return false;
	return checkbox.isSelected();
}
/**
 * Set item to be checked or unchecked.
 * @param checked true if checked false otherwise.
 */
public void setChecked(boolean checked){
	if(checkbox!=null)
		checkbox.setSelected(checked);
}
public void resetIcon(){
	try{
	if(icon$!=null){
   	  byte[] ba=Base64.decodeBase64(icon$);
   	  ImageIcon icon = new ImageIcon(ba);
   	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
   	  icon.setImage(image);
   	  title.setIcon(icon); 
   	  title.repaint();
   	  title.revalidate();
     }
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
@Override
public boolean equals(Object v) {
   try{
	if(super.equals(v))
    	return true;
	if (v instanceof JItemPanel){
          if( locator$.equals(((JItemPanel)v).locator$))
        	  return true;
          
      }
	
   }catch(Exception e){
	   LOGGER.severe(e.toString());
   }
   return false;
}
public void setPopupMenu(JPopupMenu popup){
	this.popup=popup;
}
public class MousePopupListener extends MouseAdapter {
  boolean isPopup=false;
	public void mousePressed(MouseEvent e) {
	//	System.out.println("ItemPanel:MousePopupListener:mouse pressed");
		if (e.isPopupTrigger()) {
	  //  	  System.out.println("ItemPanel:MousePopupListener:isPopupTrigger");
	    	  if(popup!=null)
	    		  isPopup=true;
	    	  else
	    		  isPopup=false;
	      }else
	    	  isPopup=false;
		//System.out.println("ItemPanel:MousePopupListener:isPopup="+isPopup);
    }

    public void mouseClicked(MouseEvent e) {
    	//System.out.println("ItemPanel:MousePopupListener:mouse clicked");
         	if(!isPopup){
      	 // System.out.println("ItemPanel:MousePopupListener:locator="+ItemPanel.this.locator$);
  			title.setBackground(Color.LIGHT_GRAY);
  			if(timer==null)
  				createTimer();
  			timer.start();
        }else{
        	//System.out.println("ItemPanel:MousePopupListener:do popup");
        	if(popup!=null){
        	    popup.show(JItemPanel.this, e.getX(), e.getY());
        	}
        }
    }

    public void mouseReleased(MouseEvent e) {
    	//System.out.println("ItemPanel:MousePopupListener:mouse released");
    	if(!isPopup)
    	if (e.isPopupTrigger()) {
	    	//  System.out.println("ItemPanel:MousePopupListener:released:isPopup");
	    	  isPopup=true;
	      }
    }
   }

}
