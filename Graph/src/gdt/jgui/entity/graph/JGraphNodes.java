package gdt.jgui.entity.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JItemsListPanel.ItemPanelComparator;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.edge.JBondsPanel;

public class JGraphNodes extends JEntitiesPanel{
	private Logger LOGGER=Logger.getLogger(JGraphNodes.class.getName());
	JMenu menu1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  public JGraphNodes (){
	        super();	
	    }
	  @Override
		public String getLocator() {
			try{
			String locator$=super.getLocator();
					if(entihome$!=null){
						Entigrator entigrator=console.getEntigrator(entihome$);
					String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
					if(icon$!=null)
					    	locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
					}
					locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JGraphNodes.class.getName());
					locator$=Locator.append(locator$,BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
					return locator$; 
				}catch(Exception e){
		        Logger.getLogger(getClass().getName()).severe(e.toString());
		        return null;
				}
		}
	  @Override
	public JContext instantiate(JMainConsole console, String locator$) {
		  try{
				this. console=console;
				this.locator$=locator$;
				 Properties locator=Locator.toProperties(locator$);
				 //list$=locator.getProperty(EntityHandler.ENTITY_LIST);
				 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 entihome$=locator.getProperty(Entigrator.ENTIHOME);
				 Entigrator  entigrator=console.getEntigrator(entihome$);
				 Sack graph=entigrator.getEntity(entityKey$);
				 String[] sa=graph.elementList("node");
				 if(sa!=null){
					list$=Locator.toString(sa);
					locator$=Locator.append(locator$,EntityHandler.ENTITY_LIST ,list$);
				 
	        	// containerKey$=locator.getProperty(EntityHandler.ENTITY_CONTAINER);
	        	// componentKey$=locator.getProperty(EntityHandler.ENTITY_COMPONENT);
				 JItemPanel[] ipl= listNodes( );
	        	 putItems(ipl);
				 }
	        	return this;
	        }catch(Exception e){
	        
	        LOGGER.severe(e.toString());
	        }
		  
		  JContext context=super.instantiate(console, locator$);
		  return context;
	  } 
	  @Override
	  /**
	   * Get the context title.
	   * @return the context title.	
	   */
	
