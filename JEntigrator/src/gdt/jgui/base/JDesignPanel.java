package gdt.jgui.base;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JClipboard;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JEntityFacetPanel;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * This class displays the general-purpose low-level
 * database management control.
 * 
 * @author imasa
 *
 */

public class JDesignPanel extends JPanel implements JContext{

	private static final long serialVersionUID = 1L;
	private static final String VALUE_MODE="Auto value mode";
	private static final String PROPERTY_MODE="Auto property mode";
	public static final String ASSIGN_MODE="Assign mode";
	private static final String INCLUDE_MODE="Include mode";
	/**
	 * The tag of the property name
	 */
	public static final String PROPERTY_NAME="property name";
	/**
	 * The tag of the property value
	 */
	public static final String PROPERTY_VALUE="property value";
	/**
	 * The tag of the list of containers.
	 */
	public static final String CONTAINERS_LIST="containers list";
	/**
	 * The tag of the container label.
	 */
	public static final String CONTAINER_LABEL="container label";
	/**
	 * The tag of the context mode.
	 */
	public static final String MODE="mode";
	private String entihome$;
	private String mode$=VALUE_MODE;
	private JTextField modeField;
	private JComboBox<String> propertyComboBox; 
	private JComboBox<String> valueComboBox;
	private JComboBox<String> entityComboBox;
	private JComboBox<String> containerComboBox;
	private Logger LOGGER =Logger.getLogger(JDesignPanel.class.getName());
	private JMainConsole console;
	/**
	 * The default constructor.
	 */
	public JDesignPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		JLabel lblProperty = new JLabel("Property");
		lblProperty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			      showPropertyPanel();
			}
		});
		GridBagConstraints gbc_lblProperty = new GridBagConstraints();
		gbc_lblProperty.insets = new Insets(5, 5, 5, 5);
		gbc_lblProperty.gridx = 0;
		gbc_lblProperty.gridy = 0;
		gbc_lblProperty.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblProperty, gbc_lblProperty);
		
		propertyComboBox = new JComboBox<String>();
		GridBagConstraints gbc_propertyComboBox = new GridBagConstraints();
		gbc_propertyComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_propertyComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_propertyComboBox.gridx = 1;
		gbc_propertyComboBox.gridy = 0;
		gbc_propertyComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(propertyComboBox, gbc_propertyComboBox);

		JLabel lblValue = new JLabel("Value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.insets = new Insets(5, 5, 5, 5);
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 1;
		gbc_lblValue.anchor=GridBagConstraints.FIRST_LINE_START;
		lblValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			   showValuePanel();
			}
		});
		add(lblValue, gbc_lblValue);

		valueComboBox = new JComboBox<String>();
		GridBagConstraints gbc_valueComboBox = new GridBagConstraints();
		gbc_valueComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_valueComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_valueComboBox.gridx = 1;
		gbc_valueComboBox.gridy = 1;
		gbc_valueComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(valueComboBox, gbc_valueComboBox);
		
		JLabel lblEntity = new JLabel("Entity");
		lblEntity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			   showEntityPanel();
			}
		});
		GridBagConstraints gbc_lblEntity = new GridBagConstraints();
		gbc_lblEntity.insets = new Insets(5, 5, 5, 5);
		gbc_lblEntity.gridx = 0;
		gbc_lblEntity.gridy = 2;
		gbc_lblEntity.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblEntity, gbc_lblEntity);

		entityComboBox = new JComboBox<String>();
		GridBagConstraints gbc_entityComboBox = new GridBagConstraints();
		gbc_entityComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_entityComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_entityComboBox.gridx = 1;
		gbc_entityComboBox.gridy = 2;
		gbc_entityComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(entityComboBox, gbc_entityComboBox);
		
		JLabel lblContainer = new JLabel("Container");
		GridBagConstraints gbc_lblContainer = new GridBagConstraints();
		gbc_lblContainer.insets = new Insets(5, 5, 5, 5);
		gbc_lblContainer.gridx = 0;
		gbc_lblContainer.gridy = 3;
		gbc_lblContainer.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblContainer, gbc_lblContainer);
		lblContainer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			   showContainersPanel();
			}
		});
		containerComboBox = new JComboBox<String>();
		GridBagConstraints gbc_containerComboBox = new GridBagConstraints();
		gbc_containerComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_containerComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_containerComboBox.gridx = 1;
		gbc_containerComboBox.gridy = 3;
		gbc_containerComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(containerComboBox, gbc_containerComboBox);
		
		JLabel lblMode = new JLabel("Mode");
		GridBagConstraints gbc_lblMode = new GridBagConstraints();
		gbc_lblMode.gridx = 0;
		gbc_lblMode.gridy = 4;
		gbc_lblMode.insets = new Insets(5, 5, 5, 5);
		gbc_lblMode.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblMode, gbc_lblMode);
		
		modeField = new JTextField();
		modeField.setEditable(false);
		GridBagConstraints gbc_modeField = new GridBagConstraints();
		gbc_modeField.insets = new Insets(0, 0, 5, 5);
		gbc_modeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modeField.gridx = 1;
		gbc_modeField.gridy = 4;
		add(modeField, gbc_modeField);
		modeField.setColumns(10);
		modeField.setText(mode$);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 1.0;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx =0;
		gbc_panel.gridy = 5;
		add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		propertyComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					String propertyName$=(String)propertyComboBox.getSelectedItem();
					setValues(propertyName$);
					if(PROPERTY_MODE.equals(mode$))
						selectEntitiesAtProperty();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		valueComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					if(VALUE_MODE.equals(mode$)||INCLUDE_MODE.equals(mode$)){
						selectEntitiesAtPropertyValue();
					if(!INCLUDE_MODE.equals(mode$))
							listContainers();
					}
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		entityComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					if(!INCLUDE_MODE.equals(mode$))
						listContainers();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
	}
