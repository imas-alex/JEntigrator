package gdt.jgui.entity.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.edge.JBondsPanel;
import gdt.jgui.tool.JTextEditor;

public class JGraphRenderer extends JPanel implements JContext , JFacetRenderer,JRequester{
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private static final String ACTION_CREATE_GRAPH="action create graph";
	private JMainConsole console;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    private Sack graph;
    String requesterResponseLocator$;
	@Override
	public void response(JMainConsole console, String locator$) {
		System.out.println("JGraphrenderer:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
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
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
		
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return locator$;
	}

	@Override
	public String getFacetHandler() {
		return GraphHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "graph";
	}

	@Override
	public String getCategoryIcon() {
try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		return ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
}catch(Exception e){
	LOGGER.severe(e.toString());
	return null;
}
	}

	@Override
	public String getCategoryTitle() {
		return "Graphs";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			GraphHandler graphHandler=new GraphHandler();
			graphHandler.instantiate(entityLocator$);
			graphHandler.adaptClone(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			GraphHandler graphHandler=new GraphHandler();
			graphHandler.instantiate(entityLocator$);
			graphHandler.adaptRename(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}

	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		try{	
	    	String graphHandler$=GraphHandler.class.getName();
	    	if(entity.getElementItem("fhandler", graphHandler$)!=null){
				entity.putElementItem("jfacet", new Core(null,graphHandler$,JGraphFacetOpenItem.class.getName()));
				entigrator.save(entity);
			}
	    }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
	    }
		
	}

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New graph");
		    String text$="NewGraph"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JGraphRenderer gr=new JGraphRenderer();
		    String grLocator$=gr.getLocator();
		    grLocator$=Locator.append(grLocator$, Entigrator.ENTIHOME,entihome$);
		    grLocator$=Locator.append(grLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    grLocator$=Locator.append(grLocator$, BaseHandler.HANDLER_METHOD,"response");
		    grLocator$=Locator.append(grLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_GRAPH);
		    String requesterResponseLocator$=Locator.compressText(grLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
			
		}
		return null;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public JMenu getContextMenu() {
		JMenu	menu=new JMenu("Context");
		   menu.setName("Context");
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
			
			return menu;
	}

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
				String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
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

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
				System.out.println("JGraphRenderer:instantiate:locator="+locator$);
				this.console=console;
				if(console==null)
					System.out.println("JGraphRenderer:instantiate:consoleis null");
				Properties locator=Locator.toProperties(locator$);
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Entigrator entigrator=console.getEntigrator(entihome$);
				requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
	            graph=entigrator.getEntityAtKey(entityKey$);
	            entityLabel$=graph.getProperty("label");
	   		    locator=new Properties();
   	   		 locator.setProperty(Locator.LOCATOR_TITLE, "Graph");
   	  	locator.setProperty(Entigrator.ENTIHOME,entihome$);
   	  	String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
   	  	if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			}catch(Exception e){
		        Logger.getLogger(getClass().getName()).severe(e.toString());
			}
			return this;
	}

	@Override
	public String getTitle() {
		return "Graph";
	}

	@Override
	public String getSubtitle() {
		return entityLabel$;
	}

	@Override
	public String getType() {
		return "graph";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
