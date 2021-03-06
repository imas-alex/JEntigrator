package gdt.data.store;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.FileExpert;
/**
* This class is the main provider of database management methods.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/

public class Entigrator {
 final static boolean debug=false;
 /**
   * The name of the properties file. This XML file contains all property names
   * linked to property name/value maps.   
   */
	public static final String PROPERTY_INDEX="_OMw7Msp2wcy5tMEmoayqGzwIJE8";
	/**
	 * The name of the icons directory.
	 */
	public static final String ICONS="_juwhpT_txc_SIN45Mt48Tt3FIEOc";
	/**
	   * The name of the quick map file. This XML file contains the shortest
	   * information about entities. The usage of the quick map accelerates 
	   * access to entities. 	
	   */
	public static final String DEPENDENCIES="_34OGgNhynOBECkS_SlCJt_SNh_SY9Q";
	
	/**
	 * Reserved attribute name. The entities cache uses time stamp to keep
	 * recent entities in memory some time to accelerate the multiple
	 * sequential access to the same entity.  
	   */
	public static final String TIMESTAMP="timestamp";
	/**
	   * This directory contains property name/value maps.	
	   */
    public static final String PROPERTY_BASE="_mVr_SNS5qWHZiO9qPAlAOfTWrMZQ";
    /**
	   * This directory contains property value/entity maps.	
	   */
	public static final String PROPERTY_MAP="_A6nLHPcMALLI8jWAj4wPUuYOJOo";
	/**
	   * This directory contains entities files.	
	   */
	public static final String ENTITY_BASE="_xXsnV5R_SGxkuH_SpLJDeXCglybws";
	/**
	   * Reserved attribute. Entities having PRESERVED assigned cannot be 
	   * deleted.	
	   */
	private static final String PRESERVED="preserved";
	/**
	   * The parent directory of the database.
	   */
	public static final String ENTIHOME="entihome";
	private final int ENT_OK = 0;
    private final int WRONG_ARG = -1;
    private final int ENT_PROP_NOT_ASSIGNED = 1;
    private final int ENT_MULTIPLE_VALUES_OK = 2;
    private final int ENT_MULTIPLE_VALUES_BAD = 3;
    private final int INDX_NO_PROP_ENTRY = 5;
    private final int INDX_NO_PROP_SACK = 6;
    private final int INDX_NO_MAP_ENTRY = 7;
    private final int INDX_NO_MAP_SACK = 8;
    private final int INDX_NO_ENTITY_ENTRY = 9;
    private final int INDX_ENTITY_FALSE_MAP = 11;
    private final int INDX_OK = 12;
    private Sack propertyIndex;
    public EntitiesCache entitiesCache;
    private final Logger LOGGER= Logger.getLogger(Entigrator.class.getName()); 
   @SuppressWarnings("unused")
   private int progress;
   private String entihome$;
   public static final String LOCK_TIME="lock.time";
   public static final String LOCK_STORE="lock store";
   public static final String SAVE_ID="save id";
   private StoreAdapter storeAdapter;
   Hashtable <String,Object>handlers;
   Hashtable <String,Object>type2handler;
  // Hashtable <String,FileLock>fileLocks;
 
   /**
    * Constructor.
    * 
    * @param arguments string array with size at least 1.
    * arguments[0] must be the path of the parent directory
    * of the database. 
    */
   public Entigrator(String[] arguments) {
        if (arguments == null) {
            LOGGER.severe(": no arguments");
            return;
        }
        entihome$ = arguments[0];
        //System.out.println("Entigrator:constructor:entihome="+entihome$);
//        LOGGER.info("Entigrator:constructor:entihome="+entihome$);
        try {
            File propIndex = new File(entihome$ + "/" +PROPERTY_INDEX); 
              boolean badIndex = false;
            if (!propIndex.exists() || propIndex.length() < 10)
 
            {
  //          	 System.out.println("Entigrator:constructor:cannot find property index=="+propIndex.getPath());
                badIndex = true;
              }
            else {
                propertyIndex = Sack.parseXML(this,entihome$ + "/" + PROPERTY_INDEX);
                if (propertyIndex == null)
                    badIndex = true;
                else {
                    String[] sa = propertyIndex.elementListNoSorted("property");
                    if (sa == null )
                        badIndex = true;
                }
            }
            if (badIndex) {
                propertyIndex = new Sack();
                propertyIndex.putAttribute(new Core("String", "residence.base", "register"));
                propertyIndex.putAttribute(new Core("String", "alias", "property.index"));
                propertyIndex.createElement("property");
                propertyIndex.setKey(PROPERTY_INDEX);
                propertyIndex.setPath(PROPERTY_INDEX);
                propertyIndex.saveXML(getEntihome() + "/"+PROPERTY_INDEX);
                indx_reindex(null);
            }
            if (propertyIndex == null)
                propertyIndex = Sack.parseXML(this,entihome$ + "/" + PROPERTY_INDEX);
            if (propertyIndex == null) {
                //System.out.println("Entigrator:constructor:cannot get property index");
            	LOGGER.severe(":cannot get property index");
            }
        } catch (Exception e) {
            //System.out.println("Entigrator:constructor:cannot find property index:" + e.toString());
        	LOGGER.severe(":"+e.toString());
        }
        
        entitiesCache=new EntitiesCache(this);
        storeAdapter=new StoreAdapter(this);
    	handlers=new Hashtable<String,Object>();
    	type2handler=new Hashtable<String,Object>();
    //	fileLocks=new Hashtable<String,FileLock>();
    }
   
   /**
	 * Refresh an entity from cache or disk.
	 *  @param sack the entity.
	 * @return the entity.
	 */
    /**
  	 * Save an entity.
  	 *  @param sack the entity.
  	 * @return true if success false otherwise.
  	 */
    
    private synchronized boolean save(Sack sack){
    	try{
    	if (sack == null) {
    		if(debug)
    		System.out.println("Entigrator:save:sack is null");
            return false;
        }
    	// System.out.println("Entigrator:save:"+sack.getKey());
    	 String entityBase$ =ENTITY_BASE; 
      	 String base$ = sack.getAttributeAt("residence.base");
      	//System.out.println("Entigrator:save:base="+base$);
    	 if(entityBase$.equals(base$)){
    		 ent_alter(sack);
    	     return true;
    	 }else{
    		 //
    		 String path$=null;
    		 if(base$==null||"register".equals(base$))
    			  path$ =entihome$+"/" + sack.getKey();
    		 else
    		      path$ =entihome$+"/" + base$ + "/data/" +sack.getKey();
    		// System.out.println("Entigrator:save:path="+path$);
        	   sack.saveXML(path$);
        	   return true;
    	 }
    	}catch(Exception e){
    		LOGGER.severe(e.toString());
    		return false;
    	}
    }
    /**
  	 * Check entity file.
  	 *  @param entityKey$ key of the entity
  	 * @return true if entity file exists false otherwise.
  	 */
     private boolean touchEntity(String entityKey$) {
        try {
            String entihome$ = getEntihome();
            String memberPath$ = entihome$ + "/" + getBaseName() + "/data/" + entityKey$;
            File member = new File(memberPath$);
            return !(!member.exists() || member.length() < 10);
        } catch (Exception e) {
        	LOGGER.severe(":touchEntity:" + e.toString());
            return false;
        }
    }
     /**
   	 * Get entity by key.
   	 *  @param entityKey$ key of the entity
   	 * @return entity or null.
   	 */
    public synchronized Sack getEntityAtKey(String entityKey$) {
    	Sack entity= entitiesCache.get(entityKey$);
    	if(entity!=null)
    		return entity;
    	return storeAdapter.ent_reloadAtKey(entityKey$);
    }
    
    private synchronized Sack getBaseMember(String baseAlias$, String candidate$) {
    	if("entity.base".equals(baseAlias$))
        	return getEntityAtKey(candidate$);

    	String entihome$ =getEntihome();
    	String base$=null;
        if("entity.base".equals(baseAlias$))
        		base$ =ENTITY_BASE;
        if("property.base".equals(baseAlias$))
        	    base$=PROPERTY_BASE;
        if("property.map.base".equals(baseAlias$))
    	    base$=PROPERTY_MAP;
        if(base$==null){
        	LOGGER.severe(":getMember:cannot find base alias=" + baseAlias$);
        	return null;
        }
        String memberPath$ = entihome$ + "/" + base$ + "/data/" + candidate$;
        File member = new File(memberPath$);
        if (!member.exists()){
        //	LOGGER.severe(":getMember:cannot find member at path="+memberPath$); 
        	return null;
        }
        if (member.length() < 10) {
            try {
  //          	LOGGER.severe(":getMember:delete wrong member at path="+memberPath$); 
                member.delete();
            } catch (Exception e) {
            	LOGGER.severe(":getMember:"+e.toString());
            }
            return null;
        }
        try {
            return Sack.parseXML(this,memberPath$);
        } catch (Exception e) {
          	LOGGER.severe(":getMember:"+e.toString());
            return null;
        }
    }
    /**
   	 * List all property names.
   	 * 
   	 * @return array of property names.
   	 */
    public String[] indx_listPropertyNames() {
    	try{
    	String[] sa=propertyIndex.elementListNoSorted("property");
    	String[] sa1=new String[sa.length+1];
    	for(int i=0;i<sa.length;i++)
    		sa1[i]=sa[i];
    	sa1[sa.length]="label";
    	Support.sortStrings(sa1);
        return  sa1;
    	}catch(Exception e){
    		LOGGER.severe(e.toString());
    	}
    	return null;
    }
    /**
   	 * List all property values.
   	 *  @param propertyName$ the name of the property.
   	 * @return array of property values or null.
   	 */
    public String[] indx_listPropertyValues(String propertyName$) {
        if (propertyName$ == null) {
            LOGGER.severe(":indx_listPropertyValues:property name is null");
            return null;
        }
        if("label".equals(propertyName$)){
        	return indx_listAllLabels();
        }
        Core core = propertyIndex.getElementItem("property", propertyName$);
        if (core == null) {
            LOGGER.severe(":indx_listPropertyValues:cannot find property =" + propertyName$);
            return null;
        }
        Sack property = getBaseMember("property.base", core.value);
        if (property == null) {
        	LOGGER.severe(":indx_listPropertyValues:cannot find property  name=" + core.value);
        	return null;
        }
        return property.elementList("value");
    }

