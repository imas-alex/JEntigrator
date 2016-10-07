package gdt.jgui.entity.graph;
/*
 * Copyright 2016 Alexander Imas
 * This file is extension of JEntigrator.

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Timer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconShapeTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.PickWithIconListener;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.VertexStringerImpl;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.tool.AutocompleteJComboBox;
import gdt.jgui.tool.JTextEditor;
/**
 * This context visualize the graph.
 *  * @author imasa
 *
 */

public class JGraphRenderer extends JPanel implements JContext , JRequester
, MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private static final String ACTION_CREATE_GRAPH="action create graph";
	private JMainConsole console;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    private Sack graphEntity;
   private String locator$;
   int v=-1;
   int b=-1;
  private JPopupMenu popup;
    String requesterResponseLocator$;
    AutocompleteJComboBox searchBox ;
private VisualizationViewer<Number,Number> vv = null;

//private AbstractLayout<Number,Number> layout = null;

    Timer timer;
    DirectedSparseGraph<Number, Number> graph;
 //   boolean done;

    protected JButton switchLayout;

    public static final int EDGE_LENGTH = 100;
    Integer v_prev = null;
    String message$;
/**
 * The default constructor
 */
    public JGraphRenderer()
  	{
  	    super();
  	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  	    
  	}
    /**
     * Response on call from the other context.
     *	@param console main console
     *  @param locator$ action's locator 
     */  
	@Override
	public void response(JMainConsole console, String locator$) {
//		System.out.println("JGraphrenderer:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);

			if(ACTION_CREATE_GRAPH.equals(action$)){
				Sack newEntity=entigrator.ent_new("graph", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"name","value"));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,GraphHandler.class.getName(),GraphHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.graph.JGraphFacetAddItem",EdgeHandler.class.getName(),"gdt.jgui.entity.graph.JGraphFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","graph.png"));
				entigrator.save(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "graph", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JGraphRenderer.class, "graph.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
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
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
/**
 * Get the context panel.
 * @return the context panel.
 */
	@Override
	public JPanel getPanel() {
		return this;
	}
	 /**
		 * Get the context menu.
		 * @return the context menu.
		 */
	@Override
	public JMenu getContextMenu() {
		final JMenu	menu=new JMenu("Context");
		   menu.setName("Context");
		   menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
					menu.removeAll();
					//mxGraphComponent graphComponent = new mxGraphComponent(graph);
					JMenuItem edgesItem = new JMenuItem("Edges");
					 edgesItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
						
								JGraphEdgesPanel ep=new JGraphEdgesPanel();
								String ep$=ep.getLocator();
								ep$=Locator.append(ep$, Entigrator.ENTIHOME, entihome$);
								ep$=Locator.append(ep$, EntityHandler.ENTITY_KEY, entityKey$);
								ep$=Locator.append(ep$, EntityHandler.ENTITY_LABEL, entityLabel$);
								Entigrator entigrator=console.getEntigrator(entihome$);
								String icon$=ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY, "edge.png");
								ep$=Locator.append(ep$,Locator.LOCATOR_ICON,icon$);
								JConsoleHandler.execute(console, ep$);
							}catch(Exception ee){
								 Logger.getLogger(JGraphRenderer.class.getName()).info(ee.toString());
							}
							   }
					});
					menu.add(edgesItem);
					 JMenuItem  nodesItem = new JMenuItem("Nodes");
					   nodesItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								JGraphNodes gn=new JGraphNodes();
								String gnLocator$= gn.getLocator();
								gnLocator$=Locator.append(gnLocator$, Entigrator.ENTIHOME, entihome$);
								gnLocator$=Locator.append(gnLocator$, EntityHandler.ENTITY_KEY, entityKey$);
								JConsoleHandler.execute(console, gnLocator$);
							}
						} );
						menu.add(nodesItem);
						JMenuItem  entityItem = new JMenuItem("Entity");
						   entityItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									JEntityFacetPanel fp=new JEntityFacetPanel();
									String fp$=fp.getLocator();
									fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
									fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY, entityKey$);
									fp$=Locator.append(fp$, EntityHandler.ENTITY_LABEL, "Entity");
									JConsoleHandler.execute(console,fp$);
								}
							} );
							menu.add(entityItem);
						menu.addSeparator();	
						JMenuItem  resetItem = new JMenuItem("Reset");
						   resetItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									reset();
								}
							} );
						   menu.add(resetItem);
						  if(GraphHandler.undoCan(console, locator$)){
							  JMenuItem  undoItem = new JMenuItem("Undo");
							   undoItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										GraphHandler.undoPop(console, locator$);
										 init2();
											revalidate();
											repaint();
									}
								} );
							   menu.add(undoItem);
						  } 
						  
							  JMenuItem  viewsItem = new JMenuItem("Views");
							   viewsItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
									//	GraphHandler.undoPop(console, locator$);
									  showViews();
									}
								} );
							   menu.add(viewsItem);
						   
						  menu.addSeparator();	
							JMenuItem  recentItem = new JMenuItem("Put as recent");
							   recentItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										console.getRecents().put(getTitle(), getLocator());
									}
								} );
							   menu.add(recentItem);
							   menu.addSeparator();	
							   JMenuItem  unmarkItem = new JMenuItem("Unmark all");
							   unmarkItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
									unmarkAll();
									}
								} );
							   menu.add(unmarkItem);
							 
					if(hasSelectedNode()){
						menu.addSeparator();
						JMenuItem  markItem = new JMenuItem("Mark");
						   markItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								markSelectedNode();
								}
							} );
						   menu.add(markItem);
						   JMenuItem  pickItem = new JMenuItem("Pick out");
						   pickItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									markSelectedNode();
									pickOut();
								}
							} );
						   menu.add(pickItem);
						   JMenuItem  expandItem = new JMenuItem("Expand");
						   expandItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									markSelectedNode();
									expand();
								}
							} );
						   menu.add(expandItem);
					}
				    menu.addSeparator();
				    JMenuItem  saveItem = new JMenuItem("Save");
					   saveItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								saveAsPicture();
							}
						} );
					   menu.add(saveItem);	
					   JMenuItem  copyItem = new JMenuItem("Copy");
					   copyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								console.clipboard.clear();
								Entigrator entigrator=console.getEntigrator(entihome$);
								String graphLocator$=EntityHandler.getEntityLocatorAtKey(entigrator,  entityKey$);
								console.clipboard.putString(graphLocator$);
							}
						} );
					   menu.add(copyItem);	
					   menu.addSeparator();
					   JMenuItem  doneItem = new JMenuItem("Done");
					   doneItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								console.back();
								
							}
						} );
					   menu.add(doneItem);
				}

				@Override
				public void menuDeselected(MenuEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void menuCanceled(MenuEvent e) {
					// TODO Auto-generated method stub
					
				}
		   });
		  
			return menu;
	}
	  /**
		 * Get the context locator.
		 * @return the context locator.
		 */	
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JGraphRenderer.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
				if(entihome$!=null){
					locator.setProperty(Entigrator.ENTIHOME,entihome$);
					Entigrator entigrator=console.getEntigrator(entihome$);
				String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"map.png");
				if(icon$!=null)
				    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
				}
			}
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	  /**
		 * Create a new facet renderer.
		 * @param console the main console.
		 * @param locator$ the locator string.
		 * @return the fields editor.
		 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			//	System.out.println("JGraphRenderer:instantiate:locator="+locator$);
				this.console=console;
				this.locator$=locator$;
				
				Properties locator=Locator.toProperties(locator$);
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
				Entigrator entigrator=console.getEntigrator(entihome$);
				requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
	            graphEntity=entigrator.getEntityAtKey(entityKey$);
	             
	            entityLabel$=graphEntity.getProperty("label");
	            String viewComponentKey$=locator.getProperty(JGraphViews.VIEW_COMPONENT_KEY);
	            String viewKey$=locator.getProperty(JGraphViews.VIEW_KEY);
	   		    locator=new Properties();
   	   		 locator.setProperty(Locator.LOCATOR_TITLE, "Graph");
   	  	locator.setProperty(Entigrator.ENTIHOME,entihome$);
   	  	String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
   	  	if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
   	 //System.out.println("JGraphRenderer:instantiate:action="+action$);
   	  	if(JGraphViews.ACTION_SHOW_VIEW.equals(action$)){
   	  	//System.out.println("JGraphRenderer:instantiate:show view");
   	  	     try{
   	  	    	 Sack viewComponent=entigrator.getEntityAtKey(viewComponentKey$);
   	  	     
   	  	     Core[]ca=viewComponent.elementGet(viewKey$);
   	  	     if(graphEntity.existsElement("node.select"))
   	  	    graphEntity.createElement("node.select");
   	  	graphEntity.elementReplace("node.select", ca);
   	  	entigrator.save(graphEntity);
   	  	     }catch(Exception ee){
   	  	    	 Logger.getLogger(JGraphRenderer.class.getName()).info(ee.toString()); 
   	  	     }
   	  	}
   	  	displayGraph();
		}catch(Exception e){
		        Logger.getLogger(getClass().getName()).severe(e.toString());
			}
		//System.out.println("JGraphRenderer:instantiate:finish");
			return this;
			
	}
	/**
	 * Get title of the context.  
	 * @return the title of the context.
	 */	
	@Override
	public String getTitle() {
		if(message$==null)
			return "Map";
		else
			return "Map"+message$;
		
	}
	/**
	 * Get subtitle of the context.  
	 * @return the subtitle of the context.
	 */	
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	 /**
     * Get type of the  context.  
     * @return the type of the context.
     */	
	@Override
	public String getType() {
		return "graph";
	}
