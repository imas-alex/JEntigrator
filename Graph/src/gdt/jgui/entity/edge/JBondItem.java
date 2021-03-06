package gdt.jgui.entity.edge;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Logger;


import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import gdt.data.entity.BaseHandler;

import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.bonddetail.JBondDetailPanel;
import gdt.jgui.entity.node.JNodeFacetAddItem;
import gdt.jgui.entity.node.JNodeFacetOpenItem;
/**
 *This class represents a bond item in the list.
 * @author imasa
 *
 */ 
public class JBondItem extends JItemPanel{
	private static final long serialVersionUID = 1L;
	private boolean isCommitted=false;
	private int pos=0;
	boolean debug=false;
	String entihome$;
	String entityKey$;
	String bondKey$;
	String edgeKey$;
	String bondInNodeKey$;
	String bondOutNodeKey$;
	public JBondItem(){
		super();
		title$="bond"; 
	}
	
	//public JBondItem(final JMainConsole console,final String locator$){
	public void setIcon(String icon$){
		this.icon$=icon$;
	}
	/**
	 * The constructor.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	public JItemPanel instantiate(final JMainConsole console,final String locator$){
		this.console=console;
		this.locator$=locator$;
		//super(console,locator$);
		if(debug)
			System.out.println("JBondItem:instantiate:locator="+locator$);
	//		System.out.println("JBondItem:is committed="+isCommitted);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	//	String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
		bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
		edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
		bondInNodeKey$=locator.getProperty(JBondsPanel.BOND_IN_NODE_KEY);
		bondOutNodeKey$=locator.getProperty(JBondsPanel.BOND_OUT_NODE_KEY);
		title.setText(title$);
		title$=locator.getProperty(Locator.LOCATOR_TITLE);
		if(title$!=null)
			title.setText(title$);
	//	System.out.println("JBondItem:bond key"+bondKey$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		if(debug)
			System.out.println("JBondItem:instantiate:1");
		//if(isCommitted){
		if(icon$!=null)
		  icon$= ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"bond.png");
		//}else{
		 // icon$= ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"draft.png");
		//}
		if(debug)
			System.out.println("JBondItem:instantiate:icon loaded");
		resetIcon();
		
	//	JBondsPanel.saveSelection( console, entihome$, edgeKey$, bondKey$);
		popup = new JPopupMenu();
		if(debug)
			System.out.println("JBondItem:make popup menu");
		popup.addPopupMenuListener(new PopupMenuListener(){

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			//	System.out.println("JBondsItem:popup will become visible");
				pos= JBondItem.this.getBounds().y;
			//	System.out.println("JBondsItem:position="+ JBondItem.this.getBounds().y);
				popup.removeAll();
				isCommitted=isCommitted(console,locator$);
				Properties locator=Locator.toProperties(locator$);
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				Entigrator entigrator=console.getEntigrator(entihome$);
				if(isCommitted){
					   icon$= ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"bond.png");
					}else{
					  icon$= ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"draft.png");
					}
					
					resetIcon();
				// System.out.println("JBondsItem:popup will become visible:is commited="+isCommitted);
			
		JMenuItem openOutItem=new JMenuItem("Open out node");
		   popup.add(openOutItem);
		   openOutItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openOutItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					//	System.out.println("JBondsItem:popup.locator="+locator$);
						Properties locator=Locator.toProperties(locator$);
						String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
						String outNodeKey$=locator.getProperty(JBondsPanel.BOND_OUT_NODE_KEY);
					    
						if(outNodeKey$==null){
							Entigrator entigrator=console.getEntigrator(entihome$);
					    Sack host=entigrator.getEntityAtKey(entityKey$);
					    Core bond=host.getElementItem("bond", bondKey$);
					    outNodeKey$=bond.type;
						}
						JEntityFacetPanel fp=new JEntityFacetPanel();
						String fpLocator$=fp.getLocator();
						fpLocator$=Locator.append(fpLocator$,Entigrator.ENTIHOME, entihome$);
						fpLocator$=Locator.append(fpLocator$,EntityHandler.ENTITY_KEY, outNodeKey$);
						JConsoleHandler.execute(console,fpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		   JMenuItem openInItem=new JMenuItem("Open in node");
		   popup.add(openInItem);
		   openInItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openInItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						Properties locator=Locator.toProperties(locator$);
						String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
						String inNodeKey$=locator.getProperty(JBondsPanel.BOND_IN_NODE_KEY);
					    
						if(inNodeKey$==null){
							Entigrator entigrator=console.getEntigrator(entihome$);
					    Sack host=entigrator.getEntityAtKey(entityKey$);
					    Core bond=host.getElementItem("bond", bondKey$);
					    inNodeKey$=bond.value;
						}
							JEntityFacetPanel fp=new JEntityFacetPanel();
						String fpLocator$=fp.getLocator();
						fpLocator$=Locator.append(fpLocator$,Entigrator.ENTIHOME, entihome$);
						fpLocator$=Locator.append(fpLocator$,EntityHandler.ENTITY_KEY, inNodeKey$);
						JConsoleHandler.execute(console,fpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		   JMenuItem openEdgeItem=new JMenuItem("Open edge");
		   popup.add(openEdgeItem);
		   openEdgeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openEdgeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						Properties locator=Locator.toProperties(locator$);
						String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
	                   String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
					    
						if(edgeKey$==null){
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack host=entigrator.getEntityAtKey(entityKey$);
							Core bond=host.getElementItem("bond", bondKey$);
							if(isHostedByEdge())
								edgeKey$=entityKey$;
							if(isHostedByNode())
								edgeKey$=host.getElementItemAt("edge", bond.name);
						}
						JEntityFacetPanel fp=new JEntityFacetPanel();
						String fpLocator$=fp.getLocator();
						fpLocator$=Locator.append(fpLocator$,Entigrator.ENTIHOME, entihome$);
						fpLocator$=Locator.append(fpLocator$,EntityHandler.ENTITY_KEY, edgeKey$);;
						JConsoleHandler.execute(console,fpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		JMenuItem deleteItem=new JMenuItem("Delete");
		   popup.add(deleteItem);
		   deleteItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   deleteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						 int response = JOptionPane.showConfirmDialog(JBondItem.this, "Delete bond ?", "Confirm",
							       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							   if (response != JOptionPane.YES_OPTION) 
							       return;
						Properties locator=Locator.toProperties(locator$);
						String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
					    Entigrator entigrator=console.getEntigrator(entihome$);
						Sack host=entigrator.getEntityAtKey(entityKey$);
						Core bond=host.getElementItem("bond", bondKey$);
						Sack outNode=entigrator.getEntityAtKey(bond.type);
						Sack inNode=entigrator.getEntityAtKey(bond.value);
						if(outNode!=null)
							outNode.removeElementItem("bond", bondKey$);
						if(inNode!=null)
							inNode.removeElementItem("bond", bondKey$);
						host.removeElementItem("bond",bondKey$ );
						entigrator.ent_alter(host);
						if(outNode!=null)
							entigrator.ent_alter(outNode);
						if(inNode!=null)
							entigrator.ent_alter(inNode);
						JBondsPanel bp=new JBondsPanel();
						String bpLocator$=bp.getLocator();
						bpLocator$=Locator.append(bpLocator$,Entigrator.ENTIHOME, entihome$);
						bpLocator$=Locator.append(bpLocator$,EntityHandler.ENTITY_KEY, entityKey$);
						bpLocator$=Locator.append(bpLocator$,JItemsListPanel.POSITION, String.valueOf(pos));
						JConsoleHandler.execute(console,bpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		  if(isHostedByEdge()){
			
		   String nodeKey$=getNodeKeyFromClipboard();
		   if(nodeKey$!=null&&canSetNode()){
			   popup.addSeparator();  
		   JMenuItem outNodeItem=new JMenuItem("Set out node");
		   popup.add(outNodeItem);
		   outNodeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   outNodeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					
					Properties locator=Locator.toProperties(locator$);
					String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
				    String nodeKey$=getNodeKeyFromClipboard();
				    Entigrator entigrator=console.getEntigrator(entihome$);
					Sack edge=entigrator.getEntityAtKey(entityKey$);
					Core bond=edge.getElementItem("bond", bondKey$);
					if (bond==null)
						bond=new Core(nodeKey$,bondKey$,null);
					else
						bond.type=nodeKey$;
					edge.putElementItem("bond",bond );
					entigrator.ent_alter(edge);
					JBondsPanel bp=new JBondsPanel();
					String bpLocator$=bp.getLocator();
					bpLocator$=Locator.append(bpLocator$,Entigrator.ENTIHOME, entihome$);
					bpLocator$=Locator.append(bpLocator$,EntityHandler.ENTITY_KEY, entityKey$);
					bpLocator$=Locator.append(bpLocator$,JItemsListPanel.POSITION, String.valueOf(pos));
					JConsoleHandler.execute(console,bpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
				
			    });
		   JMenuItem inNodeItem=new JMenuItem("Set in node");
		   popup.add(inNodeItem);
		   inNodeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   inNodeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					
					Properties locator=Locator.toProperties(locator$);
					String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
				    String nodeKey$=getNodeKeyFromClipboard();
				    Entigrator entigrator=console.getEntigrator(entihome$);
					Sack edge=entigrator.getEntityAtKey(entityKey$);
					Core bond=edge.getElementItem("bond", bondKey$);
					if (bond==null)
						bond=new Core(null,bondKey$,nodeKey$);
					else
						bond.value=nodeKey$;
					edge.putElementItem("bond",bond );
					entigrator.ent_alter(edge);
					JBondsPanel bp=new JBondsPanel();
					String bpLocator$=bp.getLocator();
					bpLocator$=Locator.append(bpLocator$,Entigrator.ENTIHOME, entihome$);
					bpLocator$=Locator.append(bpLocator$,EntityHandler.ENTITY_KEY, entityKey$);
					bpLocator$=Locator.append(bpLocator$,JItemsListPanel.POSITION, String.valueOf(pos));
					JConsoleHandler.execute(console,bpLocator$ );
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
				
			    });
		   } 
		   
		   if(!isCommitted&&canBeCommitted()){
			   JMenuItem commitItem=new JMenuItem("Commit");
			   popup.add(commitItem);
			   commitItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   commitItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
						
						Properties locator=Locator.toProperties(locator$);
						String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
					    Entigrator entigrator=console.getEntigrator(entihome$);
						Sack edge=entigrator.getEntityAtKey(entityKey$);
						Core bond=edge.getElementItem("bond", bondKey$);
						Sack outNode=entigrator.getEntityAtKey(bond.type);
						Sack inNode=entigrator.getEntityAtKey(bond.value);
						if(inNode==null||outNode==null){
							System.out.println("JBondItem:commit:broken bond="+bondKey$);
							return;
						}
						if(!outNode.existsElement("bond"))
							outNode.createElement("bond");
						outNode.putElementItem("bond", bond);
						if(!outNode.existsElement("edge"))
							outNode.createElement("edge");
						outNode.putElementItem("edge", new Core(null,bondKey$,edge.getKey()));
						if(!inNode.existsElement("bond"))
							inNode.createElement("bond");
						inNode.putElementItem("bond", bond);
						if(!inNode.existsElement("edge"))
							inNode.createElement("edge");
						inNode.putElementItem("edge", new Core(null,bondKey$,edge.getKey()));
						if(!outNode.existsElement("fhandler"))
							outNode.createElement("fhandler");
						outNode.putElementItem("fhandler", new Core(null, NodeHandler.class.getName(),NodeHandler.EXTENSION_KEY));
						if(!inNode.existsElement("fhandler"))
							inNode.createElement("fhandler");
						inNode.putElementItem("fhandler", new Core(null, NodeHandler.class.getName(),NodeHandler.EXTENSION_KEY));
						if(!outNode.existsElement("jfacet"))
							outNode.createElement("jfacet");
						outNode.putElementItem("jfacet", new Core(JNodeFacetAddItem.class.getName(), NodeHandler.class.getName(),JNodeFacetOpenItem.class.getName()));
						
						if(!inNode.existsElement("fhandler"))
							inNode.createElement("fhandler");
						inNode.putElementItem("fhandler", new Core(null, NodeHandler.class.getName(),NodeHandler.EXTENSION_KEY));
						if(!inNode.existsElement("jfacet"))
							inNode.createElement("jfacet");
						inNode.putElementItem("jfacet", new Core(JNodeFacetAddItem.class.getName(), NodeHandler.class.getName(),JNodeFacetOpenItem.class.getName()));
					
						entigrator.ent_alter(outNode);
						entigrator.ent_alter(inNode);
						entigrator.ent_assignProperty(outNode, "node",outNode.getProperty("label"));
						entigrator.ent_assignProperty(inNode, "node",inNode.getProperty("label"));
						JBondsPanel bp=new JBondsPanel();
						String bpLocator$=bp.getLocator();
						bpLocator$=Locator.append(bpLocator$,Entigrator.ENTIHOME, entihome$);
						bpLocator$=Locator.append(bpLocator$,EntityHandler.ENTITY_KEY, entityKey$);
						bpLocator$=Locator.append(bpLocator$,JItemsListPanel.POSITION, String.valueOf(pos));
						JConsoleHandler.execute(console,bpLocator$ );
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
					}
					
				    });
		   }
		  }
//		  System.out.println("JBondItem:finish");
	
			
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
		return this;
	}
	
	private String getNodeKeyFromClipboard(){
		try{
		String[] sa=console.clipboard.getContent();
		if(sa==null||sa.length<1)
			return null;
		Properties clipLocator;
		String entityKey$;
		for(String s:sa){
			clipLocator=Locator.toProperties(s);
			entityKey$=clipLocator.getProperty(EntityHandler.ENTITY_KEY);
			if(entityKey$!=null)
				return entityKey$;
		}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
}
	/**
	 * Detect if the bond is committed.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return true if is committed false otherwise.
	 */
	public static boolean isCommitted(JMainConsole console,String locator$){
		try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
		String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack edge=null;
		if(edgeKey$!=null)
			edge=entigrator.getEntityAtKey(edgeKey$);
		else{
		if(entityKey$!=null){	
		Sack host=entigrator.getEntityAtKey(entityKey$);
		if(host!=null&&	"edge".equals(host.getProperty("entity")))
			edge=host;
		}
		}
		if(edge==null){
			System.out.println("JBondsItem:isCommitted:edge not found");
			return false;
		}
		Core bond=edge.getElementItem("bond", bondKey$);
		Sack outNode=entigrator.getEntityAtKey(bond.type);
		Sack inNode=entigrator.getEntityAtKey(bond.value);
		Core outBond=outNode.getElementItem("bond", bondKey$);
		if(outBond==null){
			System.out.println("JBondsItem:isCommitted:out bond not found");
			return false;
		}
		Core inBond=inNode.getElementItem("bond", bondKey$);
		if(inBond==null){
			System.out.println("JBondsItem:isCommitted:out bond not found");
			  return false;
		}
		if(!outBond.type.equals(bond.type))
			return false;
		if(!outBond.value.equals(bond.value))
			return false;
		if(!outBond.value.equals(inBond.value))
			return false;
		if(!outBond.type.equals(inBond.type))
			return false;
		if(!inBond.value.equals(bond.value))
			return false;
		if(!inBond.type.equals(bond.type))
			return false;
		return true;
	}catch(Exception e){
		Logger.getLogger(JBondItem.class.getName()).severe(e.toString());
	
	}
		return false;
}
	private boolean canBeCommitted(){
		try{
		Properties locator=Locator.toProperties(JBondItem.this.locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack edge=entigrator.getEntityAtKey(entityKey$);
		Core bond=edge.getElementItem("bond", bondKey$);
		if(bond!=null&&bond.type!=null&&bond.value!=null&&!bond.type.equals(bond.value))
			return true;
	  	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	
	}
		return false;
}
	private boolean canSetNode(){
		try{
		String[] sa=console.clipboard.getContent();
		if(sa==null)
			return false;
		Properties locator;
		for(String s:sa){
			locator=Locator.toProperties(s);
			if(locator.getProperty(EntityHandler.ENTITY_KEY)!=null)
					return true;
		}
	  	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	
	}
		return false;
}
	private boolean isHostedByEdge(){
		try{
			Properties locator=Locator.toProperties(JBondItem.this.locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack host=entigrator.getEntityAtKey(entityKey$);
			if(host.getProperty("edge")!=null)
					return true;
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
			return false;
	}
	private boolean isHostedByNode(){
		try{
			Properties locator=Locator.toProperties(JBondItem.this.locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack host=entigrator.getEntityAtKey(entityKey$);
			if(host.getProperty("node")!=null)
					return true;
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
			return false;
	}
	@Override
	public String getLocator() {
		if(debug)
			System.out.println("JBondItem:getLocator.BEGIN");
		    Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,"bond item");
		    if(debug)
				System.out.println("JBondItem:getLocator.0");
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(debug)
				System.out.println("JBondItem:getLocator.1");
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"bond.png");
			locator.setProperty(Locator.LOCATOR_ICON_LOCATION,EdgeHandler.EXTENSION_KEY);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    if(bondKey$!=null)
			       locator.setProperty(JBondsPanel.BOND_KEY,bondKey$);
		    if(edgeKey$!=null)
			       locator.setProperty(JBondsPanel.EDGE_KEY,edgeKey$);
		    if(bondInNodeKey$!=null)
			       locator.setProperty(JBondsPanel.BOND_IN_NODE_KEY,bondInNodeKey$);
		    if(bondOutNodeKey$!=null)
			       locator.setProperty(JBondsPanel.BOND_OUT_NODE_KEY,bondOutNodeKey$);
		  
		    if(debug)
				System.out.println("JBondItem:getLocator.2");
		    locator.setProperty(Locator.LOCATOR_TITLE,title$);
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   //locator.setProperty(BaseHandler.HANDLER_CLASS,JBondItem.class.getName());
		   locator.setProperty(BaseHandler.HANDLER_CLASS,JBondDetailPanel.class.getName());
		   
		   if(debug)
				System.out.println("JBondItem:getLocator.locator="+Locator.toString(locator));
		   return Locator.toString(locator);
	}
}