private void setProperties(){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listPropertyNames();
		Support.sortStrings(sa);
		DefaultComboBoxModel<String> comboBoxModel=new DefaultComboBoxModel<String>();
		for(String aSa:sa)
			comboBoxModel.addElement(aSa);
		propertyComboBox.setModel(comboBoxModel);
		if(sa!=null&&sa.length>0){
		String[] vSa=entigrator.indx_listPropertyValues(sa[0]);
		if(vSa!=null){
		Support.sortStrings(vSa);
		DefaultComboBoxModel<String> vComboBoxModel=new DefaultComboBoxModel<String>();
		for(String aSa:vSa)
			vComboBoxModel.addElement(aSa);
				valueComboBox.setModel(vComboBoxModel);
		}
		if(VALUE_MODE.equals(mode$))
			selectEntitiesAtPropertyValue();
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void setValues(String propertyName$){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listPropertyValues(propertyName$);
		DefaultComboBoxModel<String> comboBoxModel=new DefaultComboBoxModel<String>();
		if(sa==null||sa.length<1){
			valueComboBox.setModel(comboBoxModel);
			if(VALUE_MODE.equals(mode$))
				entityComboBox.setModel(comboBoxModel);
			return;
		}
		Support.sortStrings(sa);
		for(String aSa:sa)
			comboBoxModel.addElement(aSa);
		valueComboBox.setModel(comboBoxModel);
		if(VALUE_MODE.equals(mode$))
			selectEntitiesAtPropertyValue();
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
	}	
}
private void selectEntitiesAtPropertyValue(){
	entityComboBox.removeAllItems();
	String propertyName$=(String)propertyComboBox.getSelectedItem();
	String propertyValue$=(String)valueComboBox.getSelectedItem();
	Entigrator entigrator=console.getEntigrator(entihome$);
	String[] sa=entigrator.indx_listEntities(propertyName$, propertyValue$);
	if(sa!=null){
		String label$;
		DefaultComboBoxModel<String> entityBoxModel=new DefaultComboBoxModel<String>();
		ArrayList<String>sl=new ArrayList<String>();
		for(String aSa:sa){
			label$=entigrator.indx_getLabel(aSa);
		    if(label$!=null)
		    	sl.add(label$);
		}
		sa=sl.toArray(new String[0]);
		Support.sortStrings(sa);
		for(String aSa:sa)
			entityBoxModel.addElement(aSa);
		entityComboBox.setModel(entityBoxModel);
		}
}
private void selectEntitiesAtProperty(){
	String propertyName$=(String)propertyComboBox.getSelectedItem();
	Entigrator entigrator=console.getEntigrator(entihome$);
	String[] sa=entigrator.indx_listEntitiesAtPropertyName(propertyName$);
	if(sa!=null){
		String label$;
		DefaultComboBoxModel<String> entityBoxModel=new DefaultComboBoxModel<String>();
		ArrayList<String>sl=new ArrayList<String>();
		for(String aSa:sa){
			label$=entigrator.indx_getLabel(aSa);
		    if(label$!=null)
		    	sl.add(label$);
		}
		sa=sl.toArray(new String[0]);
		Support.sortStrings(sa);
		for(String aSa:sa)
			entityBoxModel.addElement(aSa);
		entityComboBox.setModel(entityBoxModel);
		}
}
private void intersect(){
	String propertyName$=(String)propertyComboBox.getSelectedItem();
	String propertyValue$=(String)valueComboBox.getSelectedItem();
	Entigrator entigrator=console.getEntigrator(entihome$);
	String[] sa=entigrator.indx_listEntities(propertyName$, propertyValue$);
	DefaultComboBoxModel<String> entityBoxModel=new DefaultComboBoxModel<String>();
	if(sa!=null){
		String label$;
		ArrayList<String>sl=new ArrayList<String>();
		for(String aSa:sa){
			label$=entigrator.indx_getLabel(aSa);
		    if(label$!=null)
		    	sl.add(label$);
		}
		sa=sl.toArray(new String[0]);
		ComboBoxModel<String> model=entityComboBox.getModel();
		int cnt=model.getSize();
		sl.clear();
		for(int i=0;i<cnt;i++)
			sl.add(model.getElementAt(i));
		String[] ea=sl.toArray(new String[0]);	
        sa = Support.intersect(sa, ea);
        if(sa!=null){
		   Support.sortStrings(sa);
		   for(String aSa:sa)
			   entityBoxModel.addElement(aSa);
        }
	}
	 entityComboBox.setModel( entityBoxModel);		
}
private void copy(){
	console.clipboard.clear();
	String entityLabel$=(String)entityComboBox.getSelectedItem();
	if(entityLabel$==null)
		return;
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.ent_getAtLabel(entityLabel$);
	if(entity==null)
		return;
	String locator$=EntityHandler.getEntityLocator(entigrator, entity);
	console.clipboard.putString(locator$);
	/*
	String copyLocator$=JClipboard.getPutLocator(new String[]{locator$});
	String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "copy.png");
	copyLocator$= Locator.append(copyLocator$,Locator.LOCATOR_ICON,icon$);
	JConsoleHandler.execute(console, copyLocator$);
	*/
}
private void reindex(){
	String entityLabel$=(String)entityComboBox.getSelectedItem();
	if(entityLabel$==null)
		return;
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.ent_getAtLabel(entityLabel$);
	if(entity==null)
		return;
	String locator$=EntityHandler.getEntityLocator(entigrator, entity);
	JEntityPrimaryMenu.reindexEntity(console, locator$);
}
private void delete(){
	String entityLabel$=(String)entityComboBox.getSelectedItem();
	if(entityLabel$==null)
		return;
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.ent_getAtLabel(entityLabel$);
	if(entity==null)
		return;
	JDialog.setDefaultLookAndFeelDecorated(true);
    int response = JOptionPane.showConfirmDialog(this, "Delete entity '"+entityLabel$+"' ?", "Confirm",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (response == JOptionPane.YES_OPTION) {
        entigrator.deleteEntity(entity);
        if(VALUE_MODE.equals(mode$)){
        	selectEntitiesAtPropertyValue();
        }
    }
  }
private void listEntities(){
	ComboBoxModel<String> model=entityComboBox.getModel();
	int cnt=model.getSize();
	if(cnt<1)
		return;
	String[] la=new String[cnt];
	for(int i=0;i<cnt;i++)
		la[i]=model.getElementAt(i);
	String list$=Locator.toString(la);
	JEntitiesPanel ep=new JEntitiesPanel();
	String locator$=ep.getLocator();
	locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
	locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, list$);
	JConsoleHandler.execute(console, locator$);
  }
private String[] getSelectedEntities(){
	try{
	ComboBoxModel<String> model=entityComboBox.getModel();
	int cnt=model.getSize();
	if(cnt<1)
		return null;
	Entigrator entigrator=console.getEntigrator(entihome$);
	ArrayList<String>sl=new ArrayList<String>();
	String entityKey$;
	for(int i=0;i<cnt;i++){
		entityKey$=entigrator.indx_keyAtLabel(model.getElementAt(i));
		if(entityKey$!=null)
			sl.add(entityKey$);
	}
	return sl.toArray(new String[0]);
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return null;
	}
  }
/**
 * Get JPanel object to put into the main console as context.	
 */
@Override
	public JPanel getPanel() {
		return this;
	}
/**
 * Get context menu.	
 */
	@Override
	public JMenu getContextMenu() {
		JMenu menu=new JMenu("Context");
		JMenuItem autoValueModeItem = new JMenuItem(VALUE_MODE);
		autoValueModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode$=VALUE_MODE;
				modeField.setText(mode$);
				selectEntitiesAtPropertyValue();
			}
		} );
		menu.add(autoValueModeItem);
		JMenuItem autoPropertyModeItem = new JMenuItem(PROPERTY_MODE);
		autoPropertyModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode$=PROPERTY_MODE;
				modeField.setText(mode$);
				selectEntitiesAtProperty();
			}
		} );
		menu.add(autoPropertyModeItem);
		JMenuItem autoAssignModeItem = new JMenuItem(ASSIGN_MODE);
		autoAssignModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode$=ASSIGN_MODE;
				modeField.setText(mode$);
			}
    } );
		menu.add(autoAssignModeItem);
		JMenuItem autoIncludeModeItem = new JMenuItem(INCLUDE_MODE);
		autoIncludeModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode$=INCLUDE_MODE;
				modeField.setText(mode$);
			}
    } );
		menu.add(autoIncludeModeItem);
		menu.addSeparator();
		JMenuItem listItem = new JMenuItem("List");
		listItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listEntities();
			}
    } );
		menu.add(listItem);
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
    } );
		menu.add(copyItem);
		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
    } );
		menu.add(deleteItem);
		JMenuItem intersectItem = new JMenuItem("Intersect");
		intersectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				intersect();
			}
    } );
		menu.add(intersectItem);
		JMenuItem reindexItem = new JMenuItem("Reindex");
		reindexItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reindex();
			}
    } );
		menu.add(reindexItem);

		menu.addSeparator();
		JMenuItem setAsContainerItem = new JMenuItem("Set entity as container");
		setAsContainerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
					String entity$=(String)entityComboBox.getSelectedItem();
					model.addElement(entity$);
					containerComboBox.setModel(model);
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
    } );
		menu.add(setAsContainerItem);
		JMenuItem listContainersItem = new JMenuItem("List containers");
		listContainersItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listContainers();
			}
    } );
		menu.add(listContainersItem);
		menu.addSeparator();
		JMenuItem baseNavigatorItem = new JMenuItem("Base navigator");
		baseNavigatorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JBaseNavigator bn=new JBaseNavigator();
				String bnLocator$=bn.getLocator();
				bnLocator$=Locator.append(bnLocator$, Entigrator.ENTIHOME, entihome$);
				JConsoleHandler.execute(console, bnLocator$);
			}
    } );
		menu.add(baseNavigatorItem);
		return menu;
	}