private Sack indx_getProperty(String propertyName$) {
        Core core = propertyIndex.getElementItem("property", propertyName$);
        if (core == null) {
            LOGGER.severe(":indx_getProperty:cannot get property key for property=" + propertyName$);
            return null;
        }
        return getBaseMember("property.base", core.value);
    }
/**
	 * Change property name.
	 *  @param propertyName$ old property name
	 *  @param newPropertyName$ new property name.
	 */
public void prp_editPropertyName(String propertyName$,String newPropertyName$) {
    Core core = propertyIndex.getElementItem("property", propertyName$);

    if (core == null) {
        LOGGER.severe(":indx_editProperty:cannot get property key for property=" + propertyName$);
        
    }
    propertyIndex.removeElementItem("property", propertyName$);
    propertyIndex.putElementItem("property", new Core(core.value,newPropertyName$,core.value));
    save(propertyIndex);
}
private Sack indx_getPropertyMap(String propertyName$, String propertyValue$) {
        if (propertyName$ == null) {
        	LOGGER.severe(":indx_getPropertyMap:argument is null");
        	return null;
        }
        Core core = propertyIndex.getElementItem("property", propertyName$);
        if (core == null) {
            String map$ = indx_addPropertyValue(propertyName$, propertyValue$);
            return getBaseMember("property.map.base", map$);
        }
        Sack property = getBaseMember("property.base", core.value);
        if (property == null) {
            String map$ = indx_addPropertyValue(propertyName$, propertyValue$);
            return getBaseMember("property.map.base", map$);
        }
        String map$ = property.getElementItemAt("value", propertyValue$);
        if (map$ == null) {
            map$ = indx_addPropertyValue(propertyName$, propertyValue$);
        }
        Sack map = getBaseMember("property.map.base", map$);
        if (map == null) {
            property.removeElementItem("value", propertyValue$);
            save(property);
            map$ = indx_addPropertyValue(propertyName$, propertyValue$);
        }
        return getBaseMember("property.map.base", map$);
    }
/**
 * List keys of entities having certain properties assigned.
 *  @param criteria  the properties object that contains a set
 *  of property name/value pairs.
 * @return string array of entities keys.
 */   
public String[] indx_listEntities(Properties criteria) {
        if (criteria == null)
            return null;
       Enumeration <?>en = criteria.keys();
        String propertyName$ ;
        String propertyValue$;
        String[] cur=null ;
        String[] sum=null;
        int i = 0;
        while (en.hasMoreElements()) {
            try {
                propertyName$ = (String) en.nextElement();
                propertyValue$ = criteria.getProperty(propertyName$);
                cur = indx_listEntities(propertyName$, propertyValue$);
                if (cur == null)
                    return null;
                if (i++ == 0)
                    sum = cur;
                else
                    sum = intersect(sum, cur);
                if (sum == null || sum.length < 1)
                    return null;
            } catch (Exception e) {
                    LOGGER.severe(":indx_listEntities:"+e.toString());
            }
        }
        return sum;
    }
