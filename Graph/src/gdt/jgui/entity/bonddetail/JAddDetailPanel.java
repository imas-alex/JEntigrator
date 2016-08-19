package gdt.jgui.entity.bonddetail;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;

/**
 * Displays the list of bond details.
 * Provides menu to manage bond details.
 * @author imasa
 *
 */

public class JAddDetailPanel extends JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(JAddDetailPanel.class.getName());
	protected String entihome$;
	protected String entityKey$;
	protected String bondKey$;
	protected JMenuItem addItem;
	protected String requesterResponseLocator$;
	Hashtable<String,JItemPanel> items;
/**
 * The default constructor.
 */
    public JAddDetailPanel (){
        super();	
    }
    
  /**
   * Get context menu.
   * @return the context menu.
   * 
   */
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		mia=null;
		 int cnt=menu.getItemCount();
		 if(cnt>0){
			 mia=new JMenuItem[cnt];
			for(int i=0;i<cnt;i++) 
			 mia[i]=menu.getItem(i);
		 }
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
				menu.removeAll();
				if(mia!=null)
					 for(JMenuItem mi:mia)
					try{
			  			 if(mi!=null) 
			  			 menu.add(mi);
			  			}catch(Exception ee){
			  				 System.out.println("JAddDetailPanel:getConextMenu:"+ee.toString());
			  			}
			    
				
			
				JMenuItem doneItem = new JMenuItem("Done");
				doneItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						if(requesterResponseLocator$!=null){
							try{
							   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
							   String responseLocator$=new String(ba,"UTF-8");
							   JConsoleHandler.execute(console, responseLocator$);
								}catch(Exception ee){
									LOGGER.severe(ee.toString());
								}
						}else
						  console.back();
						
					}
					
				});
	add(doneItem);
	
		if(hasSelectedItems()){
			
			addItem = new JMenuItem("Add");
			addItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//removeComponents();
				}
			} );
			menu.add(addItem);
			} 
			}
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			@Override
			public void menuCanceled(MenuEvent e) {
			}	
	});
		return menu;	
	}
/**
 * Get the context locator.
 *  @return the context locator.
 */
	@Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null){
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		       Entigrator entigrator=console.getEntigrator(entihome$);
		       String icon$=ExtensionHandler.loadIcon(entigrator, BondDetailHandler.EXTENSION_KEY, "detail.png");
		       if(icon$!=null)
			    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		        	   
		    }
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		   locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		   
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,JAddDetailPanel.class.getName());
		    locator.setProperty(BaseHandler.HANDLER_LOCATION,BondDetailHandler.EXTENSION_KEY);
		    return Locator.toString(locator);
	}