/**
 * Complete facet.
 */
	@Override
	public void close() {Entigrator entigrator=console.getEntigrator(entihome$);
	
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println("JGraphRenderer: mouseDragged:BEGIN");
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		//System.out.println("JGraphRenderer: mouseMoved:BEGIN");
		
	}
	
private void displayGraph(){
	try{
		//System.out.println("JGraphRenderer:displayGraph:BEGIN");
		init2();
		revalidate();
		repaint();
    
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}

private void reset(){
	try{
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		 graphEntity.removeElement("node.select");
		 GraphHandler.undoReset(console, locator$);
		 entigrator.save(graphEntity);
		 init2();
			revalidate();
			repaint();
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void pickOut(int v){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		 if(graphEntity.existsElement("node.select"))
			 graphEntity.clearElement("node.select");
			 
		 entigrator.save(graphEntity);
		 expand(v);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void pickOut(){
	Collection <Number>vc=graph.getVertices();
	final PickedState<Number> pickedState = vv.getPickedVertexState();
	for( Number n:vc){
		 if (pickedState.isPicked(n))
		    pickOut( n.intValue());
	}
}
private void expand(){
	
	try{
		Collection <Number>vc=graph.getVertices();
	//	System.out.println("JGraphRenderer:expand:vc="+vc.size());
	final PickedState<Number> pickedState = vv.getPickedVertexState();
	for( Number n:vc){
		 if (pickedState.isPicked(n))
		    expand( n.intValue());
	}
	}catch(Exception e){
		LOGGER.severe(e.toString());	
	}
}
private void expand(int v){
//	System.out.println("JGraphRenderer:expand:v="+v);
	try{
		GraphHandler.undoPush(console, locator$); 
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		 String node$=graphEntity.getElementItemAtValue("vertex", String.valueOf(v));
		 if(!graphEntity.existsElement("node.select"))
			 graphEntity.createElement("node.select");
		 graphEntity.putElementItem("node.select", new Core(null,node$,null));
		 Core[] ca=graphEntity.elementGet("bond");
				    ArrayList<String>nbl=new ArrayList<String>();
						for(Core c:ca){
							if(c.value.equals(node$))
								if(!nbl.contains(c.type))
									nbl.add(c.type);
							if(c.type.equals(node$))
								if(!nbl.contains(c.value))
										nbl.add(c.value);
						}
		  Core nodeCore;
		  String icon$;
		  String label$;
		  boolean rebuild=false;
		  for(String nb:nbl){
		           		graphEntity.putElementItem("node.select", new Core(null,nb,null));
		           	    nodeCore= graphEntity.getElementItem("node", nb);
		           	    if(nodeCore==null){
		           	    	label$=entigrator.indx_getLabel(nb);
		           	    	icon$=entigrator.ent_getIconAtKey(nb);
		           	    	if(label$!=null&&icon$!=null){
		           	    		graphEntity.putElementItem("node", new Core(icon$,nb,label$));
		           	    		rebuild=true;
		           	    	}
		           	    }
		           	} 
		 entigrator.save(graphEntity);
		 if(rebuild)
			// rebuild();
		 NodeHandler.rebuild(entigrator, entityKey$);
		 init2();
			revalidate();
			repaint();
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void hideItem(int v){
	//System.out.println("JGraphRenderer:hide:v="+v);
	try{
		GraphHandler.undoPush(console, locator$); 
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		 String node$=graphEntity.getElementItemAtValue("vertex", String.valueOf(v));
		 Core[] na=graphEntity.elementGet("node");
		 if(!graphEntity.existsElement("node.select")){
			 graphEntity.createElement("node.select");
			 na=graphEntity.elementGet("node");
		 }else{
			 na=graphEntity.elementGet("node.select");
			 graphEntity.clearElement("node.select");
		 }
		for(Core n:na){
			 if(!node$.equals(n.name))
				 graphEntity.putElementItem("node.select", new Core(null,n.name,null));
		 }
		 entigrator.save(graphEntity);
		 init2();
			revalidate();
			repaint();
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void entity(int v){
	try{
		 
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		Core[]ca=graphEntity.elementGet("vertex");
		String v$=String.valueOf(v);
		for(Core c:ca)
			if(v$.equals(c.value)){
				JEntityFacetPanel fp=new JEntityFacetPanel();
				String fp$=fp.getLocator();
				fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
				fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY, c.name);
				JConsoleHandler.execute(console, fp$);
			}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void init2(){
	//System.out.println("JGraphRenderer:init2:BEGIN");
	removeAll();
	 graph = new DirectedSparseGraph<Number,Number>();
	// final ObservableCachingLayout<Number, Number> layout =(ObservableCachingLayout< Number,Number>)vv.getGraphLayout();
		
	 Entigrator entigrator=console.getEntigrator(entihome$);
	 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
	 if(graphEntity.existsElement("vertex"))
		 graphEntity.removeElement("vertex");
	 graphEntity.createElement("vertex");
	 if(graphEntity.existsElement("edge"))
		 graphEntity.removeElement("edge");
	 graphEntity.createElement("edge");
	 String[] sa= graphEntity.elementListNoSorted("node.select");
	 if(sa==null)
			sa= graphEntity.elementListNoSorted("node");
	 //System.out.println("JGraphRenderer:init2:sa="+sa.length);
	 for (int i = 0; i <sa.length; i++) {
		 graphEntity.putElementItem("vertex", new Core(null,sa[i],String.valueOf(i)));
         graph.addVertex(i);
     }
	 entigrator.save(graphEntity);
	 Core[] va=graphEntity.elementGet("vertex");
	 int v1;
	 int v2;
	 Core[] ba=graphEntity.elementGet("bond.select");
	 boolean removeStandAloneNodes=true;
	 if(ba==null){
	    ba=graphEntity.elementGet("bond");
	    removeStandAloneNodes=false;
	 }
	 Map<Number,String> map = new HashMap<Number,String>();
	 Map<Number,Icon> iconMap = new HashMap<Number,Icon>();
	 String icon$;
	 byte[] bar;
	 ImageIcon icon;
	 for (int i = 0; i <va.length; i++) {
		 v1=Integer.parseInt(va[i].value);
		 map.put(i, graphEntity.getElementItemAt("node", va[i].name));
		 icon$=entigrator.readIconFromIcons(graphEntity.getElementItem("node",va[i].name).type);
			if(icon$!=null){
				bar=Base64.decodeBase64(icon$);
	      	  icon = new ImageIcon(bar);
	      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	      	  icon.setImage(image);
              iconMap.put(i, icon);
			}
        if(ba!=null)     
		 for(int j=0;j<ba.length;j++){
			if(va[i].name.equals(ba[j].type)){
			try{
				v2=Integer.parseInt(graphEntity.getElementItemAt("vertex", ba[j].value));
				//System.out.println("JGraphRenderer:init2:v1="+v1+" v2="+v2);
				graphEntity.putElementItem("edge", new Core(String.valueOf(v1)+"+"+String.valueOf(v2),ba[j].name,String.valueOf(j)));
				 graph.addEdge(j++, v1, v2, EdgeType.DIRECTED);
				}catch(Exception e){
					
				}
				}
		 	}
		} 
	 
	 //remove stand alone vertices
	if(removeStandAloneNodes){
	 Collection <Number>vc=graph.getVertices();
	 String nodeKey$;
	 ArrayList <Number>nl=new ArrayList<Number>();
		for( Number n:vc)
			if(graph.getOutEdges(n).size()<1 && graph.getInEdges(n).size()<1){
				nodeKey$=graphEntity.getElementItemAtValue("vertex", String.valueOf(n));
			//	System.out.println("JGraphRenderer:init:stand alone="+n);
				graphEntity.removeElementItem("vertex", nodeKey$);
				graphEntity.removeElementItem("node.select", nodeKey$);
				nl.add(n);
			}
    entigrator.save(graphEntity);
    for(Number n:nl)
    	graph.removeVertex(n);
     }
    FRLayout<Number, Number> layout = new FRLayout<Number, Number>(graph);
    layout.setMaxIterations(100);
    layout.setInitializer(new RandomLocationTransformer<Number>(new Dimension(400,400), 0));
    vv =  new VisualizationViewer<Number, Number>(layout, new Dimension(400,400));
    Transformer<Number,Paint> vpf = 
            new PickableVertexPaintTransformer<Number>(vv.getPickedVertexState(), Color.white, Color.yellow);
        vv.getRenderContext().setVertexFillPaintTransformer(vpf);
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Number>(vv.getPickedEdgeState(), Color.black, Color.cyan));

        vv.setBackground(Color.white);
        final Transformer<Number,String> vertexStringerImpl = 
                new VertexStringerImpl<Number,String>(map);
            vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl);
            vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.cyan));
            vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
            final DemoVertexIconShapeTransformer<Number> vertexIconShapeTransformer =
                    new DemoVertexIconShapeTransformer<Number>(new EllipseVertexShapeTransformer<Number>());
                
                final DemoVertexIconTransformer<Number> vertexIconTransformer =
                	new DemoVertexIconTransformer<Number>();
                
                vertexIconShapeTransformer.setIconMap(iconMap);
                vertexIconTransformer.setIconMap(iconMap);
                
                vv.getRenderContext().setVertexShapeTransformer(vertexIconShapeTransformer);
                vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
                 PickedState<Number> ps = vv.getPickedVertexState();
                ps.addItemListener(new PickWithIconListener<Number>(vertexIconTransformer));
      
                vv.setVertexToolTipTransformer(new ToStringLabeller<Number>());
        		
              
                final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
                add(panel);
                
                final DefaultModalGraphMouse<Number,Number> graphMouse = new DefaultModalGraphMouse<Number,Number>();
                vv.setGraphMouse(graphMouse);
                vv.addKeyListener(graphMouse.getModeKeyListener());
                vv.addMouseListener(new MousePopupListener());
                layoutVertices(); 
                final ScalingControl scaler = new CrossoverScalingControl();

                JButton plus = new JButton("+");
                plus.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scaler.scale(vv, 1.1f, vv.getCenter());
                    }
                });
                JButton minus = new JButton("-");
                minus.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scaler.scale(vv, 1/1.1f, vv.getCenter());
                    }
                });

                JComboBox modeBox = graphMouse.getModeComboBox();
                JPanel modePanel = new JPanel();
                modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
                modePanel.add(modeBox);
                
                JPanel scaleGrid = new JPanel(new GridLayout(1,0));
                scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
       
                JPanel controls = new JPanel();
                controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
                scaleGrid.add(plus);
                scaleGrid.add(minus);
                controls.add(scaleGrid);
           
                controls.add(modePanel);
                String[] la=null;
                //
                String[] na =graphEntity.elementListNoSorted("node.select");
                if(na==null)
                	na=graphEntity.elementListNoSorted("node");
                if(na!=null){
                	ArrayList<String>sl=new ArrayList<String>();
                	String label$;
                for	(String n:na){
                	label$=entigrator.indx_getLabel(n);
                	if(label$!=null)
                		sl.add(label$);
                }
                Collections.sort(sl);
                
                 la=sl.toArray(new String[0]);
                
                }
                //
                //System.out.println("JGraphRenderer:init:la="+la.length);
               searchBox = new AutocompleteJComboBox(la);
                JPanel searchPanel = new JPanel();
                searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
                searchPanel.add(searchBox);
                controls.add(searchPanel);
                add(controls, BorderLayout.SOUTH);
}
private boolean hasSelectedNode(){
	try{
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
	String label$=(String)searchBox.getSelectedItem();
	String nodeKey$=entigrator.indx_keyAtLabel(label$);
	if(graphEntity.getElementItem("vertex", nodeKey$)!=null)
		return true;
	}catch(Exception e){
	}
	return false;
}
private void markSelectedNode(){
	try{
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
	String label$=(String)searchBox.getSelectedItem();
	String nodeKey$=entigrator.indx_keyAtLabel(label$);
	String vertexNumber$=graphEntity.getElementItemAt("vertex", nodeKey$);
	vv.getPickedVertexState().pick(Integer.valueOf(vertexNumber$), true);
	}catch(Exception e){
	}
	
}
private void layoutVertices(){
	try{
	//System.out.println("JGraphRenderer:layoutVertices:BEGIN");
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		Core [] ca=graphEntity.elementGet("vertex");
		if(ca==null)
			return;
		int vertexId;
		final ObservableCachingLayout<Number, Number> layout =(ObservableCachingLayout< Number,Number>)vv.getGraphLayout();
	   	Collection <Number>vc=graph.getVertices();
        Core nodePointer;
        String nodeKey$;
        int nodeX;
        int nodeY;
		for(Core c:ca){
			try{
			vertexId=Integer.parseInt(c.value);
			for( Number n:vc)
				if(vertexId==n.intValue())
			    {
					try{
					nodeKey$=graphEntity.getElementItemAtValue("vertex",String.valueOf(vertexId) );
				   	nodePointer=graphEntity.getElementItem("node.select",nodeKey$);
				   	nodeX=Integer.parseInt(nodePointer.type);
				   	nodeY=Integer.parseInt(nodePointer.value);
				    layout.setLocation(n, new  Point(nodeX,nodeY));
					}catch(Exception eee){
						Logger.getLogger(JGraphRenderer.class.getName()).info(eee.toString()+": "+c.name);
					}
			    }
			}catch(Exception ee){
				Logger.getLogger(JGraphRenderer.class.getName()).info(ee.toString()+": "+c.name);
			}
		}
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
}
private void showViews(){
	try{
	//	System.out.println("JGraphRenderer:showViews:BEGIN");
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		Core [] ca=graphEntity.elementGet("vertex");
		if(ca==null)
			return;
		int vertexId;
		final ObservableCachingLayout<Number, Number> layout =(ObservableCachingLayout< Number,Number>)vv.getGraphLayout();
		Collection <Number>vc=graph.getVertices();
       Point2D	p;	
		for(Core c:ca){
			vertexId=Integer.parseInt(c.value);
			for( Number n:vc)
				if(vertexId==n.intValue())
			    {
					
					p=layout.transform(n);
					graphEntity.putElementItem("node.select", new Core(String.valueOf((int)p.getX()),c.name,String.valueOf((int)p.getY())));
			//	System.out.println("JGraphRenderer:showViews:n="+n.toString()+ " key="+c.name+" point="+ layout.transform(n) );
			   
			    }
		}
      entigrator.save(graphEntity);
      JGraphViews gvs=new JGraphViews();
	    String gvs$=gvs.getLocator();
	    gvs$=Locator.append(gvs$, Entigrator.ENTIHOME, entihome$);
	    gvs$=Locator.append(gvs$, EntityHandler.ENTITY_KEY, entityKey$);
	    gvs$=Locator.append(gvs$,JRequester.REQUESTER_ACTION,JGraphViews.ACTION_SAVE_VIEW );
	    JConsoleHandler.execute(console, gvs$);
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
	
}
private void unmarkAll(){
	Collection <Number>vc=graph.getVertices();
	for( Number n:vc)
		vv.getPickedVertexState().pick(n, false);
	
}
private void openEdge(int b){
	try{
		//System.out.println("JGraphRenderer:openEdge:b="+b);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		
		String bondKey$=graphEntity.getElementItemAtValue("edge", String.valueOf(b));
		Core core=graphEntity.getElementItem("edge", bondKey$);
		ArrayList<String>el=new ArrayList<String>();
		String v1v2$=core.type;
		//System.out.println("JGraphRenderer:openEdge:v1v2="+v1v2$);
		Core[] ca=graphEntity.elementGet("edge");
		String edgeKey$;
		for(Core c:ca )
			if(v1v2$.equals(c.type)){
				edgeKey$=graphEntity.getElementItemAt("edge.entity",c.name);
				el.add(entigrator.indx_getLabel(edgeKey$));
			}
		String[] sa=el.toArray(new String[0]);
		if(sa.length==1){
			JEntityFacetPanel fp=new JEntityFacetPanel();
			String fp$=fp.getLocator();
			fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
			fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY, entigrator.indx_keyAtLabel(sa[0]));
			JConsoleHandler.execute(console,fp$);
			return;
		}
		JEntitiesPanel jep=new JEntitiesPanel();
		String jepLocator$=jep.getLocator();
		String entitiesList$=Locator.toString(sa);
		   jepLocator$=Locator.append(jepLocator$, Entigrator.ENTIHOME, entihome$);
		   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_LIST,entitiesList$);
		   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_KEY,entityKey$);
		   JConsoleHandler.execute(console, jepLocator$);
		 
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
	
}
private void displayDetails(int b){
	try{
//		System.out.println("JGraphRenderer:displayDetails:b="+b);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		String bondKey$=graphEntity.getElementItemAtValue("edge", String.valueOf(b));
		Core core=graphEntity.getElementItem("edge", bondKey$);
		ArrayList<String>el=new ArrayList<String>();
		String v1v2$=core.type;
//		System.out.println("JGraphRenderer:displayDetails:v1v2="+v1v2$);
		Core[] ca=graphEntity.elementGet("edge");
		String edgeKey$;
		ArrayList<String>bl=new ArrayList<String>();
		for(Core c:ca )
			if(v1v2$.equals(c.type)){
				edgeKey$=graphEntity.getElementItemAt("edge.entity",c.name);
				el.add(edgeKey$);
				bl.add(c.name);
			}
		Sack edge;
        String[] sa=el.toArray(new String[0]);
		ArrayList<String>sl=new ArrayList<String>();
		for(String s:sa){
			edge=entigrator.getEntityAtKey(s);
			ca=edge.elementGet("detail");
		   if(ca==null)
			  continue;
		   for(Core c:ca)
		       for(String bk:bl){
		    	   if(bk.equals(c.type))
		    		   sl.add(entigrator.indx_getLabel(c.value));
		}
		}
		 Collections.sort(sl);	   
		  String entitiesList$=Locator.toString(sl.toArray(new String[0]));
		   JEntitiesPanel jep=new JEntitiesPanel();
		   String jepLocator$=jep.getLocator();
		   jepLocator$=Locator.append(jepLocator$, Entigrator.ENTIHOME, entihome$);
		   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_LIST,entitiesList$);
		   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_KEY,entityKey$);
		   JConsoleHandler.execute(console, jepLocator$);
		
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
	
}
private void saveAsPicture(){
	try{
		String fileName$=System.getProperty("graph.png");
		JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
	    chooser.setDialogTitle(fileName$);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	String folder$=chooser.getSelectedFile().getPath();
	    	String file$ =(String)JOptionPane.showInputDialog("File");
	    if ((file$ != null) && (file$.length() > 0)) {
    	int width = vv.getWidth();
        int height = vv.getHeight();
        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics =bi.createGraphics();
        vv.paint(graphics);
        graphics.dispose();
		File outputfile = new File(folder$+"/"+file$+".png");
		if(!outputfile.exists())
			outputfile.createNewFile();
		ImageIO.write(bi, "png", outputfile);
	    }
	    }
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
}
class MousePopupListener extends MouseAdapter {
	  boolean isPopup=false;
		public void mousePressed(MouseEvent e) {
			//System.out.println("EntityStructurePanel:MousePopupListener:mouse pressed");
			if (e.isPopupTrigger())
				isPopup=true;
			else
				isPopup=false;
				//System.out.println("JGraphRenderer:MousePopupListener:isPopup="+isPopup);
		}

