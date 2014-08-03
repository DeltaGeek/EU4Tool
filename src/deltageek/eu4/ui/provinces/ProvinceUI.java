package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.EU4Data;
import deltageek.eu4.model.Province;
import deltageek.eu4.model.ProvinceType;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProvinceUI extends JPanel {

    private DefaultListModel<Province> provinceListModel = new DefaultListModel<>();
    private DefaultComboBoxModel<ProvinceType> provinceFilterModel = new DefaultComboBoxModel<>(ProvinceType.getFilterValues());

    private JTextField txtLoadDir;
    private JButton btnLoad;
    private JList<Province> lstProvinces;
    private JList<Province> lstAdjacent;
    private JComboBox<ProvinceType> cbxProvinceFilter;

    private boolean isUpdatingAdjacency = false;

    public ProvinceUI(final EU4Data data) {
        super(new BorderLayout());

        ProvinceEventHandlers handlers = new ProvinceEventHandlers(this, data);

        btnLoad = new JButton("Load Data");
        btnLoad.addActionListener(handlers.getLoadButtonHandler());

        txtLoadDir = new JTextField("D:\\Steam\\steamapps\\common\\Europa Universalis IV");

        cbxProvinceFilter = new JComboBox<>(provinceFilterModel);
        cbxProvinceFilter.getModel().setSelectedItem(null);
        cbxProvinceFilter.setRenderer(new FilterListCellRenderer());
        cbxProvinceFilter.addActionListener(handlers.getFilterHandler());
        cbxProvinceFilter.setEnabled(false);

        lstProvinces = new JList<>(provinceListModel);
        lstProvinces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstProvinces.addListSelectionListener(handlers.getProvinceSelectionHandler());
        lstProvinces.setCellRenderer(new ProvinceListCellRenderer());

        lstAdjacent = new JList<>();
        lstAdjacent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAdjacent.addMouseListener(handlers.getAdjacencySelectionHandler());
        lstAdjacent.setCellRenderer(new ProvinceListCellRenderer(this));

        JPanel textPanel = new JPanel();
        textPanel.add(new JLabel("EU 4 Base Directory"));
        textPanel.add(txtLoadDir);
        textPanel.add(btnLoad);

        JPanel provincePanel = new JPanel(new BorderLayout());
        provincePanel.add(cbxProvinceFilter, BorderLayout.NORTH);
        provincePanel.add(new JScrollPane(lstProvinces), BorderLayout.CENTER);

        add(textPanel, BorderLayout.NORTH);
        add(provincePanel, BorderLayout.WEST);
        add(new JScrollPane(lstAdjacent), BorderLayout.CENTER);
    }

    public Path getLoadPath(){
        return Paths.get(txtLoadDir.getText());
    }

    public void clearProvinceSelection(){
        lstProvinces.clearSelection();
        provinceListModel.removeAllElements();
    }

    public void setProvinces(java.util.List<Province> provinces){
        provinces.forEach(provinceListModel::addElement);
    }

    public Province getSelectedProvince(){
        int selectedIndex = lstProvinces.getSelectedIndex();

        if(selectedIndex == -1)
            return null;

        return lstProvinces.getModel().getElementAt(selectedIndex);
    }

    public void setSelectedProvince(Province province){
        if(isUpdatingAdjacency)
            return;

        Province selectedProvince = lstProvinces.getSelectedValue();
        lstProvinces.clearSelection();

        if(province != null)
        {
            isUpdatingAdjacency = true;
            lstProvinces.setSelectedValue(province, true);
            lstAdjacent.setSelectedValue(selectedProvince, true);
            isUpdatingAdjacency = false;
        }
    }

    public Province getSelectedAdjacency(){
        int selectedIndex = lstAdjacent.getSelectedIndex();

        if(selectedIndex == -1)
            return null;

        return lstAdjacent.getModel().getElementAt(selectedIndex);
    }

    public void setAdjacentProvinces(Province[] provinces){
        lstAdjacent.setListData(provinces);
    }

    public ProvinceType getSelectedProvinceFilter(){
        return (ProvinceType) cbxProvinceFilter.getSelectedItem();
    }

    public void setProvinceFilterEnabled(boolean enabled){
        cbxProvinceFilter.setEnabled(enabled);
    }
}
