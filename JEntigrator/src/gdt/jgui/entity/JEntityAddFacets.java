package gdt.jgui.entity;
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
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetAddItem;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.bookmark.JBookmarksFacetAddItem;
import gdt.jgui.entity.fields.JFieldsFacetAddItem;
import gdt.jgui.entity.folder.JFolderFacetAddItem;
import gdt.jgui.entity.webset.JWebsetFacetAddItem;
/**
 * Displays a list of available facets to add to the entity.
 * @author imasa
 *
 */
public class JEntityAddFacets extends JItemsListPanel {
private static final long serialVersionUID = 1L;
private Logger LOGGER=Logger.getLogger(JEntityAddFacets.class.getName());
String entihome$;
String entityKey$;
String entityLabel$;
String locator$;
JMenuItem addItem;
/**
 * The default constructor.
 */
public JEntityAddFacets() {
		super();
	}
/**
 * Get the context menu.
 * @return the context menu.
 */
	 @Override
		public JMenu getContextMenu() {
		   menu=super.getContextMenu();
		   menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
				//System.out.println("EntityEditor:getConextMenu:menu selected");
				if(addItem!=null) 
				menu.remove(addItem);
				if(hasSelectedItems()){
					   addItem = new JMenuItem("Add selected");
					   addItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
						      String[] sa=JEntityAddFacets.this.listSelectedItems();
						      if(sa!=null){
						    	  for(String aSa:sa)
						    		  System.out.println("EntityAddFacets:item="+aSa);
						    	  applyFacets();
						    	  JEntityFacetPanel erm=new JEntityFacetPanel();
						  		  String locator$=erm.getLocator();
						  		  locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
						  		  locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
       					  		  JConsoleHandler.execute(console, locator$);
						      }
							}
						} );
						menu.add(addItem);
				}
				}
				@Override
				public void menuDeselected(MenuEvent e) {
				}
	    		@Override
				public void menuCanceled(MenuEvent e) {
				}
			});
			return menu;
	 }
/**
 * Get the context locator.
 * @return the context locator.
 */
	 @Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    if(entityLabel$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	       locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,JEntityAddFacets.class.getName());
		   String icon$=Support.readHandlerIcon(getClass(), "facet.png");
		   locator.setProperty(Locator.LOCATOR_ICON,icon$);
		   return Locator.toString(locator);
	}
	/**
	 * Create the add facets panel.
	 * @param console the main console.
	 * @param locator$ the context locator.
	 */
	 @Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			this.console=console;
			this.locator$=locator$;
	//		System.out.println("JEntityAddFacets:instantiate:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			putItems(getAllFacetAddItems());
			//revalidate();
			//repaint();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return this;
	}