	    public void mouseClicked(MouseEvent e) {
	    	if(!isPopup)
	    		return;
	    	final VisualizationViewer vv =
	                (VisualizationViewer)e.getSource();
	            final Layout layout = vv.getGraphLayout();
	            final Graph graph = layout.getGraph();
	            final Point2D p = e.getPoint();
	            v=-1;
	            b=-1;
	            final Point2D ivp = p;
	            GraphElementAccessor pickSupport = vv.getPickSupport();
	            if(pickSupport != null) {
	                Object vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
	                Object edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
	                if(vertex!=null){
	                	//System.out.println("JGraphRenderer:MousePopupListener:vertex="+vertex);
	                	v=((Integer)vertex).intValue();
	                }
	                if(edge!=null){
	                	//System.out.println("JGraphRenderer:MousePopupListener:edge="+edge);
	                	b=((Integer)edge).intValue();	
	                }
	                popup = new JPopupMenu();
	        		popup.addPopupMenuListener(new PopupMenuListener(){
	        			@Override
	        			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	        				if(v>-1){
	        				JMenuItem pickOutItem=new JMenuItem("Pick out");
	        				   popup.add(pickOutItem);
	        				   pickOutItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	        				   pickOutItem.addActionListener(new ActionListener() {
	        						@Override
	        						public void actionPerformed(ActionEvent e) {
	        							try{
	        								pickOut(v);
	        							}catch(Exception ee){
	        								Logger.getLogger(getClass().getName()).info(ee.toString());
	        							}
	        						}
	        					    });
	        				   JMenuItem expandItem=new JMenuItem("Expand");
	        				   popup.add(expandItem);
	        				   expandItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	        				   expandItem.addActionListener(new ActionListener() {
	        						@Override
	        						public void actionPerformed(ActionEvent e) {
	        							try{
	        								expand(v);
	        							}catch(Exception ee){
	        								Logger.getLogger(getClass().getName()).info(ee.toString());
	        							}
	        						}
	        					    });
	        				   JMenuItem entityItem=new JMenuItem("Entity");
	        				   popup.add(entityItem);
	        				   entityItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	        				   entityItem.addActionListener(new ActionListener() {
	        						@Override
	        						public void actionPerformed(ActionEvent e) {
	        							try{
	        								entity(v);
	        							}catch(Exception ee){
	        								Logger.getLogger(getClass().getName()).info(ee.toString());
	        							}
	        						}
	        					    });
	        				   JMenuItem hideItem=new JMenuItem("Hide");
	        				   popup.add(hideItem);
	        				   hideItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	        				   hideItem.addActionListener(new ActionListener() {
	        						@Override
	        						public void actionPerformed(ActionEvent e) {
	        							try{
	        								hideItem(v);
	        								//entity(v);
	        							}catch(Exception ee){
	        								Logger.getLogger(getClass().getName()).info(ee.toString());
	        							}
	        						}
	        					    });
	        				}
	        				if(b>-1){
	        					JMenuItem edgeItem=new JMenuItem("Edge");
		        				   popup.add(edgeItem);
		        				   edgeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		        				   edgeItem.addActionListener(new ActionListener() {
		        						@Override
		        						public void actionPerformed(ActionEvent e) {
		        							try{
		        						       openEdge(b);
		        							}catch(Exception ee){
		        								Logger.getLogger(getClass().getName()).info(ee.toString());
		        							}
		        						}
		        					    });
		        				   JMenuItem detailsItem=new JMenuItem("Details");
		        				   popup.add(detailsItem);
		        				   detailsItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		        				   detailsItem.addActionListener(new ActionListener() {
		        						@Override
		        						public void actionPerformed(ActionEvent e) {
		        							try{
		        						       displayDetails(b);
		        							}catch(Exception ee){
		        								Logger.getLogger(getClass().getName()).info(ee.toString());
		        							}
		        						}
		        					    });
		        					
	        				}
	        			}
	        			@Override
	        			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	        				// TODO Auto-generated method stub
	        			}
	        			@Override
	        			public void popupMenuCanceled(PopupMenuEvent e) {
	        				// TODO Auto-generated method stub
	        			}
	        		});
	        		popup.show(JGraphRenderer.this,(int)p.getX(),(int)p.getY());
	            }
	    	
	    }
	    public void mouseReleased(MouseEvent e) {
	    	
	    	if(!isPopup)
		    	if (e.isPopupTrigger()) 
			    	  isPopup=true;
	    		//System.out.println("JGraphRenderer:MousePopupListener:is Popup");
	    	}
	   }
@Override
public void activate() {
	// TODO Auto-generated method stub
	
}
}