/**
 * Create the context
 * @param console the main console.
 * @param locator$ the locator string.
 * @return an instance of the JAddDetailPanel.
 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			 this.console=console;
			 this.locator$=locator$;
			 
			 Properties locator=Locator.toProperties(locator$);
			
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
				
			 Entigrator entigrator=console.getEntigrator(entihome$);
			 FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
			 if(fha!=null){
				 JFacetRenderer facetRenderer;
				 Properties cpLocator;
				 String cpLocator$;
				 JItemPanel itemPanel;
				
				  cpLocator$=getLocator();
				  
//				  System.out.println("AllCategoriesPanel:instantiate:cpLocator="+cpLocator$);
				  cpLocator=Locator.toProperties(cpLocator$);
				  cpLocator.setProperty(BaseHandler.HANDLER_METHOD, "response");
				  //cpLocator.setProperty(Entigrator.ENTIHOME,entihome$);
				  
				// System.out.println("AllCategoriesPanel:instantiate:BEGIN MAKE CATEGORY PANELS");
				 String fh$;
				 boolean skip;
				 for(FacetHandler fh:fha){
					 try{
				  skip=false;		 
				  fh$=fh.getClassName();
				  for(String nd$:notDetail){
					  if(nd$.equals(fh$)){
						  skip=true;
						  break;
					  }
				  }
				  if(skip)
					  continue;
				//  System.out.println("AllCategoriesPanel:instantiate:fh="+fh.getClass().getName());		 
				  facetRenderer=(JFacetRenderer)JConsoleHandler.getFacetRenderer(entigrator, fh$);
					String frLocator$=facetRenderer.getLocator();
					frLocator$=Locator.append(frLocator$, Entigrator.ENTIHOME, entihome$);
					facetRenderer.instantiate(console,frLocator$ );
					
					cpLocator$=Locator.append(cpLocator$, Locator.LOCATOR_ICON,facetRenderer.getCategoryIcon());
					cpLocator$=Locator.append(cpLocator$, Locator.LOCATOR_TITLE,facetRenderer.getCategoryTitle());
					cpLocator$=Locator.append(cpLocator$, JFacetOpenItem.FACET_HANDLER_CLASS,fh$);
					cpLocator$=Locator.append(cpLocator$,BaseHandler.HANDLER_CLASS,JAddDetailPanel.class.getName());
					cpLocator$=Locator.append(cpLocator$,BaseHandler.HANDLER_LOCATION,BondDetailHandler.EXTENSION_KEY);
					cpLocator$=Locator.append(cpLocator$,BaseHandler.HANDLER_METHOD,"response");
					   
					itemPanel=new JItemPanel(console,cpLocator$);
				  ipl.add(itemPanel); 
	     		   }catch(Exception e){
	     				Logger.getLogger(getClass().getName()).info(e.toString());
	     			}	 
				 }
			 }
		//	 System.out.println("AllCategoriesPanel:instantiate:END MAKE CATEGORY PANELS");
				
			Collections.sort(ipl,new ItemPanelComparator()); 
			putItems(ipl.toArray(new JItemPanel[0]));
			return this;
        }catch(Exception e){
        
        LOGGER.severe(e.toString());
        }
        return null;
        }
	String prefix$="gdt.data.entity";
	String[] notDetail=new String[]{
	BondDetailHandler.class.getName(),
	EdgeHandler.class.getName(),
	GraphHandler.class.getName(),
	NodeHandler.class.getName(),
	ExtensionHandler.class.getName()
	
	
	};
	@Override
/**
 * Get the context title.
 * @return the context title.	
 */
public String getTitle() {
		String title$= "Add detail";
	
		return title$;
	}
	/**
	 * Get the context type.
	 * @return the context type.	
	 */
	@Override
	public String getType() {
		return "Add bond details";
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	/**
	 * Get the context subtitle.
	 * @return the context subtitle.	
	 */
	@Override
	public String getSubtitle() {
		String subtitle$=null;
		try{
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  subtitle$=entigrator.getBaseName();
			  if(entityKey$!=null){
				  subtitle$=entigrator.indx_getLabel(entityKey$);
			  }
			}catch(Exception e){
			}
		return subtitle$;
	}
	/**
	 * Response on call from the other context.
	 *	@param console main console
	 *  @param locator$ action's locator 
	 */

		@Override
		public void response(JMainConsole console, String locator$) {
//	System.out.println("JAddDetailPanel:response.BEGIN");
//		System.out.println("JAddDetailPanel:response:locator="+locator$);	
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String facetHandler$=locator.getProperty(JFacetOpenItem.FACET_HANDLER_CLASS);
			Entigrator entigrator=console.getEntigrator(entihome$);
			JFacetRenderer fr=JConsoleHandler.getFacetRenderer(entigrator, facetHandler$);
			String frLocator$=fr.getLocator();
			frLocator$=Locator.append(frLocator$, Entigrator.ENTIHOME, entihome$);
			fr.instantiate(console, frLocator$);
			String detail$=fr.newEntity(console, frLocator$);
			Sack detail=entigrator.getEntity(detail$);
	//		System.out.println("JAddDetailPanel:response:detail="+detail.getProperty("label"));
		}catch(Exception e){
			Logger.getLogger(JAddDetailPanel.class.getName()).severe(e.toString());
		}
		}
}
