package gdt.jgui.entity.person;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.PersonHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.*;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.tool.JTextEditor;
public class JPersonEditor extends JFieldsEditor implements JExtendedFacetRenderer{

	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_PERSON="action create person";
	public static final String ACTION_SET_DISPLAY_NAME="action set display name";
	JMenuItem itemCompose;
	public JPersonEditor() {
		super();
		postMenu=new JMenuItem[1];
		itemCompose=new JMenuItem("Compose");
		itemCompose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("JPersonEditor:compose:");
				String displayName$=compose();
				JTextEditor te=new JTextEditor();
				String teLocator$=te.getLocator();
				teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME, entihome$);
				teLocator$=Locator.append(teLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				teLocator$=Locator.append(teLocator$,JTextEditor.TEXT,displayName$);
				teLocator$=Locator.append(teLocator$,JTextEditor.TEXT_TITLE,"Display name");
				teLocator$=Locator.append(teLocator$,JTextEditor.SUBTITLE,entityLabel$);
				
				String responseLocator$=getLocator();
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_CLASS, JPersonEditor.class.getName());
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_LOCATION,JPersonFacetAddItem.EXTENSION_KEY );
				responseLocator$=Locator.append(responseLocator$,JRequester.REQUESTER_ACTION,ACTION_SET_DISPLAY_NAME);
				teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
                JConsoleHandler.execute(console, teLocator$);
				
			}
		} );
		postMenu[0]=itemCompose;
	}
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JPersonEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,PersonHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
	    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "person.png");
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	
	
	}
	
	@Override
	public String getTitle() {
		return "Person";
	}
	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}
	@Override
	public String getType() {
			return "person";
	}
	@Override
	public String getFacetHandler() {
		return PersonHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "person";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		    return ExtensionHandler.loadIcon(entigrator,PersonHandler.EXTENSION_KEY, "person.png");
		}

	@Override
	public String getCategoryTitle() {
		return "Persons";
	}
	
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=PersonHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
					//System.out.println("JPhoneEditor:reindex:1:entity="+entity.getProperty("label"));
		    		entity.putElementItem("jfacet", new Core(JPersonFacetAddItem.class.getName(),fhandler$,JPersonFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,JPersonFacetAddItem.EXTENSION_KEY));
					entigrator.ent_alter(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Person"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Person entity");
	    JPersonEditor pe=new JPersonEditor();
	    String peLocator$=pe.getLocator();
	    Properties responseLocator=Locator.toProperties(peLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Person");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);
		JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JPersonEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				Sack newEntity=entigrator.ent_new("person", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"Suffix",null));
				newEntity.putElementItem("field", new Core(null,"Prefix",null));
				newEntity.putElementItem("field", new Core(null,"Given name",null));
				newEntity.putElementItem("field", new Core(null,"Family name",null));
				newEntity.putElementItem("field", new Core(null,"Phonetic middle name",null));
				newEntity.putElementItem("field", new Core(null,"Phonetic given name",null));
				newEntity.putElementItem("field", new Core(null,"Phonetic family name",null));
				newEntity.putElementItem("field", new Core(null,"Middle name",null));
				newEntity.putElementItem("field", new Core(null,"Display name",null));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,PersonHandler.class.getName(),JPersonFacetAddItem.EXTENSION_KEY));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.person.JPersonFacetAddItem",PersonHandler.class.getName(),"gdt.jgui.entity.person.JPersonFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","person.png"));
				//entigrator.save(newEntity);
				entigrator.ent_alter(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "person", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(getClass(), "person.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
				//newEntity.print();
				reindex(console, entigrator, newEntity);
				JEntityFacetPanel efp=new JEntityFacetPanel(); 
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Locator.LOCATOR_TITLE,newEntity.getProperty("label"));
				efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, newEntity.getKey());
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_LABEL, newEntity.getProperty("label"));
				JEntityPrimaryMenu.reindexEntity(console, efpLocator$);
				Stack<String> s=console.getTrack();
				s.pop();
				console.setTrack(s);
				JConsoleHandler.execute(console, efpLocator$);
				return;
			}
			if(ACTION_SET_DISPLAY_NAME.equals(action$)){
				//System.out.println("JPersonEditor:response:set display name="+text$);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entity.putElementItem("field", new Core(null,"Display name",text$));
				entigrator.ent_alter(entity);
				String feLocator$=getLocator();
				//feLocator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
				//feLocator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
				JConsoleHandler.execute(console, feLocator$);
				return;
			}
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				String cellField$=locator.getProperty(CELL_FIELD);
				String name$=locator.getProperty(CELL_FIELD_NAME);
				Core core=entity.getElementItem("field", name$);
				if(CELL_FIELD_NAME.equals(cellField$))
					core.name=text$;
				else if (CELL_FIELD_VALUE.equals(cellField$))
					core.value=text$;
//				System.out.println("FieldsEditor:response:name="+core.name+" value="+core.value);
				entity.putElementItem("field", core);
				entigrator.ent_alter(entity);
				String feLocator$=getLocator();
				feLocator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
				feLocator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
				JConsoleHandler.execute(console, feLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
	
	}
	private String compose() {
		try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack person=entigrator.getEntityAtKey(entityKey$);
        StringBuffer sb = new StringBuffer();
        String field$ = person.getElementItemAt("field", "Prefix");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ' ');
            }
        field$ = person.getElementItemAt("field", "Given name");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ' ');
            }
        field$ = person.getElementItemAt("field", "Middle name");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ' ');
            }
        field$ = person.getElementItemAt("field", "Family name");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ',');
            }
        field$ = person.getElementItemAt("field", "Suffix");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$);
            }
        String displayName$=sb.toString();
        return displayName$;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
			return null;
		}
    }	
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			//System.out.println("JPersonEditor:adaptRename:locator="+locator$);
			if(console==null)
				System.out.println("JPersonEditor:adaptRename:console is null");
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			PersonHandler personHandler=new PersonHandler();
			personHandler.instantiate(entityLocator$);
			personHandler.adaptRename(entigrator);
		}catch(Exception e){
			Logger.getLogger(JPersonEditor.class.getName()).severe(e.toString());
		}
	}
	@Override
	public JFacetRenderer instantiate(JMainConsole console, String locator$) {
		try{
			//System.out.println("JMovieEditor.instantiate:begin");
				this.console=console;
				Properties locator=Locator.toProperties(locator$);
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				if(entityKey$!=null)
					return super.instantiate(console, locator$);
				else
					return this;
			}catch(Exception e){
				Logger.getLogger(getClass().getName()).severe(e.toString());
			}
			return this;
	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JPersonFacetOpenItem.class.getName();
	}
	@Override
	public String getFacetIcon() {
		
		return "person.png";
	}	
}
