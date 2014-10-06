package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.MapData;
import deltageek.eu4.model.Province;
import deltageek.eu4.model.ProvinceType;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class ProvincePane extends JPanel {
    private MapData mapData;

    private DefaultListModel<Province> provinceListModel = new DefaultListModel<>();
    private DefaultComboBoxModel<ProvinceType> provinceFilterModel = new DefaultComboBoxModel<>(ProvinceType.getFilterValues());

    private JList<Province> lstProvinces;
    private JList<Province> lstAdjacent;
    private JComboBox<ProvinceType> cbxProvinceFilter;
    private JButton btnExport;

    private boolean isUpdatingAdjacency = false;
    private ProvinceUIHandlers handlers;

    public ProvincePane(){
        this(null);
    }

    public ProvincePane(MapData data) {
        super(new BorderLayout());

        mapData = data;

        handlers = new ProvinceUIHandlers(this, mapData);

        cbxProvinceFilter = new JComboBox<>(provinceFilterModel);
        cbxProvinceFilter.getModel().setSelectedItem(null);
        cbxProvinceFilter.setRenderer(new FilterListCellRenderer());
        cbxProvinceFilter.addActionListener(e -> refresh());
        cbxProvinceFilter.setEnabled(false);

        lstProvinces = new JList<>(provinceListModel);
        lstProvinces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstProvinces.addListSelectionListener(handlers.getProvinceSelectionHandler());
        lstProvinces.setCellRenderer(new ProvinceListCellRenderer());

        lstAdjacent = new JList<>();
        lstAdjacent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAdjacent.addMouseListener(handlers.getAdjacencySelectionHandler());
        lstAdjacent.setCellRenderer(new ProvinceListCellRenderer(this));

        JPanel provincePanel = new JPanel(new BorderLayout());
        provincePanel.add(cbxProvinceFilter, BorderLayout.NORTH);
        provincePanel.add(new JScrollPane(lstProvinces), BorderLayout.CENTER);

        JScrollPane adjacencyScroll = new JScrollPane(lstAdjacent);
        JPanel adjacencyPanel = new JPanel(new BorderLayout());
        JLabel adjacencyHeader = new JLabel("Adjacent Provinces");
        adjacencyHeader.setHorizontalAlignment(SwingConstants.CENTER);
        adjacencyPanel.add(adjacencyHeader, BorderLayout.NORTH);
        adjacencyPanel.add(adjacencyScroll, BorderLayout.CENTER);

        btnExport = new JButton("Export for mod");
        btnExport.addActionListener(handlers.getExportHandler());

        add(provincePanel, BorderLayout.WEST);
        add(adjacencyPanel, BorderLayout.CENTER);
        add(btnExport, BorderLayout.SOUTH);
    }

    public void refresh(){
        ProvinceType filter = getSelectedProvinceFilter();

        if(mapData != null) {
            handlers.setMapData(mapData);

            java.util.List<Province> filteredProvinces =
                    mapData.provinces
                            .stream()
                            .filter(p -> filter == null || p.provinceType == filter)
                            .collect(Collectors.toList());

            Collections.sort(filteredProvinces);
            setProvinces(filteredProvinces);
        }
    }

    public void setProvinces(java.util.List<Province> provinces){
        lstProvinces.clearSelection();
        provinceListModel.removeAllElements();
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

    public void setData(MapData data) {
        mapData = data;
        refresh();
    }

    public void setExportButtonEnabled(boolean isEnabled) {
        btnExport.setEnabled(isEnabled);
    }
}
