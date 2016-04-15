package gdt.jgui.base;
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
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JArchivePanel;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
/**
* This context displays of all entities belonging to the category.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JCategoryPanel extends JItemsListPanel {
	private static final long serialVersionUID = 1L;
	public static final String RENDERER = "renderer";
	String entihome$;
	String renderer$;
	String entityType$;
	String categoryIcon$;
	String categoryTitle$;
	JMenu menu;
	JMenuItem deleteItem;
	JMenuItem copyItem;
	Hashtable<String,JItemPanel> items;
	private JMenuItem[] mia;
	/**
	 * Default constructor
	 *  
	 */
	public JCategoryPanel() {
		super();
	}
	/**
	 * Get context locator. 
	 * @return the locator.
	 */	
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null)
	    	locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(renderer$!=null)
		    	locator.setProperty(RENDERER,renderer$);
	    if(entityType$!=null)
	    	locator.setProperty(EntityHandler.ENTITY_TYPE,entityType$);
	    if(categoryIcon$!=null)
	    	 locator.setProperty(Locator.LOCATOR_ICON, categoryIcon$);
		if(categoryTitle$!=null) 
		   locator.setProperty(Locator.LOCATOR_TITLE, categoryTitle$);
	   else
	       locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	   
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */	
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
	//	System.out.println("JCategory:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		try{
		this.console=console;