private void setSelection(JComboBox <String>comboBox,String item$){
	 ComboBoxModel<String> model=comboBox.getModel();
	 if (model != null) {
         int cnt = model.getSize();
         String[] sa = null;
         if (cnt > 0) {
             sa = new String[cnt];
             for (int i = 0; i < cnt; i++)
                 if (item$.equals(model.getElementAt(i))) {
                      comboBox.setSelectedIndex(i);
                     return;
                 }
         }
     }
}
private String getList(JComboBox <String>comboBox){
	 ComboBoxModel<String> model=comboBox.getModel();
	 if (model == null)
	     return null;
        int cnt = model.getSize();
        String[] sa = null;
        if (cnt < 1)
        	return null;
        sa = new String[cnt];
        for (int i = 0; i < cnt; i++)
          sa[i]=model.getElementAt(i);
        return Locator.toString(sa);
}
/**
 * Get context locator. 
 * @return the locator.
 */		
@Override
public String getLocator() {

		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(Locator.LOCATOR_TITLE, "Design");
	    locator.setProperty(MODE, mode$);
	   if(entihome$!=null){
	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
	      Entigrator entigrator=console.getEntigrator(entihome$);
	    String icon$=Support.readHandlerIcon(entigrator,JBasesPanel.class, "design.png");
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	   }
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JDesignPanel.class.getName());
	    String propertyName$=(String)propertyComboBox.getSelectedItem();
	    if(propertyName$!=null)
	    	locator.setProperty(PROPERTY_NAME,propertyName$);
	    String propertyValue$=(String)valueComboBox.getSelectedItem();
	    if(propertyValue$!=null)
	    	locator.setProperty(PROPERTY_VALUE,propertyValue$);
	    String entityLabel$=(String)entityComboBox.getSelectedItem();
	    if(entityLabel$!=null){
	    	locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    	String entitiesList$=getList(entityComboBox);
	    	locator.setProperty(EntityHandler.ENTITY_LIST,entitiesList$);
	    }
	    String containerLabel$=(String)containerComboBox.getSelectedItem();
	    if(containerLabel$!=null){
	    	locator.setProperty(CONTAINER_LABEL,containerLabel$);
	    	String containersList$=getList(containerComboBox);
	    	locator.setProperty(CONTAINERS_LIST,containersList$);
	    }
	    locator.setProperty(MODE,mode$);
	    return Locator.toString(locator);
	}
