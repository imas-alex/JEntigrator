package gdt.jgui.entity.node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.bonddetail.JBondDetailFacetOpenItem;
import gdt.jgui.entity.bonddetail.JBondDetailPanel;
import gdt.jgui.entity.edge.JBondsPanel;

import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.entity.graph.JGraphRenderer;
import gdt.jgui.entity.graph.JWebGraph;


public class JNodeFacetOpenItem extends JFieldsFacetOpenItem {
	private static final long serialVersionUID = 1L;
	boolean debug=false;
	public JNodeFacetOpenItem(){
		super();
	}
	
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Node");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JNodeFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,NodeHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Node facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Node");
	locator.setProperty(FACET_HANDLER_CLASS,NodeHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
		//Entigrator entigrator=console.getEntigrator(entihome$);
	// String icon$=Support.readHandlerIcon(JNodeEditor.class, "node.png");
	//String icon$=ExtensionHandler.loadIcon(entigrator, NodeHandler.EXTENSION_KEY, "node.png");
    //if(icon$!=null)
		locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"node.png");
		locator.setProperty(Locator.LOCATOR_ICON_LOCATION,NodeHandler.EXTENSION_KEY);	
	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	
    
	return Locator.toString(locator);
}

@Override
public String getFacetName() {
	return "Node";
}
@Override
public String getFacetIcon(Entigrator entigrator) {
	
	return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"node.png"); 

}