//		System.out.println("CategoryPanel:instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);	
		renderer$=locator.getProperty(RENDERER);
	//	System.out.println("JCategoryPanel:instantiate:renderer="+renderer$);
		JFacetRenderer facetRenderer=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, renderer$);
		entityType$=facetRenderer.getEntityType();
    	categoryIcon$=facetRenderer.getCategoryIcon();
		categoryTitle$=facetRenderer.getCategoryTitle();
		this.locator$=getLocator();

		JItemPanel[] ipa=listCategoryMembers(console, this.locator$);
	
		putItems(ipa);
		}catch(Exception e){
		Logger.getLogger(getClass().getName()).info(e.toString());
		}
		return this;
	}
	/**
	 * Get context menu. 
	 * @return the context menu.
	 */	
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		mia=null;
		 int cnt=menu.getItemCount();
		 if(cnt>0){
			 mia=new JMenuItem[cnt];
			for(int i=0;i<cnt;i++) 
			 mia[i]=menu.getItem(i);
		 }
		//menu.addSeparator();
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
			menu.removeAll();
			if(mia!=null){
				for(JMenuItem mi:mia)
					menu.add(mi);
			}
		JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JAllCategoriesPanel acp=new JAllCategoriesPanel();
					String acpLocator$=acp.getLocator();
					acpLocator$=Locator.append(acpLocator$,Entigrator.ENTIHOME,entihome$);
					JConsoleHandler.execute(console, acpLocator$);
				}
			} );
			menu.add(doneItem);
			JMenuItem newItem = new JMenuItem("New");
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
		//			System.out.println("JCategoryPanel:new:renderer="+renderer$);
					Entigrator entigrator=console.getEntigrator(entihome$);	
					JFacetRenderer facetRenderer=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, renderer$);
					String fcLocator$=facetRenderer.getLocator();
					fcLocator$=Locator.append(fcLocator$, Entigrator.ENTIHOME, entihome$);
					facetRenderer.newEntity(console, fcLocator$);
					
				}
			} );
			menu.add(newItem);
			if(hasSelectedItems()){
				menu.addSeparator();
			deleteItem = new JMenuItem("Delete");
			deleteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
						        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					   if (response == JOptionPane.YES_OPTION) {
						  String[] sa=JCategoryPanel.this.listSelectedItems();
						  if(sa==null)
							  return;
						  String candidate$;
						  ArrayList<String>sl=new ArrayList<String>();
						  for(String aSa:sa){
							  candidate$=Locator.getProperty(aSa, EntityHandler.ENTITY_KEY);
							  if(candidate$!=null)
								  sl.add(candidate$);
						   sa=sl.toArray(new String[0]);
						   String list$=Locator.toString(sa);
						   String baseHandlerLocator$=BaseHandler.getLocator();
						   baseHandlerLocator$=Locator.append(baseHandlerLocator$, EntityHandler.ENTITY_LIST,list$ );
						   baseHandlerLocator$=Locator.append(baseHandlerLocator$, Entigrator.ENTIHOME,entihome$ );
						   baseHandlerLocator$=Locator.append(baseHandlerLocator$, BaseHandler.HANDLER_METHOD,BaseHandler.BASE_METHOD_DELETE_ENTITIES );
						   JConsoleHandler.execute(console, baseHandlerLocator$);
						   JConsoleHandler.execute(console,locator$);
						  }
					   }
					
				}
			} );
			menu.add(deleteItem);
			copyItem = new JMenuItem("Copy");
			copyItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JItemPanel[] ipa=JCategoryPanel.this.getItems();
					ArrayList<String>sl=new ArrayList<String>();
					for(JItemPanel ip:ipa)
						if(ip.isChecked())
							sl.add(ip.getLocator());
					String[]sa=sl.toArray(new String[0]);
					console.clipboard.clear();
					for(String aSa:sa)
						console.clipboard.putString(aSa); 
				}
			} );
			menu.add(copyItem);
			JMenuItem archiveItem = new JMenuItem("Archive");
			archiveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JItemPanel[] ipa=JCategoryPanel.this.getItems();
					Entigrator entigrator=console.getEntigrator(entihome$);
					Properties locator;
					ArrayList<String>sl=new ArrayList<String>();
					String entityKey$;
					for(JItemPanel ip:ipa){
						if(ip.isChecked()){
						    locator=Locator.toProperties(ip.getLocator());
						    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						    sl.add(entityKey$);
						}
					}
					String[] sa=sl.toArray(new String[0]);
					String[] ea=JReferenceEntry.getCoalition(console, entigrator, sa);
					JArchivePanel archivePanel=new JArchivePanel();
		        	String apLocator$=archivePanel.getLocator();
	        	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
				  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
			      String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "archive.png");
			      apLocator$=Locator.append(apLocator$, Locator.LOCATOR_ICON,icon$);
				  JConsoleHandler.execute(console, apLocator$);
					}
			} );
			menu.add(archiveItem);
			
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
	
	private  JItemPanel[] listCategoryMembers(JMainConsole console,String locator$){
		try{
			//System.out.println("EntitesPanel:listEntities:locator="+locator$);
			   Properties locator=Locator.toProperties(locator$);
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String entityType$=locator.getProperty(EntityHandler.ENTITY_TYPE);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   String[] sa=entigrator.indx_listEntitiesAtPropertyName(entityType$);
			   if(sa==null){
				   Logger.getLogger(JEntitiesPanel.class.getName()).info("empty category="+entityType$);
				   return null;
			   }
 		   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		   JItemPanel itemPanel;
	       String emLocator$;
	       Core [] ca=entigrator.indx_getMarks(sa);
	       JEntityFacetPanel em;
	       String icon$;
		   for(Core c:ca){
			   try{
			   itemPanel=getItem(c.name);
			   if(itemPanel==null){
			   em=new JEntityFacetPanel();
			   emLocator$=em.getLocator();
			   emLocator$=Locator.append(emLocator$,Entigrator.ENTIHOME , entihome$);
			   emLocator$=Locator.append(emLocator$,EntityHandler.ENTITY_KEY ,c.name);
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_TITLE, c.value);
			   icon$=entigrator.readIconFromIcons(c.type);
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_ICON, icon$);
			   itemPanel=new JItemPanel(console,emLocator$);
			   putItem(c.name, itemPanel);
			   }
			   if(itemPanel!=null)
			      ipl.add(itemPanel);
			   }catch(Exception ee){
				   Logger.getLogger(JCategoryPanel.class.getName()).info(ee.toString());
			   }
		   }
		   Collections.sort(ipl,new ItemPanelComparator());
		   return ipl.toArray(new JItemPanel[0]);
					   
		}catch(Exception e) {
        	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
            return null;
        }
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */	
	@Override
	public String getTitle() {
		if(categoryTitle$!=null)
			return categoryTitle$;
		else
		return "Category panel";
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */
	@Override
	public String getSubtitle() {
		return entihome$;
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */
	@Override
	public String getType() {
		return "Category panel";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
	}
	private void putItem(String key$,JItemPanel item){
		if(items==null)
			items=new Hashtable<String,JItemPanel>();
		items.put(key$, item);
	}
	private JItemPanel getItem(String key$){
		if(items==null)
			return null;
		return (JItemPanel)Support.getValue(key$, items);
	}
}
