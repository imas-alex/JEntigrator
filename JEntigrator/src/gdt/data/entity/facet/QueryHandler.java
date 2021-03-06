package gdt.data.entity.facet;

import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
/**
* Contains methods to process a query entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class QueryHandler extends FacetHandler {
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	/**
	 * Check if the query handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	String query$=entity.getProperty("query");
				if(query$!=null&&!Locator.LOCATOR_FALSE.equals(query$)){
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
	 /**
     * Get title of the query handler.  
     * @return the title of the query handler..
     */	
	@Override
	public String getTitle() {
		return "Query";
	}
	 /**
     * Get type of the query handler.  
     * @return the type of the query handler..
     */	
	@Override
	public String getType() {
		return "query";
	}
	 /**
     * Get class name of the query handler.  
     * @return the class name of the query handler..
     */	
	@Override
	public String getClassName() {
		return QueryHandler.class.getName();
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "query", entityLabel$);
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
