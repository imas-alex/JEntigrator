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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.console.JFacetOpenItem;
/**
* Contains methods to process an extension entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class ExtensionHandler extends FacetHandler{
	/**
	* Constant for the entity type.
	*/	
public static final String EXTENSION="extension";
private Logger LOGGER=Logger.getLogger(ExtensionHandler.class.getName());
/**
 * Check if the extension handler is applied to the entity  
 *  @param entigrator entigrator instance
 *  @param locator$ entity's locator 
 * @return true if applied false otherwise.
 */	
static boolean debug=false;
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(EXTENSION.equals(entity.getProperty("entity")))
				return true;
			else
				return false;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
		
	}
	/**
	 * Get an array of all facet handlers for all extensions in the database  
	 *  @param entigrator entigrator instance 
	 * @return array of facet handlers.
	 */	
	public static FacetHandler[] listExtensionHandlers( Entigrator entigrator){
		try{
		String[] sa=entigrator.indx_listEntities("entity","extension");
		if(sa==null)
			return null;
			ArrayList<FacetHandler>fl=new ArrayList<FacetHandler>();
		FacetHandler[] fha;	
		for(String aSa:sa){
			try{
			fha=listExtensionHandlers( entigrator,aSa);
			if(fha!=null)
				for(FacetHandler fh:fha)
					fl.add(fh);
			}catch(Exception ee){
				System.out.println("ExtesionHandler:listExtensionHandlers:extension="+aSa+" error:"+ee.toString());
			}
		}
		return fl.toArray(new FacetHandler[0]);
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
	}
	/**
	 * Get the handler instance.  
	 *  @param entigrator entigrator instance
	 *  @param extension$ extension key
	 *  @param handlerClass$ class name
	 * @return handler instance.
	 */	
	
	public static Object loadHandlerInstance(Entigrator entigrator,String extension$,String handlerClass$){
		try{
		if(debug)
			System.out.println("ExtensionHandler:loadHandlerInstance:extension="+extension$+" handler="+handlerClass$);
		Object obj=entigrator.getHandler(handlerClass$);
		if(obj!=null)
			return obj;
			
		Sack extension=entigrator.getEntityAtKey(extension$);
			String lib$=extension.getElementItemAt("field", "lib");
			String jar$="jar:file:" +entigrator.getEntihome()+"/"+extension$+"/"+lib$+"!/";
			if(debug)
				System.out.println("ExtensionHandler:loadHandlerInstance:jar="+jar$);
			ArrayList <URL> urll=new ArrayList<URL>();
			urll.add(new URL(jar$));
			String[]sa=extension.elementListNoSorted("external");
			if(sa!=null){
				File file; 
				for(String s:sa){
					if(debug)
						System.out.println("ExtensionHandler:loadHandlerInstance:s="+s);
					
					file=new File(entigrator.getEntihome()+"/"+extension$+"/"+s);
					if(file.exists())
						urll.add(new URL("jar:file:"+file.getPath()+"!/"));
				}
			}
			URL[] urls=urll.toArray(new URL[0]);
			Core c=null;
			String super$=null;
			String facetHandler$;
			//facet handler
			c=extension.getElementItem("content.fhandler", handlerClass$);
			if(c!=null)
				super$=c.type;
			if(super$==null){
				facetHandler$=extension.getElementItemAtValue("content.jrenderer", handlerClass$);
				if(facetHandler$!=null)
				   super$=extension.getElementItem("content.jrenderer",facetHandler$).type;
			}	
			if(super$==null)
				super$=extension.getElementItemAt("content.super", handlerClass$);
			if(debug)
				System.out.println("ExtensionHandler:loadHandlerInstance: handler="+handlerClass$+" super="+super$);
		
			
			URLClassLoader cl;
			if(super$==null||"null".equals(super$))
				cl= new URLClassLoader(urls);
			else{
				Class sh=Class.forName(super$);
				ClassLoader shLoader=sh.getClassLoader();
				cl= new URLClassLoader(urls,shLoader);
			}
			
			Class cls=cl.loadClass(handlerClass$);
			//cl.close();
			if(debug)
				System.out.println("ExtensionHandler:loadHandlerInstance:2");
			if(cls==null)
				if(debug)
					System.out.println("ExtensionHandler:loadHandlerInstance:cannot load class="+handlerClass$);

			try{
				
				Constructor[] ctors = cls.getDeclaredConstructors();
				Constructor ctor = null;
				for (int i = 0; i < ctors.length; i++) {
				    ctor = ctors[i];
				    if (ctor.getGenericParameterTypes().length == 0)
					break;
				}
				if(debug)
					System.out.println("ExtensionHandler:loadHandlerInstance:constructor="+ctor.toGenericString());
				ctor.setAccessible(true);
		 	    obj = ctor.newInstance();
			}catch(java.lang.NoClassDefFoundError ee){
				System.out.println("ExtensionHandler:loadHandlerInstance:"+ee.toString());
				return null;
			}
			entigrator.putHandler(handlerClass$,obj);
		    return obj;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	
	private static FacetHandler[] listExtensionHandlers( Entigrator entigrator,String extension$){
		try{
		
		Sack extension=entigrator.getEntityAtKey(extension$);
		String lib$=extension.getElementItemAt("field", "lib");
		if(debug)
			System.out.println("ExtesionHandler:listExtensionHandlers:extension="+extension.getProperty("label")+" jar="+lib$);
			
		Core[] ca=extension.elementGet("content.fhandler");
		if(ca==null)
			return null;
		
		ArrayList<FacetHandler>fl=new ArrayList<FacetHandler>();
		FacetHandler fh;
		Class<?> cls=null;
		String jar$="jar:file:" +entigrator.getEntihome()+"/"+extension$+"/"+lib$+"!/";
		URL[] urls = { new URL(jar$) };
		Class sh=null;
		ClassLoader shLoader;
		URLClassLoader cl;
		Object obj;
		for(Core c:ca){
			try{
				
				if(debug)
					System.out.println("ExtensionHandler:listExtensionHandlers:facet handler="+c.name);				
				obj=entigrator.getHandler(c.name);
				if(obj!=null){
					if(debug)
						System.out.println("ExtensionHandler:listExtensionHandlers:obj="+obj.getClass().getName());				
					if(obj instanceof FacetHandler){	
					fl.add((FacetHandler)obj);
					continue;
					}
				}
				 if(c.type==null||"null".equals(c.type))
					 cl= new URLClassLoader(urls);
				 else{
				 sh=Class.forName(c.type);
				 shLoader=sh.getClassLoader();
				 cl= new URLClassLoader(urls,shLoader);
				 if(debug)
						System.out.println("ExtensionHandler:listExtensionHandlers:super class ="+c.type);
				 }
			  try{
				cls=cl.loadClass(c.name);
			  }catch(java.lang.ClassNotFoundException ee ){
				  if(debug)
					  System.out.println("ExtensionHandler:listExtensionHandlers:"+ee.toString());
				   continue; 
			  }
			   entigrator.putHandler(c.name, cls);
			fh=(FacetHandler)cls.newInstance();
			entigrator.putHandler(c.name, fh);
			fl.add(fh);
			}catch(Exception ee){
				Logger.getLogger(ExtensionHandler.class.getName()).severe("load class: "+ee.toString());
			}
		}
		return fl.toArray(new FacetHandler[0]);
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
	}
	public static String loadIcon(Entigrator entigrator,String extension$,String resource$){
		try{
	if(debug)		
	System.out.println("ExtensionHandler:loadIcon:extension="+extension$+" resource="+resource$);
			Sack extension=entigrator.getEntityAtKey(extension$);
			String lib$=extension.getElementItemAt("field", "lib");
			String jar$=entigrator.getEntihome()+"/"+extension$+"/"+lib$;
			if(new File(entigrator.getEntihome()+"/"+extension$+"/res.jar").exists())
				jar$=entigrator.getEntihome()+"/"+extension$+"/res.jar";
	if(debug)		
			System.out.println("ExtensionHandler:loadIcon:jar="+jar$);
			  ZipFile zf = new ZipFile(jar$);
			    Enumeration<? extends ZipEntry> entries = zf.entries();
			    ZipEntry ze;
			    String[] sa;
			    while (entries.hasMoreElements()) {
			      try{
			    	ze = entries.nextElement();
			      sa=ze.getName().split("/");
			     if(debug) 
			      System.out.println("ExtensionHandler:loadIcon:zip entry="+sa[sa.length-1]+" resource="+resource$);
			      if(resource$.equals(sa[sa.length-1])){
			    	  InputStream is=zf.getInputStream(ze);
			    	  if(debug)
			    	  System.out.println("ExtensionHandler:loadIcon:input stream="+is.toString());
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
				            byte[] b = new byte[1024];
				            int bytesRead = 0;
				            while ((bytesRead = is.read(b)) != -1) {
				               bos.write(b, 0, bytesRead);
				            }
				            byte[] ba = bos.toByteArray();
				            is.close();
				           return Base64.encodeBase64String(ba);
			      }
			      }catch(Exception e){
			    	  
			      }
			    }
			return null;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	public static InputStream getResourceStream(Entigrator entigrator,String extension$,String resource$){
		try{
	if(debug)		
	System.out.println("ExtensionHandler:getResourceStream:extension="+extension$+" resource="+resource$);
			Sack extension=entigrator.getEntityAtKey(extension$);
			String lib$=extension.getElementItemAt("field", "lib");
			String jar$=entigrator.getEntihome()+"/"+extension$+"/"+lib$;
	
			  ZipFile zf = new ZipFile(jar$);
			    Enumeration<? extends ZipEntry> entries = zf.entries();
			    ZipEntry ze;
			    String[] sa;
			    while (entries.hasMoreElements()) {
			      try{
			    	ze = entries.nextElement();
			      sa=ze.getName().split("/");
			      if(resource$.equals(sa[sa.length-1])){
			    	  InputStream is=zf.getInputStream(ze);
			    	  if(is!=null)
			    		  return is;
	
			      }
			      }catch(Exception e){
			    	  
			      }
			    }
			return null;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	public static InputStream getResourceStream(String jar$,String resource$){
		try{
	
			  ZipFile zf = new ZipFile(jar$);
			    Enumeration<? extends ZipEntry> entries = zf.entries();
			    ZipEntry ze;
			    String[] sa;
			    while (entries.hasMoreElements()) {
			      try{
			    	ze = entries.nextElement();
			      sa=ze.getName().split("/");
	
			      if(resource$.equals(sa[sa.length-1])){
			    	  InputStream is=zf.getInputStream(ze);
			    	  if(is!=null)
			    		  return is;
	
			      }
			      }catch(Exception e){
			    	  
			      }
			    }
			    zf.close();
			return null;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	public static String[] listResourcesByType(String jar$,String type$){
		try{
			

            ArrayList<String>sl=new ArrayList<String>();		
			  ZipFile zf = new ZipFile(jar$);
			    Enumeration<? extends ZipEntry> entries = zf.entries();
			    ZipEntry ze;
			    String[] sa;
			    String resource$;
			    while (entries.hasMoreElements()) {
			      try{
			    	ze = entries.nextElement();
			      sa=ze.getName().split("/");
			      resource$=sa[sa.length-1];
	
			      if(type$.equals(FileExpert.getExtension(resource$))){
	                       sl.add(resource$);
			      }
			      }catch(Exception e){
			    	  
			      }
			    }
			return sl.toArray(new String[0]);
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "extension", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	 * Adapt the clone of the entity.  
	 */	
	@Override
	public void adaptClone(Entigrator entigrator) {
		adaptLabel( entigrator);
	}
	/**
     * Adapt the the entity after rename.   
     */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel( entigrator);
	}
	 /**
     * Get title of the extension handler.  
     * @return the title of the facet handler..
     */	
	@Override
	public String getTitle() {
		return "Extension";
	}
	 /**
     * Get type of the extension handler.  
     * @return the type of the facet handler..
     */	
	@Override
	public String getType() {
			return "extension";
	}
	 /**
     * Get class name of the extension handler.  
     * @return the class name of the facet handler..
     */	
	@Override
	public String getClassName() {
		return getClass().getName();
	}
//
	public static  void addExtensionLibraries(Entigrator entigrator,String extension$){
		try{
			if(debug)
			System.out.println("ExtensionHandler:addExtensionLibraries:extension="+extension$);
				Object obj=null;
				Sack extension=entigrator.getEntityAtKey(extension$);
				String lib$=extension.getElementItemAt("field", "lib");
				String jar$="jar:file:" +entigrator.getEntihome()+"/"+extension$+"/"+lib$+"!/";
				ArrayList <URL> urll=new ArrayList<URL>();
				urll.add(new URL(jar$));
				String[]sa=extension.elementListNoSorted("classpath");
				if(sa!=null){
					File file; 
					for(String s:sa){
						file=new File(entigrator.getEntihome()+"/"+extension$+"/"+s);
						if(file.exists())
							urll.add(new URL("jar:file:"+file.getPath()+"!/"));
					}
				}
				URL[] urls=urll.toArray(new URL[0]);
				URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
				
				ExtensionClassLoader extensionLoader = new ExtensionClassLoader(urlClassLoader);
				for(URL url:urls)
				    extensionLoader.addURL(url);
				 Thread.currentThread().setContextClassLoader(extensionLoader);
				
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
		}
	}
	public static class ExtensionClassLoader extends URLClassLoader{
		public ExtensionClassLoader(URLClassLoader classLoader) {
		    super(classLoader.getURLs());
		}
		@Override
		public void addURL(URL url) {
		    super.addURL(url);
		}

		}
	public static String getClasspath(Entigrator entigrator){
	try{
		String[] sa=entigrator.indx_listEntities("entity","extension");
		if(sa==null)
			return null;
		Sack extension;
		String lib$;
		String jar$;
		File  file; 
		StringBuffer sb=new StringBuffer();
		String[]ca;
		String separator$=System.getProperty("path.separator");
		if(sa!=null){
			for(String s:sa)
			{
				try{
				extension=entigrator.getEntityAtKey(s);
				lib$=extension.getElementItemAt("field", "lib");
				file=new File(entigrator.getEntihome()+"/"+s+"/"+lib$);
				if(file.exists())
					sb=sb.append(separator$+file.getPath());
				ca=extension.elementListNoSorted("classpath");
				if(ca!=null){
					 
					for(String c:ca){
						file=new File(entigrator.getEntihome()+"/"+s+"/"+s);
						if(file.exists())
							sb=sb.append(separator$+file.getPath());
					}
				}
				}catch(Exception ee){
					Logger.getLogger(ExtensionHandler.class.getName()).info(ee.toString());	
				}
			}
		}
		File dependencies=new File(entigrator.getEntihome()+"/"+Entigrator.DEPENDENCIES);
		if(dependencies.exists()&&dependencies.isDirectory()){
			String[] da=dependencies.list();
			if(da!=null)
				for(String d:da){
					if(d.endsWith(".jar"))
						sb=sb.append(separator$+entigrator.getEntihome()+"/"+Entigrator.DEPENDENCIES+"/"+d);
				}
			
		}
		return sb.toString();
	}catch(Exception e){
		Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());	
	}
	return null;
	}
	//
	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
}
