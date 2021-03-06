package gdt.data.entity;
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
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.entity.facet.FieldsHandler;
public class PersonHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";	
private Logger LOGGER=Logger.getLogger(FileHandler.class.getName());
//String entihome$;
//String entityKey$;
public final static String PERSON="person";
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("PhoneHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("PhoneHandler:isApplied:entity="+entity.getProperty("label"));
			String person$=entity.getProperty("person");
			if(person$!=null&&!Locator.LOCATOR_FALSE.equals(person$)){
			   if(entity.getElementItem("fhandler", getClass().getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
					entigrator.ent_alter(entity);
				}
	            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}
	public String getTitle() {
		return "Person";
	}

	public String getType() {
		return "person";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "person", entity.getProperty("label"));
		    }catch(Exception e){
		    	
		    }
	}
	@Override
	public void adaptClone(Entigrator entigrator) {
	   adaptLabel(entigrator);
		
	}

	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}


@Override
public String getClassName() {
	return PersonHandler.class.getName();
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}