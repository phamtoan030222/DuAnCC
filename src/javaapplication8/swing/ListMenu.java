package javaapplication8.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javaapplication8.event.EventMenuSelected;
import javaapplication8.model.Model_Menu;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 *
 * @author phamd
 */
public class ListMenu<E extends Object>extends JList<E>{

    private final DefaultListModel model;
    private int selectedIndex = -1;
    private int overIndex = -1;
    private EventMenuSelected event;
    
    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
    }
    public ListMenu() {
        model = new DefaultListModel();
        setModel(model);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)){
                    int index = locationToIndex(e.getPoint());
                    Object o = model.getElementAt(index);
                    if (o instanceof Model_Menu){
                        Model_Menu menu = (Model_Menu) o;
                        if(menu.getType()==Model_Menu.MenuType.MENU){
                            selectedIndex = index;
                             System.out.println("Selected index: " + selectedIndex);
                             if(event != null) {
                                 event.selected(selectedIndex);
                             }
                        }
                    } else {
                        selectedIndex = index;
                    }
                   

                    revalidate();
                    repaint();
                }
              }

            @Override
            public void mouseExited(MouseEvent e) {
              overIndex = -1;
                repaint();
            }
   });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if(index != overIndex){
                    Object o = model.getElementAt(index);
                    if(o instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) o;
                        if(menu.getType() == Model_Menu.MenuType.MENU){
                            overIndex = index;
                        } else {
                            overIndex = -1;
                        }
                        repaint();
                    }
                }
             }
            
        });
    }

    @Override
    public ListCellRenderer<? super E> getCellRenderer() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object o, int index, boolean selected, boolean focus) {
            Model_Menu data;
            if(o instanceof Model_Menu){
                data = (Model_Menu) o;
            } else {
                data=new Model_Menu("",o+"",Model_Menu.MenuType.EMPTY);
            }
            MenuItem item = new MenuItem(data);
            item.setSelected(selectedIndex == index);
            return item;
            }
        
        };
    }
    public void addItem(Model_Menu data){
        model.addElement(data);
    }
    
}
