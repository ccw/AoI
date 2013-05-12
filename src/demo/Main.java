package demo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main frame to display all points in area and relative information
 */
public class Main extends JFrame {

    private final AreaPanel pnlArea;

    public Main() {
        setTitle("[DEMO] AoI");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pnlArea = new AreaPanel(200, 1000, 150, 100, 5);
        initComponents();

        setVisible(true);
        scrollToPickedPoint();
    }

    private void initComponents() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout(5, 5));

        JScrollPane sclArea = new JScrollPane(pnlArea,
                                              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sclArea.setMinimumSize(new Dimension(800, 600));
        container.add(sclArea, BorderLayout.CENTER);

        JPanel pnlControl = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlControl.add(new JLabel("Pick the Point at Index of "));
        final JComboBox cbxPicker = new JComboBox(new AreaComboBoxModel(pnlArea));
        cbxPicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                scrollToPickedPoint();
            }
        });
        pnlControl.add(cbxPicker);

        JTable tblArea = new JTable(new AreaTableModel(pnlArea));
        tblArea.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable aTable,
                                                           Object aValue,
                                                           boolean aSelected,
                                                           boolean aFocusObtained,
                                                           int aRow, int aColumn) {
                if (aRow == 0) {
                    setBackground(Color.BLACK);
                    setForeground(Color.YELLOW);
                    setFont(new Font("Tahoma", Font.BOLD, 11));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                    setFont(new Font("Courier New", Font.BOLD, 10));
                }
                return super.getTableCellRendererComponent(aTable, aValue,
                                                           aSelected, aFocusObtained,
                                                           aRow, aColumn);
            }
        });
        JPanel pnlInfo = new JPanel(new BorderLayout(5, 5));
        pnlInfo.add(pnlControl, BorderLayout.NORTH);
        pnlInfo.add(new JScrollPane(tblArea,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                    BorderLayout.CENTER);
        container.add(pnlInfo, BorderLayout.EAST);
    }

    private void scrollToPickedPoint() {
        pnlArea.scrollRectToVisible(new Rectangle(pnlArea.getPickedPoint().x - 150 ,
                                                  pnlArea.getPickedPoint().y - 150,
                                                  300,
                                                  300));
    }

    /**
     * The combo box model to be associated with the area of points
     */
    public class AreaComboBoxModel extends DefaultComboBoxModel {
        AreaPanel pnlArea;

        public AreaComboBoxModel(AreaPanel pnlArea) {
            this.pnlArea = pnlArea;
        }

        @Override
        public int getSize() {
            return (pnlArea == null ? 0 : pnlArea.getPoints().length);
        }

        @Override
        public Object getElementAt(int i) {
            return i;
        }

        @Override
        public void setSelectedItem(Object o) {
            int index = Integer.valueOf(o.toString());
            pnlArea.setPickedIndex(index);
        }

        @Override
        public Object getSelectedItem() {
            return pnlArea.getPickedIndex();
        }
    }

    /**
     * The table model to contain the information of the points which are
     * within the area of the picked point
     */
    public class AreaTableModel extends AbstractTableModel {
        AreaPanel pnlArea;

        public AreaTableModel(AreaPanel pnlArea) {
            this.pnlArea = pnlArea;
            this.pnlArea.registerObserverTableModel(this);
        }

        @Override
        public int getRowCount() {
            return (pnlArea == null ? 0 : pnlArea.getPointsInArea().length + 1);
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Class<String> getColumnClass(int i) {
            return String.class;
        }

        @Override
        public String getColumnName(int i) {
            String name;
            switch (i) {
                case 0:
                    name = "Index";
                    break;
                case 1:
                    name = "X";
                    break;
                case 2:
                    name = "Y";
                    break;
                default:
                    name = "Unknown";
            }
            return name;
        }

        @Override
        public boolean isCellEditable(int y, int i) {
            return false;
        }

        @Override
        public Object getValueAt(int y, int i) {
            String result = "Unknown";
            if (y == 0) {
                switch (i) {
                    case 0:
                        result = String.valueOf(pnlArea.getPickedPoint().index);
                        break;
                    case 1:
                        result = String.valueOf(pnlArea.getPickedPoint().x);
                        break;
                    case 2:
                        result = String.valueOf(pnlArea.getPickedPoint().y);
                        break;
                    default:
                        result = "Unknown";
                }
            } else {
                switch (i) {
                    case 0:
                        result = String.valueOf(pnlArea.getPointsInArea()[y - 1].index);
                        break;
                    case 1:
                        result = String.valueOf(pnlArea.getPointsInArea()[y - 1].x);
                        break;
                    case 2:
                        result = String.valueOf(pnlArea.getPointsInArea()[y - 1].y);
                        break;
                }
            }
            return result;
        }

        @Override
        public void setValueAt(Object value, int y, int i) {
            if (i == 1) {
                pnlArea.setPickedIndex(y);
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}