  public String getTitle() {
	  		return "Nodes";
	  		
	  	}
  @Override
	  public JMenu getContextMenu() {
		  //menu=super.getContextMenu();
	   menu1=new JMenu("Context");
		  //menu.setName("Context");
		  menu=super.getContextMenu();
		  if(menu!=null){
	      int cnt=menu.getItemCount();
		//  System.out.println("JGraphNode:getContextMenu:super menu cnt="+cnt);
		  mia=new JMenuItem[cnt];
		  for (int i=0;i<cnt;i++)
			  mia[i]=menu.getItem(i);
		  }
		  	
		  menu1.addMenuListener(new MenuListener(){
		  	@Override
		  	public void menuSelected(MenuEvent e) {
		  		// System.out.println("JGraphNode:getContextMenu:menu selected");
		  		 menu1.removeAll();
		  		 if(mia!=null){
		  		 for(JMenuItem mi:mia)
		  			try{
		  			 if(mi!=null&&mi.getText()!=null){ 
		  			    menu1.add(mi);
		  			  System.out.println("JGraphNode:getConextMenu:add item="+mi.getText());
		  			 }
		  			}catch(Exception ee){
		  				 System.out.println("JGraphNode:getConextMenu:"+ee.toString());
		  			}
		  	
		  	 }
		  	
		  //	 System.out.println("JGraphNode:getContextMenu:2:menu cnt="+menu1.getItemCount());
		  	 menu1.addSeparator();
		  
		  	 if(hasSelectedItems()){
		  	JMenuItem deleteItem = new JMenuItem("Delete");
		  	 deleteItem.addActionListener(new ActionListener() {
		  		@Override
		  		public void actionPerformed(ActionEvent e) {
		  			 
		  			int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
		  				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		  			   if (response == JOptionPane.YES_OPTION) {
		  				  String[] sa=JGraphNodes.this.listSelectedItems();
		  				  if(sa==null)
		  					  return;
		  			
		  			   }
		  			   }
		  	});
		  	menu1.add(deleteItem);
		  	 }
		  
		  //	System.out.println("JGraphNode:getContextMenu:add 'All nodes'"); 
		  	JMenuItem allNodesItem = new JMenuItem("All nodes");
		  	allNodesItem.addActionListener(new ActionListener() {
		  		@Override
		  		public void actionPerformed(ActionEvent e) {
		  			try{
		  				Entigrator entigrator=console.getEntigrator(entihome$);
		  				String []sa= entigrator.indx_listEntitiesAtPropertyName("node");
		  				if(sa!=null){
		  					ArrayList <String> la=new ArrayList<String>();
		  					String label$;
		  					for(String s:sa){
		  						label$=entigrator.indx_getLabel(s);
		  						if(label$!=null)
		  							la.add(label$);
		  					}
		  					sa=la.toArray(new String[0]);
		  					if(sa.length<1)
		  						return;
		  					String list$=Locator.toString(sa);
		  					JEntitiesPanel ep=new JEntitiesPanel();
		  					String locator$=ep.getLocator();
		  					locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
		  					locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, list$);
		  					JConsoleHandler.execute(console, locator$);
		  				}
		  			}catch(Exception ee){
		  				System.out.println("JGraphNode:getConextMenu:all nodes:"+ee.toString());
		  			}
		  		}
		  	
		  	});
		  	allNodesItem.setVisible(true);
		  	menu1.add(allNodesItem);  
			 if(hasNodesToPaste()){
				  	JMenuItem pasteItem = new JMenuItem("Paste");
				  	 pasteItem.addActionListener(new ActionListener() {
				  		@Override
				  		public void actionPerformed(ActionEvent e) {
				  			   pasteNodes(); 
				  			   }
				  	});
				  	menu1.add(pasteItem);
				  	 }
		  	}
		  	@Override
			public void menuDeselected(MenuEvent e) {
			}
			@Override
			public void menuCanceled(MenuEvent e) {
			}	
		});
		return menu1;
		}
  private   boolean hasNodesToPaste(){
		try{
			String[] sa=console.clipboard.getContent();
			if(sa==null)
				return false;
			System.out.println("JGraphNode:hasNodesToPaste:sa="+sa.length);
			Properties locator;
			Sack node;
			Entigrator entigrator=console.getEntigrator(entihome$);
			for(String s:sa){
				try{
					System.out.println("JGraphNode:hasNodesToPaste:node locator="+s);
			    locator=Locator.toProperties(s);
			    node=entigrator.getEntity(locator.getProperty(EntityHandler.ENTITY_KEY));
			    if(node.getProperty("node")!=null)
			    		return true;
				}catch(Exception ee){}
			}
		}catch(Exception e){
			Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		}
		return false;
	}
  private   void pasteNodes(){
		try{
			String[] sa=console.clipboard.getContent();
			
			Properties locator;
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph= entigrator.getEntity(entityKey$);
			if(!graph.existsElement("node"))
				graph.createElement("node");
			String nodeKey$;
			String nodeIcon$;
			Sack node;
			for(String s:sa){
				try{
			    locator=Locator.toProperties(s);
			    nodeKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			    node=entigrator.getEntity(nodeKey$);
			   nodeIcon$=entigrator.getEntityIcon(nodeKey$);
		    	if(node.getProperty("node")!=null){
		                graph.putElementItem("node", new Core(nodeIcon$,nodeKey$,node.getProperty("label")));
			    	}
				}catch(Exception ee){}
			}
		 entigrator.save(graph);
		 JConsoleHandler.execute(console, getLocator());
		}catch(Exception e){
			Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		}
		
	}
  public  JItemPanel[] listNodes(){
		try{
			//System.out.println("JGraphNode:listNodes:entity key=" + entityKey$); 
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntity(entityKey$);
			   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
			   JItemPanel itemPanel;
			   String entityLocator$;
			   Core[] ca=graph.elementGet("node");
			   String nodeIcon$;
		   for(Core c:ca){
			   try{
			//	   System.out.println("JGraphNode:listNodes:node key="+c.name);   
			   entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, c.name);
			   entityLocator$=Locator.append(entityLocator$,Entigrator.ENTIHOME , entihome$);
			   entityLocator$=Locator.append(entityLocator$,Locator.LOCATOR_CHECKABLE , Locator.LOCATOR_TRUE);
			   JEntityFacetPanel em=new JEntityFacetPanel();
			   em.instantiate(console, entityLocator$);
			   String emLocator$=em.getLocator();
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_TITLE,c.value);
			   nodeIcon$=entigrator.readIconFromIcons(c.type);
			   if(nodeIcon$!=null)
				   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_ICON,nodeIcon$);
			   itemPanel=new JItemPanel(console,emLocator$);
			   ipl.add(itemPanel);
			   }catch(Exception ee){
				   Logger.getLogger(JEntitiesPanel.class.getName()).info(ee.toString());
			   }
		   }
		   Collections.sort(ipl,new ItemPanelComparator());
		   JItemPanel[] ipa=ipl.toArray(new JItemPanel[0]);
		   return ipa;
		}catch(Exception e) {
      	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
          return null;
      }
	}
}
