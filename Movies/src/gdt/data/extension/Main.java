package gdt.data.extension;
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
import java.net.URLDecoder;
import java.util.logging.Logger;
import gdt.data.entity.facet.ExtensionMain;
import gdt.data.grain.Core;
import gdt.data.grain.Sack;
import gdt.data.store.*;
public class Main implements ExtensionMain{
	 public static final String EXTENSION_KEY="_35a4Gr4U9MGmswmMRFtgK2erNo8";
	 private static final String EXTENSION_LABEL="movies";
	 private static final String EXTENSION_JAR="movies.jar";
	public void main(String[] args) {
      
		final String[] sa=args;
        if(sa!=null)
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              try{
           	//System.out.println("Community:main");
            	  
               String entihome$=sa[0];
               System.out.println("Movie:main.entihome="+entihome$);
               Entigrator entigrator=new Entigrator(new String[]{entihome$});
           //    System.out.println(entigrator.getEntihome());
               makeExtension(entigrator);
               String folder$=entigrator.ent_getHome(EXTENSION_KEY);
               File folder=new File(folder$);
               if(!folder.exists())
            	   folder.mkdir();
               String path$ = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
             // System.out.println("Community:main.path="+path$);
              String jar$ = URLDecoder.decode(path$, "UTF-8");
              jar$=jar$.replace("file:", "");
              jar$=jar$.replace("!/", "");
              File jar=new File(jar$);
           //    System.out.println("Community:main:jar="+jar.getPath());
               File target=new File(folder$+"/"+EXTENSION_JAR);
               if(!target.exists())
            	   target.createNewFile();
               FileExpert.copyFile(jar, target);
                }catch(Exception e ){
            	  Logger.getLogger(Main.class.getName()).severe(e.toString());
              }
            }
        });
    }
	private static Sack makeExtension(Entigrator entigrator){
		 Sack extension=entigrator.getEntityAtKey(EXTENSION_KEY);
         if(extension!=null)
        	 entigrator.deleteEntity(extension);
        	  extension=new Sack();
              extension.putAttribute(new Core("String", "residence.base",Entigrator.ENTITY_BASE));
              extension.putAttribute(new Core("String", "alias", EXTENSION_LABEL));
              extension.setKey(EXTENSION_KEY);
              String path$=entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/"+EXTENSION_KEY;
              extension.setPath(path$);
        	  if(!extension.saveXML(path$)){
        		  System.out.println("Main:makeExtension:cannot save extension="+entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/"+EXTENSION_KEY) ;
        		  return null;
        	  }
        	  entigrator.ent_reindex(extension);
              extension=entigrator.ent_assignLabel(extension, EXTENSION_LABEL);
              extension=entigrator.ent_assignProperty(extension, "entity", "extension");
              extension=entigrator.ent_assignProperty(extension, "extension",EXTENSION_LABEL);
         
         if(!extension.existsElement("fhandler"))
        	 extension.createElement("fhandler");
         else
        	 extension.clearElement("fhandler");
         extension.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.ExtensionHandler",null));
         extension.putElementItem("jfacet", new Core(null,"gdt.data.entity.facet.FieldsHandler",null));
         if(!extension.existsElement("jfacet"))
        	 extension.createElement("jfacet");
         else
        	 extension.clearElement("jfacet");
         extension.putElementItem("jfacet", new Core(null,"gdt.data.entity.facet.ExtensionHandler","gdt.jgui.entity.extension.JExtensionFacetOpenItem"));
         extension.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem","gdt.data.entity.facet.FieldsHandler","gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
         if(!extension.existsElement("field"))
        	 extension.createElement("field");
         else
        	 extension.clearElement("field");
         extension.putElementItem("field", new Core(null,"lib","movies.jar"));
         //extension.putElementItem("field", new Core(null,"res","community.tar"));
         entigrator.ent_assignProperty(extension, "fields",EXTENSION_LABEL );
         if(!extension.existsElement("content.fhandler"))
        	 extension.createElement("content.fhandler");
         else
        	 extension.clearElement("content.fhandler");
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.MovieHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.RoleHandler",EXTENSION_KEY));
         if(!extension.existsElement("content.jfacet"))
        	 extension.createElement("content.jfacet");
         else
        	 extension.clearElement("content.jfacet");
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.movie.JMovieFacetAddItem","gdt.data.entity.MovieHandler","gdt.jgui.entity.movie.JMovieFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.role.JRoleFacetAddItem","gdt.data.entity.RoleHandler","gdt.jgui.entity.role.JRoleFacetOpenItem"));
         if(!extension.existsElement("content.jrenderer"))
        	 extension.createElement("content.jrenderer");
         else
        	 extension.clearElement("content.jrenderer");
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.MovieHandler","gdt.jgui.entity.movie.JMovieEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.RoleHandler","gdt.jgui.entity.role.JRoleEditor"));
         
         return extension;
	}
}
