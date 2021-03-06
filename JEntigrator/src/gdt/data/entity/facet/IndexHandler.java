package gdt.data.entity.facet;
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

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.index.JIndexPanel;
/**
* Contains methods to process an index entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class IndexHandler extends FacetHandler{
	/**
	 * Check if the index handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(!"index".equals(entity.getProperty("entity")))
				return false;
   		    if(entity.getElementItem("fhandler", getClass().getName())==null){	
						if(!entity.existsElement("fhandler"))
							entity.createElement("fhandler");
							entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
							entigrator.ent_alter(entity);
					}
			return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
			return false;
			}
	}
	 /**
     * Get title of the index handler.  
     * @return the title of the index handler..
     */	
	@Override
	public String getTitle() {
		return "Index";
	}
	 /**
     * Get type of the index handler.  
     * @return the type of the index handler..
     */	
	@Override
	public String getType() {
		return "index";
	}
	 /**
     * Get class name of the index handler.  
     * @return the class name of the index handler..
     */	
	@Override
	public String getClassName() {
		return IndexHandler.class.getName();
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "index", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
/**
* Adapt the clone of the entity.  
*/
	@Override
	public void adaptClone(Entigrator entigrator) {
		  adaptLabel(entigrator);
	}
	/**
	 * Adapt the the entity after rename.   
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		  adaptLabel(entigrator);
		
	}
	@Override
	public void completeMigration(Entigrator entigrator) {
		try{
			Sack index=entigrator.getEntityAtKey(entityKey$);
			Core[]ca=index.elementGet("index.jlocator");
			if(ca==null)
				return;
			String oldEntihome$;
			String filePath$;
			for(Core c:ca){
				oldEntihome$=Locator.getProperty(c.value, Entigrator.ENTIHOME);
				c.value=Locator.append(c.value, Entigrator.ENTIHOME, entigrator.getEntihome());
				filePath$=Locator.getProperty(c.value, JFolderPanel.FILE_PATH);
				if(filePath$!=null){
					try{
					File sourceFile=new File(filePath$);
					filePath$=filePath$.replace(oldEntihome$, entigrator.getEntihome());
					File targetFile=new File(filePath$);
					File targetFolder=targetFile.getParentFile();
					if(!targetFolder.exists())
							targetFolder.mkdirs();
					if(!targetFile.exists())
						targetFile.createNewFile();
					FileExpert.copyFile(sourceFile, targetFile);
					c.value=Locator.append(c.value, JFolderPanel.FILE_PATH, filePath$);
					}catch(Exception ee){
						Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());	
					}
				}
				index.putElementItem("index.jlocator", c);
			}
			
			entigrator.ent_alter(index);
		}catch(Exception e){
			Logger.getLogger(JBaseNavigator.class.getName()).severe(e.toString());
		}
		
	}
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