/**
 * Get entity key by entity label
 *  @param label$ entity label
 * @return entity key.
 */ 
    public String indx_keyAtLabel(String label$) {
    	return storeAdapter.indx_keyAtLabel(label$);
        }
    /**
     * Get entity label by entity key
     *  @param key$ entity key
     * @return entity label.
     */ 
    public String indx_getLabel(String key$) {
    	
    	return storeAdapter.indx_getLabel(key$);
       }
    /**
     * Get entities labels by entities keys.
     *  @param keys array of keys
     * @return array of labels.
     */    
    public String[] indx_getLabels(String[] keys) {
    	return storeAdapter.indx_getLabels(keys);
    }
    /**
     * Get entity type by entity label.
     *  @param key$ entity key.
     * @return entity type.
     */   
    public String getEntityType(String key$){
    	return storeAdapter.getEntityType(key$);
    }
    /**
     * Get entity icon file by entity key.
     *  @param key$ entity key.
     * @return entity icon file name.
     */   
    public String ent_getIconAtKey(String key$){
    	return storeAdapter.ent_getIconAtKey(key$);
    
    }
    /**
     * Get entities icon files and labels by entities keys.
     *  @param keys array of entities keys.
     * @return array of cores where type is a name of icon file,
     * name is a key of the entity and value is a label of the entity.
     */  
    public Core[] indx_getMarks(String[] keys) {
    	return storeAdapter.indx_getMarks(keys) ;
  
         }
    /**
     * Get entities icon files and keys by entities labels.
     *  @param labels array of entities labels.
     * @return array of cores where type is a name of icon file,
     * name is a key of the entity and value is a label of the entity.
     */
    /**
     * Get labels of all entities.
     * @return array of labels
     */  
    
    public String[] indx_listAllLabels() {
    	return storeAdapter.indx_listAllLabels();
    }
    /**
     * Get keys of entities having certain property name/value assigned. 
     *  @param propertyName$ property name
     *  @param propertyValue$ propertyValue$.
     * @return array of entities keys.
     */  
    public  String[] indx_listEntities(String propertyName$, String propertyValue$) {
	        if (propertyName$ == null || propertyValue$ == null) {
	        	LOGGER.severe(":indx_listEntities:one or more null parameters");
	        	return null;
        }
        if ("label".equals(propertyName$)) {
            String key$ = indx_keyAtLabel(propertyValue$);
            if (key$ == null)
                return null;
            return new String[]{key$};
        }
        if("entity".equals(propertyName$)){
     	   try{
        	      return storeAdapter.indx_listEntitiesAtType(propertyValue$);
     	    }catch(Exception e){
     	    	 LOGGER.severe(e.toString()); 
 	 
     	    }
        }
        Core core = propertyIndex.getElementItem("property", propertyName$.trim());

        if (core == null) {
        	LOGGER.severe(":indx_listEntities:cannot find property entry =" + propertyName$ + " value=" + propertyValue$);
            return null;

        }
        Sack property = getBaseMember("property.base", core.value);
        if (property == null) {
            LOGGER.severe(":indx_listEntities:cannot find property=" + core.value + " found for property name=" + propertyName$ + " value=" + propertyValue$);
            return null;

        }
        String map$ = property.getElementItemAt("value", propertyValue$.trim());
        if (map$ == null) {
           	LOGGER.severe(":indx_listEntities:cannot find map at value=" + propertyValue$ + " in property=" + propertyName$);
        	return null;

        }
        Sack map = getBaseMember("property.map.base", map$);
        if (map == null) {
            LOGGER.severe(":indx_listEntities:cannot find map=" + map$ + " at value=" + propertyValue$ + " in property=" + propertyName$);
            return null;

        }
        String[] ea = map.elementList("entity");
        if (ea != null) {
        	ArrayList <String>el=new ArrayList<String>();
        	Sack candidate; 
        	boolean saveMap=false;
        	
        	for(String e:ea){
        		//System.out.println("Entigrator:indx_listEntities.e="+e);
        		candidate=getEntityAtKey(e);
        		if(candidate!=null){
        			el.add(e);
        		}else{
        			map.removeElementItem("entity", e);
        			saveMap=true;
        		}
        	}
        	if(saveMap)
        		this.save(map);
            return el.toArray(new String[0]);
        } else {
            LOGGER.severe(":indx_listEntities:empty map=" + map$ + " at value=" + propertyValue$ + " in property=" + propertyName$);
            return null;
        }
    }
    /**
     * Sort keys of entities by  labels. 
     *  @param keys input keys.
     * @return array of sorted keys.
     */ 
    public String[] indx_sortKeysAtlabel(String[] keys) {
    	return storeAdapter.indx_sortKeysAtlabel(keys);
    }
    /**
     * Get keys of entities having certain property name assigned. 
     *  @param propertyName$ property name.
     * @return array of entities keys.
     */ 
    public String[] indx_listEntitiesAtPropertyName(String propertyName$) {
       if("label".equals(propertyName$)){
        	   return storeAdapter.indx_listAllKeys();
       }
    	try {
            long begin=System.currentTimeMillis();
    		String property$ = propertyIndex.getElementItemAt("property", propertyName$);
    		 if(debug)
    		System.out.println("Entigrator:indx_listEntitiesAtPropertyName:get property name:"+String.valueOf(System.currentTimeMillis()-begin));
            if (property$ == null) {
           	 //LOGGER.severe(":indx_listEntitiesAtPropertyName:cannot find property in property index  property =" + propertyName$);
            	return null;
            }
            Sack property = getBaseMember("property.base", property$);
            if (property == null) {
            	LOGGER.severe(":indx_listEntitiesAtPropertyName:cannot find property =" + property$);
            	return null;
            }
            if(debug)
            System.out.println("Entigrator:indx_listEntitiesAtPropertyName:get property :"+String.valueOf(System.currentTimeMillis()-begin));
          //  Stack<String> s = new Stack<String>();
            Stack<String> s2 = new Stack<String>();
            
            String[] ma = property.elementList("value");
            if (ma == null) {
                	LOGGER.severe(":indx_listEntitiesAtPropertyName:no values in property =" + property$);
                	return null;
                }
            if(debug)
            System.out.println("Entigrator:indx_listEntitiesAtPropertyName:list maps :"+String.valueOf(System.currentTimeMillis()-begin));
            Sack map;
            ArrayList <String>sl=new ArrayList<String>();
            String[] ea ;
                for (int i = 0; i < ma.length; i++) {
                    begin=System.currentTimeMillis();
                	s2.clear();
                    map = getBaseMember("property.map.base", property.getElementItemAt("value", ma[i]));
                    if (map == null) {
                    	
                       	LOGGER.severe(":indx_listEntitiesAtPropertyName:cannot get map[" + i + "]=" + ma[i]+" property key="+property.getKey());
                    	property.removeElementItem("value", ma[i]);
                    	save(property);
                       	continue;
                    }
                    if(debug){
                    	System.out.println("Entigrator:indx_listEntitiesAtPropertyName:map="+map.getKey());	
                  //  System.out.println("Entigrator:indx_listEntitiesAtPropertyName:get map:"+String.valueOf(System.currentTimeMillis()-begin));
                    }
                    ea = map.elementList("entity");
                    if (ea == null) {
                    	LOGGER.severe(":indx_listEntitiesAtPropertyName:empty map[" + i + "]=" + ma[i]);
                    	property.removeElementItem("value", map.getKey());
                    	delete(map);
                    	continue;
                    }
                    if(debug)
                    System.out.println("Entigrator:indx_listEntitiesAtPropertyName:list entitites :"+String.valueOf(System.currentTimeMillis()-begin));
                    String label$;
                    for (String anEa : ea) {
                    	begin=System.currentTimeMillis();
                        if (!touchEntity(anEa)){
                        	label$=indx_getLabel(anEa);
                        	if(debug)
                        	System.out.println("Entigrator:indx_listEntitiesAtPropertyName:get label :"+String.valueOf(System.currentTimeMillis()-begin));
                        	if(label$==null||"null".equals(label$)){
                        		map.removeElementItem("entity", anEa);
                        		save(map);
                        		continue;
                        	}
                        	if(sl.contains(anEa))
                        		continue;
                        	sl.add(anEa);
                        		//s.push(anEa);
                        	 if(debug)
                               System.out.println("Entigrator:listEntitiesAtPropertyMame:entity key="+anEa+" label="+ indx_getLabel(anEa));
                        }
                    }
                }
          
                return sl.toArray(new String[0]);
        } catch (Exception e) {
        	LOGGER.severe(":indx_listEntitiesAtPropertyName:"+e.toString());
        	return null;
        }
    }
    /**
     * Remove broken property indexes. 
     */ 
    public void prp_deleteWrongEntries() {
        String[] sa = indx_listPropertyNames();
        if (sa != null)
            for (String aSa : sa) prp_deleteWrongPropertyEntries(aSa);
    }
    /**
     * Remove broken property values by property name. 
     * @param propertyName$ property name.
     */ 
    public void prp_deleteWrongPropertyEntries(String propertyName$) {
    	String[] sa = indx_listPropertyValues(propertyName$);
        if (sa != null){
            for (String aSa : sa) prp_deleteWrongValueEntries(propertyName$, aSa);
            sa = indx_listPropertyValues(propertyName$);
            if (sa == null)
            	prp_deletePropertyName(propertyName$);
            }
        else
        	prp_deletePropertyName(propertyName$);

    }
    
    private void prp_deleteWrongValueEntries(String propertyName$, String propertyValue$) {
        try {
        	if("label".equals(propertyName$)){
            		storeAdapter.indx_deleteWrongLabel(propertyValue$);
        		return;
        	}
        	if(debug)
            	System.out.println("Entigrator:prp_deleteWrongValueEntries:property name="+propertyName$+ " value="+propertyValue$);
        	
        	String property$ = propertyIndex.getElementItemAt("property", propertyName$);
            Sack property = getBaseMember("property.base", property$);
            Sack map = getBaseMember("property.map.base", property.getElementItemAt("value", propertyValue$));
            if(map==null){
            	prp_deletePropertyValue(propertyName$,propertyValue$);
            	return;
            }
            String[] ea = map.elementList("entity");
              Sack entity = null;
            String label$;  
            if (ea != null)
            	for (String anEa : ea) {
                    try {
                        if(debug)
                        	System.out.println("Entigrator:prp_deleteWrongValueEntries:anEa="+anEa);
                    	entity = getBaseMember("entity.base", anEa);
                    	 if (entity == null) {
                    		 if(debug)
                             	System.out.println("Entigrator:prp_deleteWrongValueEntries:remove from map anEa="+anEa);
                         
                             map.removeElementItem("entity", anEa);
                             save(map);
                             continue;
                         }
                    	label$=entity.getProperty("label");
                    	 if(debug)
                           	System.out.println("Entigrator:prp_deleteWrongValueEntries:check label="+label$);
                      
                    	if(label$==null||"null".equals(label$)){
                        	deleteEntity(entity);
                        }
                    } catch (Exception ee) {
                    	  if(debug)
                          	System.out.println("Entigrator:prp_deleteWrongValueEntries:"+ee.toString());
                   
                }
            
            }
             ea = map.elementList("entity");
            if(ea!=null)
            	return;
            else
            {
               prp_deletePropertyValue(propertyName$,propertyValue$);
               delete(map);
            }
//             save(map);
        } catch (Exception e) {
           
        	LOGGER.severe(":prp_deleteWrongValueEntries:"+e.toString());
        }
    }
    /**
     * Take off the property from the entity. 
     *  @param entity the entity
     *  @param propertyName$ the property name.
     * @return the entity.
     */   
    public Sack ent_takeOffProperty(Sack entity, String propertyName$) {
        if (entity == null) {
            LOGGER.severe(":ent_takeOffProperty:entity is null");
            return null;
        }
        if ("label".equals(propertyName$)) {
            //LOGGER.severe(":ent_takeOffProperty:cannot delete label");
        	
            return entity;
        }
        if (propertyName$ == null) {
        	LOGGER.severe(":ent_takeOffProperty:property name is null");
        	return entity;
        }
        //check entity
        Core[] ca = entity.elementGet("property");
        String map$ = null;
        String value$ = null;
        boolean modified = false;
        if (ca == null) {
        	LOGGER.severe(":ent_takeOffProperty:no properties in entity=" + entity.getKey());
        	return entity;
        } else {
            for (Core aCa : ca) {
                if (propertyName$.equals(aCa.type)){
                    entity.removeElementItem("property", aCa.name);
                    modified = true;
                    map$ = aCa.name;
                    value$ = aCa.value;
                }
            }

            if (!modified)
                return entity;
        }
        Sack map = getBaseMember("property.map.base", map$);
        if (map == null)
            return entity;
        map.removeElementItem("entity", entity.getKey());
        String[] ea = map.elementListNoSorted("entity");
        if (ea != null && ea.length > 0) {
            save(map);
            return entity;
        }

        //empty value;
        String property$ = map.getAttributeAt("property");
        if (property$ == null) {
        	LOGGER.severe(":ent_takeOffProperty:cannot get property=" + propertyName$ + ": cannot get property key for map=" + map$ + " entity=" + entity.getProperty("label"));
        	return entity;
        }
        Sack property = getBaseMember("property.base", property$);
        if (property == null) {
            LOGGER.severe(":ent_takeOffProperty: property=" + propertyName$ + ": cannot get property at key=" + property$);
            return entity;
        }
        property.removeElementItem("value", value$);
        delete(map);
        Core[] ma = property.elementGet("value");
        if (ma != null && ma.length > 0) {
            save(property);
            return entity;
        }
        propertyIndex.removeElementItem("property", propertyName$);
        save(propertyIndex);
        return entity;
    }
    /**
     * Rebuild index.
     *  @param indicator instance of Indicator class
     *  intended to inform caller about progress. Can be null.
     */  
public void indx_reindex(Indicator indicator) {
	    clr_all();
        progress = 0;
        storeAdapter.qmRebuildAll();
        String[] sa = indx_listEntities();
        if (sa == null)
             return;
        Sack candidate ;
        for (String aSa : sa) {
            if (indicator != null) {
                indicator.run();
            }
    
            candidate = storeAdapter.ent_reloadAtKey(aSa); 
            if(candidate==null){
            if(debug)
            	System.out.println("Entigrator:indx_reindex:cannot get aSa="+aSa);
                continue;
            }
            ent_reindex(candidate);
        }
    
    }

public void indx_remap() {
	storeAdapter.qmBuild();
}
/**
 * Rebuild  index entries for the entity. 
 *  @param entity the entity.
 *  @return the entity.
 */