@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
	//	System.out.println("JAddressFacetOpenItem:openFacet:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String responseLocator$=getLocator();
		Properties responseLocator=Locator.toProperties(responseLocator$);
		responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
		responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
		//
		JEntityFacetPanel efp=new JEntityFacetPanel();
		String efpLocator$=efp.getLocator();
		efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
		efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		efpLocator$=Locator.append(efpLocator$, JRequester.REQUESTER_ACTION, ACTION_DISPLAY_FACETS);
		responseLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(efpLocator$));
		//
		responseLocator$=Locator.toString(responseLocator);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);

		JBondsPanel bondsPanel=new JBondsPanel();
		String bpLocator$=bondsPanel.getLocator();
		bpLocator$=Locator.append(bpLocator$, Entigrator.ENTIHOME, entihome$);
		bpLocator$=Locator.append(bpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		bpLocator$=Locator.append(bpLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		bpLocator$=Locator.append(bpLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, bpLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}

@Override
public String getFacetRenderer() {
	return JNodeEditor.class.getName();
}
@Override
public FacetHandler getFacetHandler() {
	return new NodeHandler();
}
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	//System.out.println("JFieldsFacetOpenItem:edit:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	return super.getPopupMenu(digestLocator$);

}
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JAddressFacetOpenItem:responce:locator="+locator$);
	super.response(console,locator$);

}
@Override
public boolean isRemovable() {
	try{
		entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
		entityKey$=Locator.getProperty(locator$,EntityHandler.ENTITY_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity =entigrator.getEntityAtKey(entityKey$);
		 if("node".equals(entity.getProperty("entity")))
			 return false;
		 return true;
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	return false;
	}
}
@Override
public void removeFacet() {
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity =entigrator.getEntityAtKey(entityKey$);
		 if("node".equals(entity.getProperty("entity")))
			 return ;
		 entity.removeElementItem("fhandler", NodeHandler.class.getName());
		 entity.removeElementItem("jfacet", NodeHandler.class.getName());
		 entity.removeElement("bond");
		 entity.removeElement("edge");
		
		 entigrator.ent_alter(entity);
		 entigrator.ent_takeOffProperty(entity, "node");
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	
}
@Override
public String getFacetIconName() {
	return "node.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		if(debug)
			System.out.println("JNodeFacetOpenItem:locator="+locator$);
			
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		if(entityLabel$==null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		if(entityKey$==null)
			entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		String fieldsFacet$=locator.getProperty(FIELDS_FACET);
		if(debug)
		System.out.println("JNodeFacetOpenItem:web home="+webHome$+ " web requester="+webRequester$);
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack entity=entigrator.getEntityAtKey(entityKey$);
	    //    Core[]	ca=entity.elementGet("field");
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		
		sb.append(WUtils.getMenuBarScript());
		sb.append(WUtils.getMenuBarStyle());
	    sb.append("</head>");
	    sb.append("<body onload=\"onLoad()\" >");
	    sb.append("<ul class=\"menu_list\">");
	    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
	    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
	    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
	    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
	    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
	    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
	    sb.append("<li id=\"graph\" class=\"menu_item\" onclick=\"graph()\"><a href=\"#\">Graph</a></li>");
	    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	    if(fieldsFacet$==null)
	    	fieldsFacet$="Node";
	    sb.append("<tr><td>Facet: </td><td><strong>"+fieldsFacet$+"</strong></td></tr></table>");
	    
	    sb.append(getItems( webHome$,entigrator,entity));
        sb.append("<script>");
	    sb.append("function onLoad() {");
	    if(debug)
			System.out.println("JNodeFacetOpenItem:0");
	    //sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("initBack(\"gdt.jgui.entity.node.JNodeFacetOpenItem\",\""+webRequester$+"\");");
	    sb.append("}");
	    if(debug)
			System.out.println("JNodeFacetOpenItem:0.1");
	  
	    sb.append("function graph(){");
	   
	  //  JGraphRenderer gr=new JGraphRenderer();
	    if(debug)
			System.out.println("JNodeFacetOpenItem:1");
	   
	    //String graphLocator$=gr.getLocator();
	    //if(debug)
		//	System.out.println("JNodeFacetOpenItem:graph locator 0="+graphLocator$);
			
	  
	    Properties graphLocator=new Properties();
	    graphLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	    graphLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    graphLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    graphLocator.setProperty(WContext.WEB_HOME,webHome$);
	    graphLocator.setProperty(JGraphRenderer.SELECTED_NODE_LABEL,entityLabel$);
	    if(debug)
			System.out.println("JNodeFacetOpenItem:1");
	
	    graphLocator.setProperty(BaseHandler.HANDLER_CLASS, JWebGraph.class.getName());
	    graphLocator.setProperty( JRequester.REQUESTER_ACTION, JGraphRenderer.ACTION_RELATIONS);
	    String graphLocator$=Locator.toString(graphLocator);
	    if(debug)
			System.out.println("JNodeFacetOpenItem:graph locator="+graphLocator$);
			
	    String graphUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(graphLocator$.getBytes());
	    sb.append(" window.location.assign(\""+graphUrl$+"\");");
	    sb.append("}");
	    
	    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
	    
	 	    sb.append("</script>");
	    sb.append("</body>");
	    sb.append("</html>");
	    return sb.toString();
        
	}catch(Exception e){
		Logger.getLogger(JBasesPanel.class.getName()).severe(e.toString());	
	}
	return null;
}
private String getItems(String webHome$,Entigrator entigrator,Sack entity){
	try{
		if(debug)
			System.out.println("JNodeFacetOpenItem:getItems:node="+entity.getProperty("label"));
		StringBuffer sb=new StringBuffer();	
		Core[] ca=entity.elementGet("bond");
		if(debug){
			if(ca==null){
			System.out.println("JNodeFacetOpenItem:getItems:no bonds");
			return null;
			}
			else
				System.out.println("JNodeFacetOpenItem:getItems:ca="+ca.length);
		}
		
		String edgeKey$;
		boolean isOut=true;
		String entityKey$=entity.getKey();
		for(Core c:ca){
			if(!entityKey$.equals(c.type)){
				isOut=false;
				break;
			}
		}
		boolean isIn=true;
		
		for(Core c:ca){
			if(!entityKey$.equals(c.value)){
				isIn=false;
				break;
			}
		}
		if(ca!=null){
			
			ArrayList<Core> cl=new ArrayList<Core>(Arrays.asList(ca));
			if(debug)
				System.out.println("JNodeFacetOpenItem:getItems:cl="+cl.size());
		
			if(isOut){
			BondComparatorByValue bc=new BondComparatorByValue();
			bc.entigrator=entigrator;
			Collections.sort(cl,bc);
			}
			if(isIn){
				BondComparatorByType bc=new BondComparatorByType();
				bc.entigrator=entigrator;
				Collections.sort(cl,bc);
			}
			ca=cl.toArray(new Core[0]);
			String icon$=ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY, "bond.png");
			
		
			for(Core aCa:ca){
				  try{
  				   edgeKey$=entity.getElementItemAt("edge",aCa.name);
  				   sb.append(getItem( entigrator, icon$,  entityKey$,webHome$, aCa, edgeKey$,isOut,isIn)+"<br>");
			      }catch(Exception ee){
						   Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(ee.toString());
				  }
			}
		}
		if(debug)
			System.out.println("JNodeFacetOpenItem:getItems:sb="+sb.toString());
	return sb.toString();	
	}catch(Exception e){
        Logger.getLogger(JBondsPanel.class.getName()).severe(e.toString());
    }
     return null;	
	}