/**
 * Complete the context after
 * remove it from the main console.
 */	
@Override
public void close(){
	Stack<String>track=console.getTrack();
	track.pop();
	track.push(getLocator());
	console.setTrack(track);
}
/**
 * Create the context.
 *  @param console the main application console
 *  @param locator$ the locator string.
 * @return the context.
 */	
@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		this.console=console;
		//System.out.println("DesignPanel:instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String mode$=locator.getProperty(MODE);
		if(mode$!=null){
			this.mode$=mode$;
			modeField.setText(mode$);
		}
		setProperties();
		String propertyName$=locator.getProperty(PROPERTY_NAME);
		if(propertyName$!=null)
			setSelection(propertyComboBox,propertyName$);
		setValues((String)propertyComboBox.getSelectedItem());
		String propertyValue$=locator.getProperty(PROPERTY_VALUE);
		if(propertyValue$!=null)
			setSelection(valueComboBox,propertyValue$);
		String entitiesList$=locator.getProperty(EntityHandler.ENTITY_LIST);
		if(entitiesList$!=null){
			String[] sa=Locator.toArray(entitiesList$);
			if(sa!=null){
				DefaultComboBoxModel <String>model=new DefaultComboBoxModel<String>();
				for(String aSa:sa)
					model.addElement(aSa);
				entityComboBox.setModel(model);
				String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
				if(entityLabel$!=null)
					setSelection(entityComboBox,entityLabel$);
			}
		}
		String containersList$=locator.getProperty(CONTAINERS_LIST);
		if(containersList$!=null){
			String[] sa=Locator.toArray(containersList$);
			if(sa!=null){
				DefaultComboBoxModel <String>model=new DefaultComboBoxModel<String>();
				for(String aSa:sa)
					model.addElement(aSa);
				containerComboBox.setModel(model);
				String containerLabel$=locator.getProperty(CONTAINER_LABEL);
				if(containerLabel$!=null)
					setSelection(containerComboBox,containerLabel$);
			}
		}
		return this;
	}
