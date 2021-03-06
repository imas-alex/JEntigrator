package gdt.jgui.entity.bookmark;
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
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetAddItem;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the bookmarks facet in the list
 * of available facets to add.
 * @author imasa
 *
 */
public class JBookmarksFacetAddItem extends JFacetAddItem{
	private static final long serialVersionUID = 1L;
	/**
	 * Get the add facet item locator.
	 * @return the locator string.
	 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Bookmarks");
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_ADD_COMPONENT);
		locator.setProperty( JContext.CONTEXT_TYPE,"Bookmarks add ");
		locator.setProperty(Locator.LOCATOR_TITLE,"Bookmarks");
		locator.setProperty(JFacetOpenItem.FACET_HANDLER_CLASS,BookmarksHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		 locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"bookmark.png");
		 locator$=Locator.toString(locator);
		locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		 return Locator.toString(locator);
	}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String mode$=locator.getProperty(JFacetAddItem.ADD_MODE);
			if(JFacetAddItem.ADD_MODE_COMPONENT.equals(mode$)){
				String componentLabel$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);
				Sack component=entigrator.ent_new("bookmarks", componentLabel$);
				component.createElement("fhandler");
				component.putElementItem("fhandler", new Core(null,BookmarksHandler.class.getName(),null));
				component.createElement("jfacet");
				component.putElementItem("jfacet", new Core(getClass().getName(),BookmarksHandler.class.getName(),JBookmarksFacetOpenItem.class.getName()));
					entigrator.ent_alter(component);
				entigrator.saveHandlerIcon(JEntitiesPanel.class, "bookmark.png");
				Sack container=entigrator.getEntityAtKey(entityKey$);
				entigrator.col_addComponent(container, component);
				JEntityFacetPanel efp=new JEntityFacetPanel();
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Entigrator.ENTIHOME,entihome$);
				efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_KEY,component.getKey());
			    JConsoleHandler.execute(console, efpLocator$);	
			}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}

	/**
	 * Add facet to the entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	
	public void addFacet(JMainConsole console, String locator$) {
		try{
			
			Properties locator=Locator.toProperties(locator$);
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack entity=entigrator.getEntityAtKey(entityKey$);
			   if(!entity.existsElement("fhandler"))
				   entity.createElement("fhandler");
			   entity.putElementItem("fhandler", new Core(null,BookmarksHandler.class.getName(),null)); 
			   if(!entity.existsElement("jfacet"))
				   entity.createElement("jfacet");
			   entity.putElementItem("jfacet", new Core(JBookmarksFacetAddItem.class.getName(),BookmarksHandler.class.getName(),JBookmarksFacetOpenItem.class.getName()));
			   entigrator.ent_alter(entity);
			   entity=entigrator.ent_assignProperty(entity, "bookmarks", entity.getProperty("label"));
			}catch(Exception e){
				Logger.getLogger(getClass().getName()).severe(e.toString());
			  }
	}
	/**
	 * Create an entity of the facet type 
	 * and add it as a component to the entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void addComponent(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
		    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		    Entigrator entigrator=console.getEntigrator(entihome$);
		    String label$=entigrator.indx_getLabel(entityKey$);
			JTextEditor textEditor=new JTextEditor();
		    String editorLocator$=textEditor.getLocator();
		    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, label$+".bookmark."+Identity.key().substring(0,4));
		    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Component label");
		    editorLocator$=Locator.append(editorLocator$,JTextEditor.TEXT_TITLE,"Add bookmarks component");
		    if(entihome$!=null)
		    	 editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);	
		  
		    String responseLocator$=getLocator();
		    responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
		    responseLocator$=Locator.append(responseLocator$, Entigrator.ENTIHOME, entihome$);
		    responseLocator$=Locator.append(responseLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		    responseLocator$=Locator.append(responseLocator$, ADD_MODE, ADD_MODE_COMPONENT);
		    String requesterResponseLocator$=Locator.compressText(responseLocator$);
		    editorLocator$=Locator.append(editorLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		    JConsoleHandler.execute(console, editorLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		  }
	}
/**
 * Get facet handler instance.
 * @return the facet handler instance.
 * 
 */
	@Override
	public FacetHandler getFacetHandler() {
		return new BookmarksHandler();
	}
@Override
public String getIconResource() {
	return "bookmark.png";
}
@Override
public String getFacetOpenClass() {
	return JBookmarksFacetOpenItem.class.getName();
}
public String getFacetAddClass() {
	return JBookmarksFacetAddItem.class.getName();
}
}