private JFacetAddItem[] getAllFacetAddItems(){
	try{
		ArrayList<JFacetAddItem>fail=new ArrayList<JFacetAddItem>();
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String itemLocator$;
		JFieldsFacetAddItem fieldsItem=new JFieldsFacetAddItem();
		itemLocator$=fieldsItem.getLocator();
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME ,entihome$);
		itemLocator$=Locator.append(itemLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
		itemLocator$=fieldsItem.markAppliedUncheckable(console, itemLocator$);
		fieldsItem.instantiate(console, itemLocator$);
		fail.add(fieldsItem);
		
		//folder

		JFolderFacetAddItem folderItem=new JFolderFacetAddItem();
		itemLocator$=folderItem.getLocator();
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME ,entihome$);
		itemLocator$=Locator.append(itemLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
		itemLocator$=folderItem.markAppliedUncheckable(console, itemLocator$);
		folderItem.instantiate(console, itemLocator$);
		fail.add(folderItem);
        
		//webset

		JWebsetFacetAddItem websetItem=new JWebsetFacetAddItem();
		itemLocator$=websetItem.getLocator();
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME ,entihome$);
		itemLocator$=Locator.append(itemLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
		itemLocator$=folderItem.markAppliedUncheckable(console, itemLocator$);
		websetItem.instantiate(console, itemLocator$);
		fail.add(websetItem);

		//bookmarks
		
		JBookmarksFacetAddItem bookmarksItem=new JBookmarksFacetAddItem();
		itemLocator$=bookmarksItem.getLocator();
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME ,entihome$);
		itemLocator$=Locator.append(itemLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
		itemLocator$=folderItem.markAppliedUncheckable(console, itemLocator$);
		bookmarksItem.instantiate(console, itemLocator$);
		fail.add(bookmarksItem);		
		
		//extensions
		System.out.println("JEntityAddFacets:getAllFacetAddItems:check extensions");
		Entigrator entigrator=console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listEntities("entity","extension");
		
		String facetAddItemClass$;
		JFacetAddItem addItem;
		System.out.println("JEntityAddFacets:getAllFacetAddItems:sa="+sa.length);
		if(sa!=null){
			Sack extension;
		for(String aSa:sa){
			try{
				extension=entigrator.getEntityAtKey(aSa);
				System.out.println("JEntityAddFacets:getAllFacetAddItems:extension="+extension.getProperty("label"));
				Core[] ca=extension.elementGet("content.jfacet");
				//String extension$;
				String facetAddClass$;
				String icon$;
				String iconResource$;
				if(ca!=null)
					for(Core aCa:ca){
						try{
						facetAddItemClass$=aCa.type;
						addItem=(JFacetAddItem)ExtensionHandler.loadHandlerInstance(entigrator, aSa, facetAddItemClass$);
						facetAddClass$=addItem.getFacetAddClass();
						
						itemLocator$=addItem.getLocator();
						System.out.println("JEntityAddFacets:getAllFacetAddItems:item locator="+itemLocator$);
						itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME ,entihome$);
						itemLocator$=Locator.append(itemLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
						itemLocator$=Locator.append(itemLocator$,BaseHandler.HANDLER_LOCATION ,aSa);
						itemLocator$=Locator.append(itemLocator$,BaseHandler.HANDLER_CLASS ,facetAddClass$);
						//itemLocator$=addItem.markAppliedUncheckable(console, itemLocator$);
						iconResource$=addItem.getIconResource();
						if(iconResource$!=null){
							icon$=ExtensionHandler.loadIcon(entigrator, aSa, iconResource$);
							if(icon$!=null)
								itemLocator$=Locator.append(itemLocator$,Locator.LOCATOR_ICON,icon$);
						}
						System.out.println("JEntityAddFacets:getAllFacetAddItems:item="+addItem.getClass().getName());
						addItem.instantiate(console, itemLocator$);
						fail.add(addItem);
						}catch(Exception eee){
							LOGGER.info(eee.toString());
						}
					}
			}catch(Exception ee){
				LOGGER.info(ee.toString());
			}
		}
	}else
		System.out.println("JEntityAddFacets:getAllFacetAddItems:mo extensions found");	
	Collections.sort(fail,new ItemPanelComparator());
	JFacetAddItem[]faia=fail.toArray(new JFacetAddItem[0]);
	/*
	for(JFacetAddItem fai:faia){
	 	if(fai!=null)
		System.out.println("EntityAddFacets:getAllFacetAddItems:fai="+fai.getTitle());
	}
	*/
	return faia;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
private void applyFacets(){
	//System.out.println("EntityAddFacets:applyFacets:");
	String facetHandlerClass$;
	try{
		String[] sa=listSelectedItems();
        for(String aSa:sa ){
        	
            aSa=Locator.append(aSa, BaseHandler.HANDLER_METHOD, JFacetAddItem.METHOD_ADD_FACET);
            facetHandlerClass$=Locator.getProperty(aSa,JFacetOpenItem.FACET_HANDLER_CLASS);
            if(facetHandlerClass$!=null)
            aSa=Locator.append(aSa,BaseHandler.HANDLER_CLASS,facetHandlerClass$ );
            aSa=Locator.append(aSa,Entigrator.ENTIHOME,entihome$);
            aSa=Locator.append(aSa,EntityHandler.ENTITY_KEY,entityKey$);
            System.out.println("EntityAddFacets:applyFacets:aSa:"+aSa);
            JConsoleHandler.execute(console, aSa);
        }
	}catch(Exception e){
      	LOGGER.severe(e.toString());	
	}
}
/**
 * Get the context title.
 * @return the context title.
 */
@Override
public String getTitle() {
		return "Add facets";
	}
/**
 * Get the context type.
 * @return the context type.
 */
@Override
public String getType() {
		return "Facet selector";
	}
/**
 * Complete the context. No action.
 */
@Override
public void close() {
	}
/**
 * Get the context subtitle.
 * @return the context subtitle.
 */
@Override
public String getSubtitle() {
	return entityLabel$;
}
}