/**
 * Get context title.
 * @return the title string.
 */		
@Override
	public String getTitle() {
		try{
		  Entigrator entigrator=console.getEntigrator(entihome$);
		  String base$=entigrator.getBaseName();
		  String title$="Design("+base$+")";
		  return title$;
		}catch(Exception e){
		return "Design";
		}
	}
/**
 * Get context type.
 * @return the type string.
 */	
	@Override
	public String getType() {
		return "Design";
	}
	private void showPropertyPanel(){
		JPropertyPanel pp=new JPropertyPanel();
		String locator$=pp.getLocator();
		locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
		locator$=Locator.append(locator$,PROPERTY_NAME,(String)propertyComboBox.getSelectedItem());
		JConsoleHandler.execute(console, locator$);
	}
	private void showValuePanel(){
		JValuePanel pp=new JValuePanel();
		String locator$=pp.getLocator();
		locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
		locator$=Locator.append(locator$,PROPERTY_NAME,(String)propertyComboBox.getSelectedItem());
		locator$=Locator.append(locator$,PROPERTY_VALUE,(String)valueComboBox.getSelectedItem());
		locator$=Locator.append(locator$,MODE,mode$);
		Entigrator entigrator=console.getEntigrator(entihome$);	
		String entityLabel$=(String)entityComboBox.getSelectedItem();
		if(entityLabel$!=null){
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
			locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
		}
		String[] sa=getSelectedEntities();
		if(sa!=null){
			String selectedEntities$=Locator.toString(sa);
			locator$=Locator.append(locator$,EntityHandler.ENTITY_LIST,selectedEntities$);
		}
		JConsoleHandler.execute(console, locator$);
	}
	private void showEntityPanel(){
		String entityLabel$=(String)entityComboBox.getSelectedItem();
		if(entityLabel$==null)
			return;
		try{
//		System.out.println("DesignPanel:showEntityPanel:label="+entityLabel$);
		Entigrator entigrator=console.getEntigrator(entihome$);	
		String entityLocator$=EntityHandler.getEntityLocator(entigrator, entityLabel$);
//		System.out.println("DesignPanel:showEntityPanel:entity locator="+entityLocator$);
		Properties entityLocator=Locator.toProperties(entityLocator$);
		String entityKey$=entityLocator.getProperty(EntityHandler.ENTITY_KEY);
		String entityIcon$=entityLocator.getProperty(Locator.LOCATOR_ICON);
		JEntityFacetPanel erm=new JEntityFacetPanel();
		String locator$=erm.getLocator();
		locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
		locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
		if(entityLabel$!=null)
		  locator$=Locator.append(locator$,EntityHandler.ENTITY_LABEL,entityLabel$);
		if(entityIcon$!=null)
		   locator$=Locator.append(locator$,Locator.LOCATOR_ICON,entityIcon$);
		JConsoleHandler.execute(console, locator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	private void showContainersPanel(){
		if(containerComboBox.getSelectedItem()==null)
			return;
		JContainerPanel cp=new JContainerPanel();
		String locator$=cp.getLocator();
		locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
		locator$=Locator.append(locator$,PROPERTY_NAME,(String)propertyComboBox.getSelectedItem());
		locator$=Locator.append(locator$,PROPERTY_VALUE,(String)valueComboBox.getSelectedItem());
		Entigrator entigrator=console.getEntigrator(entihome$);	
		String entityLabel$=(String)entityComboBox.getSelectedItem();
		if(entityLabel$!=null){
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
			locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
		}
		String[] sa=getSelectedEntities();
		if(sa!=null){
			String selectedEntities$=Locator.toString(sa);
			locator$=Locator.append(locator$,EntityHandler.ENTITY_LIST,selectedEntities$);
		}
		String containerLabel$=(String)containerComboBox.getSelectedItem();
	    if(containerLabel$!=null){
	    	locator$=Locator.append(locator$,CONTAINER_LABEL,containerLabel$);
	    	String containersList$=getList(containerComboBox);
	    	locator$=Locator.append(locator$,CONTAINERS_LIST,containersList$);
	    }else
	    	System.out.println("DesignPanel:showContainersPanel:no containers");
	    locator$=Locator.append(locator$,MODE,mode$);
//		System.out.println("DesignPanel:showContainersPanel:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		JConsoleHandler.execute(console, locator$);
	}
private void listContainers(){
		containerComboBox.removeAllItems();
		try{
		String entityLabel$=(String)entityComboBox.getSelectedItem();
		if(entityLabel$==null||"empty".equals(entityLabel$)||entityLabel$.length()<1)
			return;
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		String[]sa=entigrator.ent_listContainers(entity);
		if(sa!=null){
			ArrayList<String>sl=new ArrayList<String>();
			for(String s:sa){
			entityLabel$=entigrator.indx_getLabel(s);
			if(entityLabel$!=null)
				sl.add(entityLabel$);
			}
			Collections.sort(sl);
			DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
			for(String s:sl)
				model.addElement(s);
			containerComboBox.setModel(model);
		}
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
		}
	}
/**
 * Get context subtitle.
 * @return the subtitle string.
 */	
	@Override
	public String getSubtitle() {
		try{
			return console.getEntigrator(entihome$).getBaseName();
		}catch(Exception e){
			return null;
		}
	}
}
