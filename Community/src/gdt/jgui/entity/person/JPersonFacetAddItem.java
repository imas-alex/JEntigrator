package gdt.jgui.entity.person;
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
import gdt.data.entity.PersonHandler;
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
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;
public class JPersonFacetAddItem extends JFacetAddItem{
	private static final long serialVersionUID = 1L;
	public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";
	private Logger LOGGER=Logger.getLogger(JPersonFacetAddItem.class.getName());
    String entityLabel$;
    public JPersonFacetAddItem(){
		super();
	}
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Person");
	locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_ADD_COMPONENT);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,PersonHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Person add ");
	locator.setProperty(Locator.LOCATOR_TITLE,"person");
	locator.setProperty(JFacetOpenItem.FACET_HANDLER_CLASS,PersonHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null){
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	}
	locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
	locator.setProperty( Locator.LOCATOR_ICON_FILE, "person.png");
	 locator$=Locator.toString(locator);
	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	 return Locator.toString(locator);
}
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JPersonFacetAddItem:response:locator:"+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String mode$=locator.getProperty(JFacetAddItem.ADD_MODE);
		if(JFacetAddItem.ADD_MODE_COMPONENT.equals(mode$)){
			String componentLabel$=locator.getProperty(JTextEditor.TEXT);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack component=entigrator.ent_new("person", componentLabel$);
			component.createElement("fhandler");
			component.putElementItem("fhandler", new Core(null,PersonHandler.class.getName(),null));
			component.createElement("jfacet");
			component.putElementItem("jfacet", new Core(getClass().getName(),PersonHandler.class.getName(),JPersonFacetOpenItem.class.getName()));
			component.createElement("field");
			component.putElementItem("field", new Core(null,"Name","Value"));
			entigrator.ent_alter(component);
			entigrator.saveHandlerIcon(getClass(), "person.png");
			Sack container=entigrator.getEntityAtKey(entityKey$);
			entigrator.col_addComponent(container, component);
			JEntityFacetPanel efp=new JEntityFacetPanel();
			String efpLocator$=efp.getLocator();
			efpLocator$=Locator.append(efpLocator$,Entigrator.ENTIHOME,entihome$);
			efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_KEY,component.getKey());
		    JConsoleHandler.execute(console, efpLocator$);	
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
@Override
public void addFacet(JMainConsole console, String locator$) {
	try{
	//   System.out.println("JPersonFacetAddItem:addFacet:locator:"+locator$);
	   Properties locator=Locator.toProperties(locator$);
	   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	   Entigrator entigrator=console.getEntigrator(entihome$);
	   Sack entity=entigrator.getEntityAtKey(entityKey$);
	   if(!entity.existsElement("fhandler"))
		   entity.createElement("fhandler");
	   entity.putElementItem("fhandler", new Core(null,PersonHandler.class.getName(),null)); 
	   if(!entity.existsElement("jfacet"))
		   entity.createElement("jfacet");
	   entity.putElementItem("jfacet", new Core(JPersonFacetAddItem.class.getName(),PersonHandler.class.getName(),JPersonFacetOpenItem.class.getName()));
	   entigrator.ent_alter(entity);
	   entity=entigrator.ent_assignProperty(entity, "fields", entity.getProperty("label"));
	   entity=entigrator.ent_assignProperty(entity, "person", entity.getProperty("label"));
	}catch(Exception e){
		  LOGGER.severe(e.toString());
	  }
}
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
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, label$+".fields."+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Component label");
	    editorLocator$=Locator.append(editorLocator$,JTextEditor.TEXT_TITLE,"Add fields component");
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
		  LOGGER.severe(e.toString());
	  }
	
}
@Override
public FacetHandler getFacetHandler() {
	return new PersonHandler();
}
@Override
public String  markAppliedUncheckable(JMainConsole console,String locator$) {
	try{
		//System.out.println("JPersonFacetAddItem: markAppliedUncheckable:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);	
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(entity==null)
			return null;
		boolean isApplied=false;
		if(entity.getProperty("person")!=null){
			Core fhandler=entity.getElementItem("fhandler",PersonHandler.class.getName());
			if(fhandler!=null){
					Core jfacet=entity.getElementItem("jfacet", PersonHandler.class.getName());
					if(jfacet!=null){
						if( JPersonFacetOpenItem.class.getName().equals(jfacet.value)
								&& JPersonFacetAddItem.class.getName().equals(jfacet.type)){
							isApplied=true;	
						}
					}
			}
		}
		if(isApplied)
			locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_FALSE);
		else
			locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
		this.locator$=locator$;
		return locator$;
		}catch(Exception e){
		LOGGER.info(e.toString());
		}	
		this.locator$=locator$;
		return locator$;
}
@Override
public String getIconResource() {
	// TODO Auto-generated method stub
	return "person.png";
}
@Override
public String getFacetOpenClass() {
	return JPersonFacetOpenItem.class.getName();
}
@Override
public String getFacetAddClass() {
	return JPersonFacetAddItem.class.getName();
}
}