private static String getItem(Entigrator entigrator,String icon$, String entityKey$,String webHome$, Core bond,String edgeKey$,boolean isOut,boolean isIn){
	try{
	String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
			  "\" width=\"24\" height=\"24\" alt=\"image\">";
    
	String outLabel$=entigrator.indx_getLabel(bond.type);
    String inLabel$=entigrator.indx_getLabel(bond.value);
    String edgeLabel$=entigrator.indx_getLabel(edgeKey$);
	String outHref$= getEntityReference( entigrator, webHome$, bond.type);
	String inHref$= getEntityReference( entigrator, webHome$, bond.value);
	String edgeHref$= getEntityReference( entigrator, webHome$, edgeKey$);
	StringBuffer sb=new StringBuffer();
//	sb.append(iconTerm$);
	if(!isOut){
	sb.append("<a href=\""+outHref$+"\">"+outLabel$+"</a>");
	sb.append(" -> ");
	}
	
	sb.append("<a href=\""+edgeHref$+"\">"+edgeLabel$+"</a>");
	if(hasDetailes(entigrator,bond.name,edgeKey$)){
		//sb.append("(Details)");
		String detReference$=getDetailsReference( entigrator, webHome$,entityKey$, edgeKey$,bond);
		sb.append("<a href=\""+detReference$+"\">(details)</a>");
		
	}
	
	if(!isIn){
	sb.append(" -> ");
	sb.append("<a href=\""+inHref$+"\">"+inLabel$+"</a>");
	}
	return sb.toString();
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;	
	
}
private static String getDetailsReference(Entigrator entigrator,String webHome$,String entityKey$,String edgeKey$,Core bond){
	try{
		 Properties foiLocator=new Properties();   
		 foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JBondDetailFacetOpenItem.class.getName());
	     foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	     String entityLabel$=entigrator.indx_getLabel(entityKey$);
	    foiLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		foiLocator.setProperty(JBondsPanel.EDGE_KEY,edgeKey$);
		foiLocator.setProperty(JBondsPanel.BOND_KEY,bond.name);
		foiLocator.setProperty(Locator.LOCATOR_TITLE,"Details");
		foiLocator.setProperty(WContext.WEB_HOME,webHome$);
		foiLocator.setProperty(WContext.WEB_REQUESTER,JNodeFacetOpenItem.class.getName());
		return webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());	 
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;
}
private static String getEntityReference(Entigrator entigrator,String webHome$,String entityKey$){
	try{
		 Properties foiLocator=new Properties();   
		 foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
	     foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	     String entityLabel$=entigrator.indx_getLabel(entityKey$);
	    foiLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		foiLocator.setProperty(Locator.LOCATOR_TITLE,entityLabel$);
		foiLocator.setProperty(WContext.WEB_HOME,webHome$);
		foiLocator.setProperty(WContext.WEB_REQUESTER,JNodeFacetOpenItem.class.getName());
		return webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());	 
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;
}
private static boolean hasDetailes(Entigrator entigrator,String bondKey$,String edgeKey$){
	try{
		Sack edge=entigrator.getEntityAtKey(edgeKey$);
		 Core[] ca=edge.elementGet("detail");
		 if(ca==null)
			 return false;
		Core bond=edge.getElementItem("bond", bondKey$);
		for(Core c:ca)
           if(bondKey$.equals(c.type))
        	  return true;
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return false;
}

private static class BondComparatorByValue implements Comparator<Core>{
    public Entigrator entigrator;
	@Override
	public int compare(Core o1, Core o2) {
		try{
    		String l1$=o1.value;
    		String l2$=o2.value;
    		String i1$=entigrator.indx_getLabel(l1$);
    		String i2$=entigrator.indx_getLabel(l2$);
    		if(i1$==null&&i2$==null)
    			return 0;
    		if(i1$==null||"null".equals(i1$)&&i2$!=null)
    			return -1;
    		if(i2$==null||"null".equals(i2$)&&i1$!=null)
    			return 1;	
    		return i1$.compareToIgnoreCase(i2$);
    	}catch(Exception e){
    		return 0;
    	}
	}
}
	private static class BondComparatorByType implements Comparator<Core>{
	    public Entigrator entigrator;
		@Override
		public int compare(Core o1, Core o2) {
			try{
	    		String l1$=o1.type;
	    		String l2$=o2.type;
	    		String i1$=entigrator.indx_getLabel(l1$);
	    		String i2$=entigrator.indx_getLabel(l2$);
	    		if(i1$==null&&i2$==null)
	    			return 0;
	    		if(i1$==null||"null".equals(i1$)&&i2$!=null)
	    			return -1;
	    		if(i2$==null||"null".equals(i2$)&&i1$!=null)
	    			return 1;	
	    		return i1$.compareToIgnoreCase(i2$);
	    	}catch(Exception e){
	    		return 0;
	    	}
		}
	
}
	@Override
	public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String locator$) {
		return null;
	}
}