class EntReassignProperties implements Runnable{
    public Sack entity; 
	public EntReassignProperties(Sack entity){
		this.entity=entity;
	}
    public void run(){
		        if (entity == null) 
		            return ;
		        Core[] ca = entity.elementGet("property");
		        if (ca == null || ca.length < 1) {
		            deleteEntity(entity);
		            return ;
		        }
		        String key$=null;
		        String label$=entity.getProperty("label");
		        if(entity.getProperty("label")==null)
		        	label$=entity.getKey();
		        key$=indx_keyAtLabel(label$);
		        if(key$!=null&&(!entity.getKey().equals(key$)))
		        	      label$=label$+entity.getKey().substring(0, 4);
		       entity=ent_assignLabel(entity, label$);
		        for (Core aCa : ca) 
		        	  if (aCa.type != null && aCa.value != null&&!"label".equals(aCa.type)) 
		            	entity = ent_assignProperty(entity, aCa.type, aCa.value);
	}
};
class  EntReplace   implements Runnable{
	Sack entity;
	public EntReplace(Sack entity){
		this.entity=entity;
	}
	public void run(){
			try{
				if(debug)
				System.out.println("StoreAdapter:EntReplace");
				storeAdapter.entReplace(entity);
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
}
class EntReindex implements Runnable {
	Sack entity;
	public EntReindex (Sack entity){
		this.entity=entity;
	}
	
	public void run(){
		 ExecutorService executor = Executors.newFixedThreadPool(2);
		 EntReassignProperties reassignProperties=new EntReassignProperties(entity);
		 
		 try{
			 executor.execute(reassignProperties);
		 if( executor.awaitTermination(1L, TimeUnit.MILLISECONDS))
			 executor.execute(new EntReplace(entity));
		 if( executor.awaitTermination(1L, TimeUnit.MILLISECONDS)){
		 entity=col_clearComponents(entity);
	        entity=col_updateContainers(entity);
		 }
	}catch(Exception e){
		LOGGER.severe(e.toString());
		}
}
	}
class  ReleaseScope   implements Runnable{
	String[] sa;
	public ReleaseScope(String[] sa){
		this.sa=sa;
	}
	public void run(){
			try{
			if(debug)
				System.out.println("Entigrator:ReleaseScope:sa="+sa.length);
				if(sa!=null){
					ArrayList <String>sl=new ArrayList<String>();
					for(String s:sa){
						if(ent_existsAtKey(s))
						 if(storeAdapter.entIsBusy(s)){
							 sl.add(s);
						}
					}
					String[] ea=sl.toArray(new String[0]);
					if(ea.length<1){
						store_release();
						return;
					}
					Thread.sleep(100);
					store_releaseScope(ea);
				}
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
}
public void store_releaseScope(String [] sa){
	try{
		ReleaseScope releaseScope=new ReleaseScope(sa);
		 Thread release=new Thread(releaseScope);
		 release.start();
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
}
public Sack ent_reindex(Sack entity) {
    
	progress++;
    
        if (entity == null) {
            LOGGER.severe(":ent_reindex:argument is null");
            return null;
        }
        if(debug)
        System.out.println("Entigrator:ent_reindex:entity="+entity.getProperty("label"));
        Core[] ca = entity.elementGet("property");
        if (ca == null || ca.length < 1) {
            deleteEntity(entity);
            LOGGER.severe(":ent_reindex:no properties in entity=" + entity.getKey());
            return null;
        }
  //      System.out.println("-reindex 1 ---------------------");
  //      entity.print();
        String label$=null;
        String key$=null;
    
        for (Core aCa : ca) {
        	  if(debug)
        	        System.out.println("Entigrator:ent_reindex:assign property="+aCa.value);
        	  if (aCa.type != null && aCa.value != null&&!"label".equals(aCa.type)) {
            	entity = ent_assignProperty(entity, aCa.type, aCa.value);
            }
            if ("label".equals(aCa.type)){
            	label$=aCa.value;
            	//entity.removeElementItem("property", aCa.name);
            	
            }
            if(debug)
    	        System.out.println("Entigrator:ent_reindex:finish assign property="+aCa.value);
        }
        if(debug)
	        System.out.println("Entigrator:ent_reindex:label="+label$);
       
        if(label$==null)
        	label$=entity.getKey();
       
        key$=indx_keyAtLabel(label$);
        if(key$!=null&&(!entity.getKey().equals(key$))){
        	      label$=label$+entity.getKey().substring(0, 4);
        }
        
       entity=ent_assignLabel(entity, label$);
    
       if(debug)
	        System.out.println("Entigrator:update header"); 
        entity=col_clearComponents(entity);
        entity=col_updateContainers(entity);
        ent_alter(entity);
        return entity;
    }

private boolean ent_propertyAlreadyAssigned(Sack entity, String propertyName$, String propertyValue$) {
        if (entity == null || propertyName$ == null || propertyValue$ == null) {
        	//LOGGER.info(":ent_propertyAlreadyAssigned:null argument");
        	return false;
        }
        if( propertyIndex.getElementItem("property", propertyName$)==null){
        	indx_addPropertyName(propertyName$);
        	 // LOGGER.info(":ent_propertyAlreadyAssigned:not assigned yet");
        	return false;
        }
        
        if (!propertyValue$.equals(entity.getProperty(propertyName$))) {
        	 //LOGGER.info(":ent_propertyAlreadyAssigned:not assigned yet");
        	return false;
        }
        String[] sa = indx_listEntities(propertyName$, propertyValue$);
        if (sa == null||sa.length<1){
        	//LOGGER.info(":ent_propertyAlreadyAssigned:not assigned yet");
            return false;
        }
        Sack propertyMap=indx_getPropertyMap(propertyName$,propertyValue$);
        if(propertyMap==null){
        	LOGGER.severe(":ent_propertyAlreadyAssigned:cannot find property map property name="+propertyName$+" value="+propertyValue$);
        	return false;
        }
        for(String aSa:sa)
        	if(!ent_existsAtKey(aSa))
             		propertyMap.removeElementItem("entity", aSa);
        save(propertyMap);
         sa=propertyMap.elementList("entity");
        if(sa==null){
        	//LOGGER.info(":ent_propertyAlreadyAssigned:not assigned yet"); 
        	return false;
        }
        String key$ = entity.getKey();
        //entity.print();
        
        for (String aSa : sa)
            if (key$.equals(aSa)){
            	Core[]ca=entity.elementGet("property");
            	if(ca!=null)
            		for(Core c:ca){
            			//System.out.println("Entigrator::ent_propertyAlreadyAssigned:p="+c.type+" v="+c.value);
            			if(propertyName$.equals(c.type)&& propertyValue$.equals(c.value)){
     //       	System.out.println("Entigrator::ent_propertyAlreadyAssigned:already assigned:entity="+entity.getProperty("label")+" property="+propertyName$+" value="+propertyValue$+" key="+entity.getKey());
                	             return true;
            			}
            		}
            }
        //LOGGER.info(":ent_propertyAlreadyAssigned:not assigned yet"); 
        return false;
    }
/**
 * Assign property to the entity. 
 *  @param entity the entity
 *  @param propertyName$ property name
 *  @param propertyValue$ property value.
 *  @return the entity.
 */ 
    public Sack ent_assignProperty(Sack entity, String propertyName$, String propertyValue$) {
    	//System.out.println("Entigrator:assignProperty:entity="+entity.getProperty("label"));	
    	if (entity == null || propertyName$ == null || propertyValue$ == null) {
    		LOGGER.severe(":ent_assignProperty:null argument");
    		return entity;
        }
    	//System.out.println("Entigrator:ent_assignProperty: entity="+entity.getProperty("label")+" property="+propertyName$+" value="+propertyValue$);
         if (ent_propertyAlreadyAssigned(entity, propertyName$, propertyValue$)){
        	// System.out.println("Entigrator:ent_assignProperty:already assigned");
            return entity;
        }
        if("label".equals(propertyName$)){
        	return ent_assignLabel(entity,propertyValue$);
        }
        if (entity.getProperty(propertyName$) != null){
        	//System.out.println("Entigrator:assignProperty:take off:entity="+entity.getProperty("label"));	
            entity = ent_takeOffProperty(entity, propertyName$);
        }
        int entStatus = prp_detectAtEntity(entity, propertyName$, propertyValue$);
        int indxStatus = prp_detectAtIndex(entity, propertyName$, propertyValue$);
        //System.out.println("Entigrator:ent_assignProperty:ent_assignProperty:property="+propertyName$+" value="+propertyValue$+"  entity="+entity.getKey()+", status  entity="+entStatus+" index="+indxStatus);
        switch (entStatus) {
            case ENT_OK:
                switch (indxStatus) {
                    case INDX_OK:
                        return entity;
                    case INDX_NO_ENTITY_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                            return entity;
                    case INDX_NO_MAP_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                            return entity;
                    case INDX_NO_MAP_SACK:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_PROP_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_PROP_SACK:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        return entity;
    
                    case INDX_ENTITY_FALSE_MAP:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        
                }
            case ENT_MULTIPLE_VALUES_OK:
                switch (indxStatus) {
                    case INDX_OK:
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_ENTITY_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_MAP_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_MAP_SACK:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_PROP_ENTRY:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_PROP_SACK:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_ENTITY_FALSE_MAP:
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                }
            case ENT_MULTIPLE_VALUES_BAD:
                switch (indxStatus) {
                    case INDX_OK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_ENTITY_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_MAP_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_MAP_SACK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_PROP_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_PROP_SACK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_ENTITY_FALSE_MAP:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                }
            case ENT_PROP_NOT_ASSIGNED:
                switch (indxStatus) {
                    case INDX_OK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        entity=ent_deleteWrongProperties(entity);
    
                        return entity;
                    case INDX_NO_ENTITY_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_MAP_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_MAP_SACK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_PROP_ENTRY:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_NO_PROP_SACK:
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                    case INDX_ENTITY_FALSE_MAP:
                    	
                        entity=ent_assignPropertyEntry(entity, propertyName$, propertyValue$);
                        ent_assignMapEntry(entity, propertyName$, propertyValue$);
    
                        return entity;
                }
        }
    
        return entity;
    }
    /**
     * Clone an entity. 
     *  @param template the original entity.
     *  @return the clone entity.
     */ 
    public Sack ent_clone(Sack template) {
    	if (template == null)
            return null;
    	return ent_clone(template,template.getProperty("label"));
    }
    /**
     * Clone an entity. 
     *  @param template the original entity
     *  @param label$ the label of the clone entity.
     *  @return the clone entity.
     */ 
    public Sack ent_clone(Sack template,String label$) {
        if (template == null)
            return null;
        Sack entity = new Sack();
        entity.putAttribute(new Core(null,"residence.base",Entigrator.ENTITY_BASE));
        entity.putAttribute(new Core(null,"template",template.getKey()));
        if(indx_keyAtLabel(label$)!=null)
        	label$=label$+Identity.key().substring(0, 4);
        Core core=template.getAttribute("icon");
        if(core!=null)
        	entity.putAttribute(core);
        core=new Core(null,"alias",label$);
        entity.putAttribute(core);
        Core[] ca;
        String[] ea = template.elementsListNoSorted();
        if (ea != null) {
            for (int i = 0; i < ea.length; i++) {
                try {
                    entity.createElement(ea[i]);
                    ca = template.elementGet(ea[i]);
                    entity.elementReplace(ea[i], ca);
                    
                } catch (Exception e) {
                   // System.out.println("Entigrator:ent_clone:ea[" + i + "]=" + ea[i] + ":" + e.toString());
                	LOGGER.severe(":ent_clone:ea[" + i + "]=" + ea[i] + ":" + e.toString());
                }
            }
        }
       // System.out.println("Entigrator:ent_clone:clone");
       // entity.print();
          ent_alter(entity);
          ca=entity.elementGet("property");
        for(Core aCa:ca){
        	if("template".equals(aCa.type)){
          		continue;
        	}
        	if("label".equals(aCa.type))
        	  entity=ent_assignLabel(entity, label$);
        	else
        	  entity=ent_assignProperty(entity, aCa.type, aCa.value);	
        }
        ent_alter(entity);
        return entity;
    }

    private Sack ent_deleteWrongProperties(Sack entity) {
        if (entity == null) {
        	LOGGER.severe(":ent_deleteWrongProperties:null argument");
        	return null;
        }
        Core[] ca = entity.elementGet("wrong.props");
        if (ca == null)
            return entity;
        Sack map ;
        for (Core aCa : ca) {
            map = indx_getPropertyMap(aCa.type, aCa.value);
            if (map == null)
                continue;
            map.removeElementItem("entity", entity.getKey());
            save(map);
            entity.removeElementItem("wrong.props", aCa.name);
        }
        return entity;
       
    }
private Sack ent_assignPropertyEntry(Sack entity, String propertyName$, String propertyValue$) {
       
	if (entity == null || propertyName$ == null || propertyValue$ == null) {
         	LOGGER.severe(":ent_assignPropertyEntry:null argument");
        	return entity;
        }
	//System.out.println("Entigrator:ent_assignPropertyEntry.entity="+entity.getProperty("label")+" property name="+propertyName$+" value="+propertyValue$);
         if ("label".equals(propertyName$))
            entity.putAttribute(new Core("String", "alias", propertyValue$));
        Sack map = indx_getPropertyMap(propertyName$, propertyValue$);
        if (map == null) {
        	LOGGER.severe(":ent_assignPropertyEntry:cannot get map at property=" + propertyName$ + " value=" + propertyValue$);
        	return entity;
        }
        //System.out.println("Entigrator:ent_assignPropertyEntry: map="+map.getKey());
        entity.putElementItem("property", new Core(propertyName$, map.getKey(), propertyValue$));
        entity.putAttribute(new Core(null,SAVE_ID,Identity.key()));
    
        return entity;
    }
/**
 * Assign icon file to the entity. 
 *  @param entity the entity
 *  @param icon$ the file name.
 *  @return the entity.
 */ 
public Sack ent_assignIcon(Sack entity, String icon$) {
	
	try{
		entity.putAttribute(new Core(null,"icon",icon$));
	    ent_alter(entity);    
	}catch(Exception e){
    	LOGGER.severe(":ent_assignIcon:"+e.toString());
	}
	return entity;
}
/**
 * Assign a label to the entity. 
 *  @param entity the entity
 *  @param label$ the label.
 * @return the entity.
 */ 

public Sack ent_assignLabel(Sack entity, String label$) {
    return storeAdapter.ent_assignLabel(entity, label$)	;
	
    }
private void ent_assignMapEntry(Sack entity, String propertyName$, String propertyValue$) {

        if (entity == null || propertyName$ == null || propertyValue$ == null) {
          	LOGGER.severe(":ent_assignMapEntry:null argument");
        	return;
        }

        if ("label".equals(propertyName$)) {
            ent_assignLabel(entity, propertyValue$);
            return;
        }
        Sack map = indx_getPropertyMap(propertyName$, propertyValue$);
        if (map == null) {
            LOGGER.severe(":ent_assignMapEntry:cannot get map at property=" + propertyName$ + " value=" + propertyValue$);
            return;
        }
        map.putElementItem("entity", new Core(entity.getProperty("entity"), entity.getKey(), entity.getProperty("label")));
        save(map);
    }

private int prp_detectAtIndex(Sack entity, String propertyName$, String propertyValue$) {
        int ret = WRONG_ARG;
        if (entity == null || propertyName$ == null || propertyValue$ == null)
            return ret;
        String property$ = propertyIndex.getElementItemAt("property", propertyName$);
        if (property$ == null)
            return INDX_NO_PROP_ENTRY;
        Sack property = getBaseMember("property.base", property$);
        if (property == null)
            return INDX_NO_PROP_SACK;
        String map$ = property.getElementItemAt("value", propertyValue$);
        if (map$ == null)
            return INDX_NO_MAP_ENTRY;
        Sack map = getBaseMember("property.map.base", map$);
        if (map == null)
            return INDX_NO_MAP_SACK;
        Core core = map.getElementItem("entity", entity.getKey());
        if (core == null)
            return INDX_NO_ENTITY_ENTRY;
        int INDX_FALSE_ENTITY_ENTRY = 10;
        if ("false".equals(core.value))
            return INDX_FALSE_ENTITY_ENTRY;
        core = entity.getElementItem("property", map$);
        if (core == null){
        	 if(debug)
        	System.out.println("Entigrator:prp_detectAtIndex:entity="+entity.getProperty("label")+" property name="+propertyName$+" value="+propertyValue$+" map="+map$+ " status="+INDX_ENTITY_FALSE_MAP);
            return INDX_ENTITY_FALSE_MAP;
        }
        return INDX_OK;
    }
private int prp_detectAtEntity(Sack entity, String propertyName$, String propertyValue$) {
        int ret = WRONG_ARG;
        if (entity == null || propertyName$ == null || propertyValue$ == null)
            return ret;
        String[] pa = entity.listItemsAtType("property", propertyName$);
        if (pa == null)
            return ENT_PROP_NOT_ASSIGNED;

        Core core ;

        for (String aPa : pa) {
            core = entity.getElementItem("property", aPa);
            if (propertyValue$.equals(core.value)) {
                if ("label".equals(propertyName$)) {
                    entity.putAttribute(new Core("String", "alias", propertyValue$));
                    ent_alter(entity);
                }
                if (pa.length == 1) {
                    if (!entity.existsElement("wrong.props"))
                        return ENT_OK;
                    if (entity.elementListNoSorted("wrong.props") == null
                            || entity.elementListNoSorted("wrong.props").length < 1)
                        return ENT_OK;
                    ret = ENT_MULTIPLE_VALUES_OK;

                }
            } else {
                if (!entity.existsElement("wrong.props"))
                    entity.createElement("wrong.props");
                entity.putElementItem("wrong.props", core);
                if (ret != ENT_MULTIPLE_VALUES_OK)
                    ret = ENT_MULTIPLE_VALUES_BAD;
            }
        }
        if (entity.existsElement("wrong.props")) {
  
            String[] sa = entity.elementListNoSorted("wrong.props");
            if (sa != null)
                for (String aSa : sa) {
                    entity.removeElementItem("property", aSa);
                }
            save(entity);
        }

        return ret;
    }
/**
 * Remove broken component links
 *  @param container the entity.
 * @return the entity.
 */ 

public Sack col_clearComponents(Sack container){
	try{
		Core[] ca=container.elementGet("component");
		if(ca==null)
			return container;
		ArrayList<Core>cl=new ArrayList<Core>(); 
		for(Core aCa:ca){
			if(indx_getLabel(aCa.value)!=null)
				cl.add(aCa);
			else{
				if(ent_existsAtKey(aCa.value))
					cl.add(aCa);
			}
		}
		ca=cl.toArray(new Core[0]);
		container.elementReplace("component", ca);
		return container;
	}catch(Exception e){
		LOGGER.severe(":col_clearComponents"+e.toString());
		return container;
        
	}
}
/**
 * Rebuild component - container links.
 *  @param component the entity.
 * @return the entity.
 */ 
private Sack col_updateContainers(Sack component){
	try{
		Core[] ca=component.elementGet("container");
		if(ca==null)
			return component;
		Sack container;
		String componentLabel$=component.getProperty("label");
		String componentKey$=component.getKey();
	
		for(Core aCa:ca){
				container=getEntityAtKey(aCa.value);
				if(container!=null){
					container.putElementItem("component", new Core(componentLabel$,aCa.name,componentKey$));
					ent_alter(container);
				}else{
					component.removeElementItem("container", aCa.name);
	
				}
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
		
        
	}
	return component;
}
/**
 * Add component to the entity
 *  @param container the container entity
 *  @param component the component entity.
 * @return the key of the link record.
 */ 
public String col_addComponent(Sack container, Sack component) {

        if (container == null) {
        	LOGGER.severe(":col_addComponent:container is null");
            return null;
        }
        col_clearComponents( container);
        if (component == null) {
        //	LOGGER.severe(":col_addComponent:component is null");
            return null;
        }

        if (container.getKey().equals(component.getKey())) {
        //	LOGGER.severe(":col_addComponent:component cannot be equal container");
        	return null;
        }
        if (col_existsRelation(container, component)) {
            //System.out.println("Entigrator:col_addComponent:already exists");
        	//LOGGER.info(":col_addComponent:already component");
        	 Core[] ca = component.elementGet("container");
             if (ca != null)
                 for (Core aCa : ca)
                     if (container.getKey().equals(aCa.value))
                        return aCa.name;
        		}
        if (!component.existsElement("container"))
            component.createElement("container");
        if (!container.existsElement("component"))
            container.createElement("component");
        if (!container.existsElement("component.type"))
            container.createElement("component.type");
        String key$ = gdt.data.grain.Identity.key();
        Core componentRecord = new Core(component.getProperty("label"), key$, component.getKey());
        container.putElementItem("component", componentRecord);
        Core containerRecord = new Core(container.getProperty("label"), key$, container.getKey());
        component.putElementItem("container", containerRecord);
        container.putElementItem("component.type", new Core(component.getProperty("entity"), key$, component.getProperty("component")));
       if(component.existsElement("ensemble"))
    	   component.removeElement("ensemble");
        ent_alter(component);
        if(container.existsElement("ensemble"))
     	   container.removeElement("ensemble");
       ent_alter(container);
        return key$;
    }

private boolean col_existsComponent(Sack container, Sack component) {
        if (container == null)
            return false;
        if (component == null)
            return false;
        Core[] ca = container.elementGet("component");
        if (ca == null)
            return false;
        for (Core aCa : ca)
            if (aCa.type != null)
    
                if (component.getKey().equals(aCa.value))
                    return true;
        return false;
    }
private boolean col_existsContainer(Sack container, Sack component) {
        if (container == null)
            return false;
        if (component == null)
            return false;
       Core[] ca = component.elementGet("container");
        if (ca == null)
            return false;
        for (Core aCa : ca)
            if (aCa.type != null)
               if (container.getKey().equals(aCa.value))
                    return true;
        return false;
    }
    
    /**
     * Check if the entity is a component of the container entity
     *  or its components.
     *  @param container the container entity
     *  @param component the component entity.
     * @return true if the component entity is really a component of one container
     * , false otherwise.
     */ 
    public boolean col_isComponentDown(Sack container, Sack component) {
        if (container == null)
            return false;
        if (component == null)
            return false;
        Core[] ca = container.elementGet("component");
        Sack candidate ;
        if (ca != null)
            for (Core aCa : ca) {
                if (component.getKey().equals(aCa.value))
                    return true;
                else {
                    candidate = getBaseMember("entity.base", aCa.value);
                    if (candidate != null) {
                        // System.out.println("Entigrator:col_isComponent: container="+container.getProperty("label")+" component="+component.getProperty("label")+" candidate="+candidate.getProperty("label"));   
                        if (col_isComponentDown(candidate, component))
                            return true;
                    }
                }
            }
        return false;
    }
    /**
     * Add property name into the index
     *  @param propertyName$ the property name
     * @return the property name map.
     */ 
    public Sack indx_addPropertyName(String propertyName$) {

        if (propertyName$ == null) {
        	LOGGER.severe(":indx_addPropertyName:property name is null");
            return null;
        }
    if("label".equals(propertyName$)){
    	LOGGER.severe(":indx_addPropertyName:property name is 'label'");
    	return null;
    }
        if (propertyIndex.getElementItemAt("property", propertyName$) != null) {
            String property$ = propertyIndex.getElementItemAt("property", propertyName$);
            Sack property = getBaseMember("property.base", property$);
            if (property != null)
                return property;
            else {
                propertyIndex.removeElementItem("property", propertyName$);
            }
        }

        Sack property = new Sack();
        property.putAttribute(new Core("key", "residence.base", PROPERTY_BASE));
        property.putAttribute(new Core("String", "alias", "property." + propertyName$));
        property.putAttribute(new Core("key", "property.name", propertyName$));
        property.createElement("value");
        propertyIndex.putElementItem("property", new Core("key", propertyName$, property.getKey()));
        save(property);
        save(propertyIndex);
       // System.out.println("Entigrator:indx_addPropertyName:FINISH:property name="+propertyName$);
        return property;
    }

    private void clr_maps() {
        try {
            String mapBase$ =entihome$+ "/" + PROPERTY_MAP + "/data";
            String propertyBase$ =entihome$+ "/" + PROPERTY_BASE + "/data";
            File mapBase = new File(mapBase$);
            String[] sa = mapBase.list();
            if (sa == null || sa.length < 1)
                return;
            Sack map;
            String property$;
            File propFile;

            for (String aSa : sa) {
                map = Sack.parseXML(this,mapBase$ + "/" + aSa);
                if (map == null)
                    try {
                        new File(mapBase$ + "/" + aSa).delete();
                        continue;
                    } catch (Exception ee) {
                      	LOGGER.severe(":clr_maps:file=" + aSa + ":" + ee.toString());
                    }
                property$ = map.getAttributeAt("property");
                if (property$ == null) {
                    try {
                        new File(mapBase$ + "/" + aSa).delete();
                        LOGGER.severe(":clr_maps:wrong property:delete map=" + aSa);
                        continue;
                    } catch (Exception ee) {
                    	  LOGGER.severe(":clr_maps:file=" + aSa + ":" + ee.toString());
                    }
                }
                try {
                    propFile = new File(propertyBase$ + "/" + property$);
                    if (propFile.exists())
                        continue;
                } catch (Exception ee) {
                	LOGGER.severe(":clr_maps:file=" + aSa + ":" + ee.toString());
                }
                try {
                    new File(mapBase$ + "/" + aSa).delete();
                    
                } catch (Exception ee) {
                     LOGGER.severe(":clr_maps:file=" + aSa + ":" + ee.toString());
                }
            }
        } catch (Exception e) {
       	  LOGGER.severe(":clr_maps:"+e.toString());
        }
    }

    private void clr_properties() {
        try {
            if (propertyIndex == null) {
            	LOGGER.severe(":clr_properties:property index is null");
            	return;
            }
           String propertyBase$ = entihome$+ "/" + PROPERTY_BASE + "/data";
           File propertyBase = new File(propertyBase$);
            String[] sa = propertyBase.list();
            if (sa == null || sa.length < 1)
                return;

            Sack property;
            String property$;
            for (String aSa : sa) {
                property = Sack.parseXML(this,propertyBase$ + "/" + aSa);
                if (property == null)
                    try {
                        new File(propertyBase$ + "/" + aSa).delete();
                       
                        continue;
                    } catch (Exception ee) {
                        LOGGER.severe(":clr_properties:file=" + aSa + ":" + ee.toString());
                    }
                property$ = property.getAttributeAt("property.name");
                if (property$ == null) {
                    try {
                        new File(propertyBase$ + "/" + aSa).delete();
                        continue;
                    } catch (Exception ee) {
                    	 LOGGER.severe(":clr_properties:file=" + aSa + ":" + ee.toString());
                    }
                }
                String key$ = propertyIndex.getElementItemAt("property", property$);
                if (key$ == null || !key$.equals(property.getKey()))
                    try {
                        new File(propertyBase$ + "/" + aSa).delete();
                    } catch (Exception ee) {
                    	 LOGGER.severe(":clr_properties:file=" + aSa + ":" + ee.toString());
                    }

            }
        } catch (Exception e) {
        	 LOGGER.severe(":clr_properties:"+e.toString());
        }
    }

   private void clr_all() {
        clr_index();
        clr_properties();
        clr_maps();
    }
  private void clr_index() {
        if (propertyIndex == null)
            return;
        Core[] ca = propertyIndex.elementGet("property");
        if (ca == null || ca.length < 1)
            return;
        Sack property = null;
        for (Core aCa : ca) {
            try {
                property = getBaseMember("property.base", aCa.value);
            } catch (Exception ee) {
            	LOGGER.severe(":clr_index:"+ee.toString());
            }
            if (property == null)
                propertyIndex.removeElementItem("property", aCa.name);
        }
        try {
            propertyIndex.saveXML(getEntihome() + "/_OMw7Msp2wcy5tMEmoayqGzwIJE8");
        } catch (Exception e) {
        	LOGGER.severe(":clr_index:"+e.toString());
        }
    }
  /**
   * Add property value into the index
   *  @param propertyName$ the property name
   *  @param propertyValue$ the property value
   * @return the key of the property value map.
   */ 
    public String indx_addPropertyValue(String propertyName$, String propertyValue$) {
        if("label".equals(propertyName$)){
        	LOGGER.severe(":indx_addPropertyValue:cannot add property='label'");
        	return null;
        }
    	if (propertyName$ == null || propertyValue$ == null){
        	LOGGER.severe(":indx_addPropertyValue:property name or value is null");
            return null;
    	}
        Sack property = null;
        if (propertyIndex.getElementItemAt("property", propertyName$) != null)
            property = getBaseMember("property.base", propertyIndex.getElementItemAt("property", propertyName$));
        String map$ =null;
        if (property == null) {

            propertyIndex.removeElementItem("property", propertyName$);
            property = indx_addPropertyName(propertyName$);
            if (property == null) {
            	LOGGER.severe(":indx_addPropertyValue:cannot find/create property=" + propertyName$);
                return null;
            }
        } else {
            map$ = property.getElementItemAt("value", propertyValue$);
            if (map$ != null) {
                Sack map = getBaseMember("property.map.base", map$);
                if (map != null){
                    return map$;
                }
            }
        }
        property.removeElementItem("value", propertyValue$);
        Sack propertyMap = new Sack();
        propertyMap.putAttribute(new Core("key", "residence.base",PROPERTY_MAP)); 
        propertyMap.putAttribute(new Core("key", "property", property.getKey()));
        propertyMap.putAttribute(new Core("key", "property.name", propertyName$));
        propertyMap.putAttribute(new Core("key", "property.name", propertyValue$));
        propertyMap.putAttribute(new Core("key", "icon", "property_list.gif"));
        propertyMap.putAttribute(new Core("String", "alias", "property." + propertyName$ + "" + propertyValue$));
        propertyMap.createElement("entity");
        property.putElementItem("value", new Core("key", propertyValue$, propertyMap.getKey()));
        save(property);
        save(propertyMap);
        return propertyMap.getKey();
    }
    /**
     * Delete property value from the index and all entities.
     *  @param propertyName$ the property name
     *  @param propertyValue$ the property value
     */ 
    public void prp_deletePropertyValue(String propertyName$, String propertyValue$) {
        if ((propertyName$ == null) || (propertyValue$ == null)){
        	LOGGER.severe(":prp_deletePropertyValue:argument is null");
        	return;
        }
       if("label".equals(propertyName$)){
    	   ent_releaseLabel(propertyValue$);
    	   return;
       }
       Sack property = indx_getProperty(propertyName$);
        if (property == null){
        	LOGGER.severe(":prp_deletePropertyValue:cannot find property");
        	return;
        }
        String map$ = property.getElementItemAt("value", propertyValue$);
        if(map$!=null){
           prp_deleteValue(map$);
        }
        property.removeElementItem("value", propertyValue$.trim());
        save(property);
    }
    /**
     * Replace property value in the index .
     *  @param propertyName$ the property name
     *  @param propertyValue$ the old property value
     *  @param newPropertyValue$ the new property value.
     */ 
    public void prp_editPropertyValue(String propertyName$, String propertyValue$,String newPropertyValue$) {
        if ((propertyName$ == null) || (propertyValue$ == null)){
        	LOGGER.severe(":prp_editPropertyValue:argument is null");
        	return;
        }
       Sack property = indx_getProperty(propertyName$);
        if (property == null){
        	LOGGER.severe(":prp_deletePropertyValue:cannot find property");
        	return;
        }
        Core map = property.getElementItem("value", propertyValue$);
        if(map==null){
        	LOGGER.severe(":prp_editPropertyValue:no map for property name="+propertyName$+" value="+propertyValue$);
        	return;
        }
        property.removeElementItem("value", propertyValue$.trim());
        property.putElementItem("value", new Core(map.type,newPropertyValue$,map.value));
        save(property);
    }
    /**
     * Delete property from the database.
     *  @param propertyName$ the property name
     */ 
    public void prp_deletePropertyName(String propertyName$) {
         if ((propertyName$ == null || propertyName$.length() < 1)) {
            Core[] ca = propertyIndex.elementGet("property");
            if (ca == null)
                return;
            ArrayList<Core> cl = new ArrayList<Core>();
            for (Core aCa : ca)
                if (aCa.name != null && aCa.name.length() > 0)
                    cl.add(aCa);
            ca = cl.toArray(new Core[0]);
            propertyIndex.elementReplace("property", ca);
            save(propertyIndex);
            return;
        }
        Sack property = indx_getProperty(propertyName$);
        if (property != null) {
            String[] sa = property.elementList("value");
            if (sa != null)
                for (String aSa : sa) prp_deleteValue(property.getElementItemAt("value", aSa));
            delete(property);
        }
        propertyIndex.removeElementItem("property", propertyName$.trim());
        save(propertyIndex);
    }
    private boolean prp_deleteValue(String map$) {
        if (map$ == null) {
        	LOGGER.severe(":prp-deleteValue:map is null");
        	return false;
        }
        Sack map = getBaseMember("property.map.base", map$);
        if (map == null) {
        	LOGGER.severe(":prp-deleteValue:can not find map=" + map$);
        	return false;
        }
        Sack property = getBaseMember("property.base", map.getAttributeAt("property"));
        if (property != null) {
            property.removeElementItem("value", property.getElementItemAtValue("value", map$));
            save(property);
        }
        String[] ea = map.elementList("entity");
        {
            if (ea != null) {
                {
                    Sack entity;
                    for (String anEa : ea) {
                        entity = getBaseMember("entity.base", anEa);
                        if (entity == null)
                            continue;
                        entity.removeElementItem("property", map$);
                        ent_alter(entity);
                    }
                }
            }
            delete(map);
        }
        return true;
    }
    private void delete(Sack sack) {
        if (sack == null)
            return;
        if (sack.getAttributeAt(PRESERVED) != null) {
        	LOGGER.severe(":delete:cannot delete preserved sack");
        	return;
        }
        try {
            String record$ = entihome$ + "/" + sack.getKey();
            File record = new File(record$);
            if (record.exists()) {
                 FileExpert.delete(record$);
            }
            String base$ = sack.getAttributeAt("residence.base");
            String path$;
            if ("register".equals(base$))
                path$ = entihome$ + "/" + sack.getPath();
            else
                path$ = entihome$ + "/" + base$ + "/data/" + sack.getKey();
            File file = new File(path$);
            file.delete();
        } catch (Exception e) {
           	LOGGER.severe(":delete"+e.toString());
        }
    }
    boolean col_existsRelation(Sack container, Sack component) {
        if (component == null || container == null)
            return false;
        if (col_existsComponent(container, component))
            if (col_existsContainer(container, component))
                return true;
        col_breakRelation(container, component);
        return false;
    }
    /**
     * Break container-component relation. 
     *  @param container the container entity
     *  @param component the component entity.
     * @return the container entity.
     */
    public Sack col_breakRelation(Sack container, Sack component) {
        if (container == null || component == null) {
        	LOGGER.severe(":col_breakRelation:argument is null");
            return container;
        }
        String component$ = component.getKey();
        String container$ = container.getKey();
        Core[] ca = component.elementGet("container");
        if (ca != null)
            for (Core aCa : ca)
                if (container$.equals(aCa.value))
                    component.removeElementItem("container", aCa.name);
        ca = container.elementGet("component");
        if (ca != null)
            for (Core aCa : ca)
                if (component$.equals(aCa.value)){
                    container.removeElementItem("component", aCa.name);
                    container.removeElementItem("component.type", aCa.name);
                }
        ent_alter(component);
        ent_alter(container);
        return container;
    }
    /**
     * Create a new entity 
     *  @param type$ the type(category) of the entity
     *  @param label$ the entity label.
     * @return the created entity.
     */
    public Sack ent_new(String type$,String label$) {
        if (type$ == null||label$==null){
          	LOGGER.severe(":ent_new:argument is null");
        	return null;
        }
        Sack entity = new Sack();
        entity.putAttribute(new Core("key", "icon", "sack.gif"));
        entity.putAttribute(new Core(null, "residence.base", ENTITY_BASE));
        entity.createElement("property");
        ent_assignLabel(entity, label$);
        entity.putElementItem("property", new Core("entity",Identity.key(),type$));
        ent_alter(entity);
    
        return entity;
    }
    /**
     * Create a new entity 
     *  @param type$ the type(category) of the entity
     *  @param label$ the entity label
     *  @param key$ the key of the entity.
     * @return the created entity.
     */
    public Sack ent_new(String type$,String label$,String key$) {
        if (type$ == null||label$==null||key$==null){
          	LOGGER.severe(":ent_new:argument is null");
        	return null;
        }
        Sack entity = new Sack();
        entity.setKey(key$);
        entity.putAttribute(new Core("key", "icon", "sack.gif"));
        entity.putAttribute(new Core(null, "residence.base", ENTITY_BASE));
        entity.createElement("property");
        entity.putElementItem("property", new Core("label",entity.getKey(),label$));
        entity.putElementItem("property", new Core("entity",Identity.key(),type$));
    
        ent_alter(entity);
        ent_reindex(entity);
        return entity;
    }
    /**
     * Get the entity by label 
     *  @param label$ the entity label
     * @return the entity or null.
     */
    public Sack ent_getAtLabel(String label$) {
    	try{
    	String key$=indx_keyAtLabel(label$);
    	return getBaseMember("entity.base",key$);
    	}catch(Exception e){
    		LOGGER.severe(":ent_getAtLabel:"+e.toString());
    		return null;
    	}
    }
    
    public void ent_releaseLabel(String label$){
    	storeAdapter.ent_releaseLabel(label$);
    }
    public boolean deleteEntity(Sack entity) {
    	if (entity == null) 
        	return true;
    	String key$=entity.getKey();
    	if(!storeAdapter.ent_delete(entity))
    		return false;
    	entitiesCache.delete(key$);
        String[] ra = ent_listContainers(entity);
        try {
            if (ra != null && ra.length > 0) {
                Sack container;
                for (String aRa : ra) {
                    container = getBaseMember("entity.base", aRa);
                    if (container != null)
                        col_breakRelation(container, entity);
                }
            }
        } catch (Exception e) {
        	LOGGER.severe(":deleteEntity:"+e.toString());
        }
        ra = ent_listComponents(entity);
        try {
            if (ra != null && ra.length > 0) {
                Sack component;
                for (String aRa : ra) {
                    component = getBaseMember("entity.base", aRa);
                    if (component != null) {
                        col_breakRelation(entity, component);
                    }
                }
            }
        } catch (Exception e) {
        	LOGGER.severe(e.toString());
        }
        String label$ = null;
        try {
            String[] sa = entity.elementList("property");
            label$ = entity.getProperty("label");
            if (sa != null) {
                Sack map;
                for (String aSa : sa) {
                    map = getBaseMember("property.map.base", aSa);
                    if (map == null)
                        continue;
                    map.removeElementItem("entity", key$);
                    save(map);
                }
            }

            if (label$ != null)
                prp_deletePropertyValue(entity.getProperty("entity"), label$);
       
        } catch (Exception e) {
        	//LOGGER.info(":deleteEntity:"+e.toString());
        }
        	
        	
        try {
            String record$ = getEntihome() + "/" + key$;
            FileExpert.delete(record$);
            if (label$ != null)
                prp_deletePropertyValue("label", label$);
        } catch (Exception ee) {
        //	LOGGER.info(":deleteEntity:"+ee.toString());
        }
       return true;
    }
    /**
     * List containers of the entity 
     *  @param entity the entity.
     * @return the array of keys of containers.
     */
public String[] ent_listContainers(Sack entity) {
        if (entity == null) {
           // LOGGER.severe(":ent_listContainers:entity is null");
            return null;
        }
        Core[] ca = entity.elementGet("container");
        if (ca == null) {
        	// LOGGER.info(":ent_listContainers:no 'container' element in entity=" + entity.getProperty("label"));
        	return null;
        }
        Stack<String> s = new Stack<String>();
        Sack container;
        boolean modified = false;
        for (Core aCa : ca) {
            if (entity.getKey().equals(aCa.value))
                continue;
            container = getBaseMember("entity.base", aCa.value);
            if (container == null) {
                entity.removeElementItem("container", aCa.name);
                modified = true;
                continue;
            }
            s.push(aCa.value);
        }
        if (modified)
            ent_alter(entity);
        int cnt = s.size();
        if (cnt < 1) {
            //LOGGER.info(":ent_listContainers:empty 'container' element in entity=" + entity.getProperty("label"));
            return null;
        }
        String[] sa = new String[cnt];
        for (int i = 0; i < cnt; i++)
            sa[i] =  s.pop();
        return sa;
    }
/**
 * List all entities in the database. 
 * @return the array of keys of all entities.
 */
    public String[] indx_listEntities() {
        String base$ = entihome$+ "/" +ENTITY_BASE+ "/data";
        File entityHome = new File(base$);
        try {
            return entityHome.list();
        } catch (Exception e) {
            LOGGER.severe(":indx_listEntities:" + e.toString());
            return null;
        }
    }
    /**
     * List components of the entity 
     *  @param entity the entity.
     * @return the array of keys of components.
     */
    public String[] ent_listComponents(Sack entity) {
       try{
    	if (entity == null)
            return null;
        Core[] ca = entity.elementGet("component");
        if (ca == null)
            return null;
        Stack<String> s = new Stack<String>();
        for (Core aCa : ca) {
            if (entity.getKey().equals(aCa.value))
                continue;
            s.push(aCa.value);
        }
        int cnt = s.size();
        if (cnt < 1)
            return null;
        String[] sa = new String[cnt];
        for (int i = 0; i < cnt; i++)
            sa[i] = s.pop();
        return sa;
       }catch(Exception e){
    	   Logger.getLogger(getClass().getName()).severe(e.toString());
    	   return null;
       }
    }
    /**
     * List components of the entity and its components
     * recursively. 
     *  @param entity the entity.
     * @return the array of keys of components.
     */
    public String[] ent_listComponentsCascade(Sack entity) {
        if (entity == null)
            return null;

        Stack<String> s = new Stack<String>();
        ent_listComponentsCascade(entity, s);
        int cnt = s.size();
        if (cnt < 1)
            return null;
        String[] sa = new String[cnt];
        for (int i = 0; i < cnt; i++)
            sa[i] = (String) s.pop();
        return sa;
    }
    private  void ent_listComponentsCascade(Sack entity, Stack<String> s) {
        String[] sa = ent_listComponents(entity);
        if (sa == null)
            return;
        Sack component ;
        for (String aSa : sa) {
            component =getEntityAtKey(aSa); 
            		//getMember("entity.base", aSa);
            if (component == null) {
                continue;
            }
            Support.addItem(aSa, s);
            ent_listComponentsCascade(component, s);
        }
    }
  /**
   * Get the path of the entity home directory 
   *  @param entity$ the entity key.
   * @return the path of the home directory.
   */
  public String ent_getHome(String entity$) {
          return entihome$ + "/" + entity$;
    }
  /**
   * Get the path of parent directory of the database. 
   * @return the path of the database parent directory.
   */
    public String getEntihome() {
        return entihome$;
        }
    /**
     * Get the name of the database directory. 
     * @return the name of the database directory.
     */
    public String getBaseName() {
        String entihome$ = getEntihome();
        if (entihome$ == null)
            return null;
        try {
            File entihome = new File(entihome$);
            return entihome.getName();
        } catch (Exception e) {
        	 LOGGER.severe(":getBasename:" + e.toString());
        	return null;
        }
    }

 private static String[] intersect(String[] list1, String[] list2) {

     if (list2 == null || list1 == null) {
         return null;
     }
     Stack<String> s1 = new Stack<String>();
     Stack<String> s2 = new Stack<String>();
     for (String aList2 : list2) s2.push(aList2);
     String line$ ;
     boolean found ;
     String member$ = null;
     while (!s2.isEmpty()) {
         try {
             found = false;
             line$ = s2.pop().toString();
             if (line$ == null)
                 continue;
             for (String aList1 : list1) {
                 member$ = aList1;

                 if (line$.equals(member$)) {
                     found = true;
                     break;
                 }
             }
             if (found)
                     Support.addItem(member$, s1);
                 //}
         } catch (Exception e) {
        	Logger.getLogger(Entigrator.class.getName()).info(":intersect:"+e.toString());
         }
     }
     int cnt = s1.size();
     if (cnt < 1)
         return new String[0];
     String[] res = new String[cnt];
     for (int i = 0; i < cnt; i++)
         res[i] = s1.pop().toString();
     return res;
 }
 
  /**
  * Get the icon from the icon directory
  * encoded as Base64 string. 
  *  @param icon$ the name of icon file.
  * @return the icon string .
  */ 
public  String readIconFromIcons(String icon$) {
 try {
		String path$ = getEntihome() + "/" + ICONS+"/"+icon$;
		FileInputStream is=new FileInputStream(path$);
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(b)) != -1) {
               bos.write(b, 0, bytesRead);
            }
            byte[] ba = bos.toByteArray();
            is.close();
           return Base64.encodeBase64String(ba);
	} catch (Exception e) {
		 
	}
return null;
}
/**
 * Copy the icon from the class resource
 *  into icons folder.
 *  @param handler the handler class
 *  @param icon$ the name of icon resource.
 */ 
public void saveHandlerIcon(Class<?> handler,String icon$) {
		try {
			File iconFile=new File(getEntihome()+"/"+ICONS+"/"+icon$);
			if(iconFile.exists())
				return;
			InputStream is=handler.getResourceAsStream(icon$);
			iconFile.createNewFile();
			FileOutputStream fos=new FileOutputStream(iconFile);
	            byte[] b = new byte[1024];
	            int bytesRead = 0;
	            while ((bytesRead = is.read(b)) != -1) {
	               fos.write(b, 0, bytesRead);
	            }
	            is.close();
	            fos.close();
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
/**
 * Reload entity from the disk
 *  @param entityKey$ the key string
 *  @return the entity.
 */ 
public Sack ent_reload(String entityKey$){
	Sack entity= Sack.parseXML(this,entihome$+"/"+ Entigrator.ENTITY_BASE +"/data/"+entityKey$);
	if(entity!=null)
		entitiesCache.put(entity);
	return entity;
}
/**
 * Check if the entity exists
 *  @param entityKey$ the key of the entity
 *  @return true if the entity exists false otherwise
 */

public boolean ent_existsAtKey(String entityKey$){
	return storeAdapter.entExistsAtKey(entityKey$);
}
/**
 * Check if the entity is busy and cannot be modified.
 *  @param entityKey$ the key of the entity
 *  @return true if the entity is busy false otherwise.
 */
public boolean ent_isBusy(String entityKey$){
	return storeAdapter.entIsBusy(entityKey$);
}
/**
 * Check if entities from the list or the storage are busy
 * and cannot be modified.
 *  @param storage true if the storage must be checked false otherwise.
 *  @param sa the array of keys of entities to be checked. 
 *  @return true if entities or storage are false otherwise.
 */
public boolean store_scopeIsBusy(boolean storage,String[] sa){
	if(storage)
		if(storeAdapter.qmIsBusy())
			return true;
	if(sa!=null)
		for(String s:sa)
			if (storeAdapter.entIsBusy(s))
					return true;
	return false;		
}
/**
 * Set flag busy.
 *  @param storage true if the storage must be locked false otherwise.
 *  @param sa the array of keys of entities to be locked. 

 */
public void store_setScopeBusy(boolean storage,String[] sa){
	if(storage)
		storeAdapter.qmSetBusy();
	if(sa!=null)
		for(String s:sa)
			storeAdapter.entSetBusy(s);
}

/**
 * Release the entity for editing.
 *  @param entityKey$ the key of the entity.
 */ 

public void ent_release(String entityKey$){
	 
	storeAdapter.entRelease( entityKey$);
}
/**
 * Save the entity on the disk.
 *  @param entity the entity.
 *  
 */ 

public void ent_alter(Sack entity){
	//System.out.println("Entigrator:ent_alter:entity="+entity.getProperty("label")); 
	storeAdapter.entReplace(entity);
    entitiesCache.put(entity);	
}
/**
 * Release the entity in the separate thread.
 *  @param entity the entity.
 */ 

public void ent_runReplace(Sack entity){
	//System.out.println("Entigrator:ent_runReplace:entity="+entity.getProperty("label")); 
	storeAdapter.entRunReplace( entity);
}

/**
 * Lock the entity for editing.
 *  @param entity the the entity.
 *  
 */ 
public void ent_setBusy(Sack entity){
	storeAdapter.entSetBusy(entity);
	}
/**
 * Check if at least one entity having the given tape
 * exists.
 *  @param type$ the type.
 *  @return true if succeeded false otherwise.
 */ 


public boolean ent_existsAtType(String type$){
	return storeAdapter.ent_existsAtType(type$);
}
/**
 * Check if the entity has different ID as 
 * the same entity saved on the disk.

 *  @param entity the entity.
 *  @return true if IDs are different false otherwise.
 */ 

public boolean ent_entIsObsolete(Sack entity){
	return storeAdapter.entIsObsolete(entity);
	}
/**
 * Put facet handler in the cache. 
 *  @param handler$ the name of the handler's class.
 *  @param handler the facet handler.
 *  
 */ 
public void putHandler(String handler$,Object handler){
	try{
		 if(debug)
		System.out.println("Entigrator:putHandler:handler="+handler$);
		handlers.put(handler$, handler);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
/**
 * Get the facet handler from the cache. 
 *  @param handler$ the name of the handler's class.
 *  @return  the handler.
 */ 

public Object getHandler(String handler$){
	try{
		if(debug)
		System.out.println("Entigrator:getHandler:handler="+handler$);
		return handlers.get(handler$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return null;
}
/**
 * Put facet handler in the types map. 
 *  @param type$ the entity type assigned to the handler.
 *  @param handler the facet handler.
 *  
 */ 

public void putHandlerAtType(String type$,Object handler){
	try{
		 if(debug)
		System.out.println("Entigrator:putHandlerAtType:handler="+type$);
		type2handler.put(type$, handler);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
/**
 * Get all facet handlers from the map. 
 *  @return the array of facet handlers.
 *  
 */ 

public FacetHandler[] getAllFacetHandlers(){
	if( type2handler.isEmpty())
		return null;
	ArrayList<Object> ol = new ArrayList<Object>(type2handler.values());
	ArrayList<FacetHandler>fhl=new ArrayList<FacetHandler>();
	for(Object o:ol ){
		if(o instanceof FacetHandler)
			fhl.add((FacetHandler)o);
	}
	return fhl.toArray(new FacetHandler[0]);
	
}
/**
 * Get a facet handler from the map.
 * @param type$ the entity type assigned to the handler.  
 *  @return the facet handler.
 *  
 */
public Object getHandlerAtType(String type$){
	try{
		if(debug)
		System.out.println("Entigrator:getHandlerAtType:handler type="+type$);
		return type2handler.get(type$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return null;
}

public boolean keyExistsInCache(String entityKey$){
	if(entitiesCache.get(entityKey$)!=null)
		return true;
	else
		return false;
}
public void store_replace(){
	 storeAdapter.qmRunReplace();
}
public void store_reload(){
	 if(debug)
	   System.out.println("Entigrator:store_reload");
	
	 storeAdapter.qmReload();
}

public void clearCache(){
	entitiesCache.clear();

}
public void clearHandlers(){
	handlers.clear();
	type2handler.clear();

}
public boolean store_outdated(){
	
	return storeAdapter.qmOutdated();
}
public boolean ent_outdated(Sack entity){
	
	return storeAdapter.entIsObsolete(entity);
}
public void store_refresh(){
	storeAdapter.qmRefresh();
}
public boolean store_isBusy(){
	 if(debug)
		 System.out.println("Entigrator:store_isBusy");
	return storeAdapter.qmIsBusy();
}

public void store_release(){
	//System.out.println("Entigrator:store_release");
	storeAdapter.qmRelease();
}
public void store_lock(){
	storeAdapter.qmSetBusy();
}

public void setBulkMode(boolean set){
	 storeAdapter.setBulkMode(set);
}


}

        
