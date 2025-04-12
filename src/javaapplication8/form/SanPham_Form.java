package javaapplication8.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javaapplication8.model.Model_SanPham;
import javaapplication8.model.SanPham_ChiTiet;
import javaapplication8.model.SanPham_ThuocTinh;
import javaapplication8.service.SanPhamChiTietService;
import javaapplication8.service.SanPhamService;
import javaapplication8.service.SanPhamThuocTinhService;
import javaapplication8.service.serviceimpl.SanPhamChiTietServiceImpl;
import javaapplication8.service.serviceimpl.SanPhamServiceImpl;
import javaapplication8.service.serviceimpl.SanPhamThuocTinhServiceImpl;
import javaapplication8.until.CodeGeneratorUtil;
import javaapplication8.until.STT;
import javaapplication8.until.ValidationUtil;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class SanPham_Form extends javax.swing.JPanel {

    private final SanPhamService service_sp = new SanPhamServiceImpl();
    private final SanPhamThuocTinhService service_spthuoctinh = new SanPhamThuocTinhServiceImpl();
    final SanPhamChiTietService service_spChiTiet = new SanPhamChiTietServiceImpl();

    private TableRowSorter<DefaultTableModel> rowSorter;

    private HashMap<String, Integer> sanPhamDangBanMap = new HashMap<>();
    private HashMap<String, Integer> sanPhamNgungBanMap = new HashMap<>();
    private HashMap<String, Integer> mauSacMap = new HashMap<>();
    private HashMap<String, Integer> kichThuocMap = new HashMap<>();
    private HashMap<String, Integer> chatLieuMap = new HashMap<>();

    private HashMap<String, Integer> idMauSacTheoMa = new HashMap<>();
    private HashMap<String, Integer> idKichThuocTheoMa = new HashMap<>();
    private HashMap<String, Integer> idChatLieuTheoMa = new HashMap<>();

    private HashMap<String, Integer> idSanPhamTheoMa = new HashMap<>();

    public SanPham_Form() {
        initComponents();
        customizeTabblePane();

        // Tắt khả năng chỉnh sửa
        tbl_sanpham.setDefaultEditor(Object.class, null);
        tbl_sanphamchitiet.setDefaultEditor(Object.class, null);
        tbl_thuoctinh.setDefaultEditor(Object.class, null);

        cbo_khoanggia.removeAllItems();
        cbo_khoanggia.addItem("Mặc định");
        cbo_khoanggia.addItem("Thấp đến Cao");
        cbo_khoanggia.addItem("Cao đến Thấp");

        fillTable_SP(service_sp.layDanhSachSanPhamDangBan());
        fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Áp dụng cho tất cả các cột
        for (int i = 0; i < tbl_sanpham.getColumnCount(); i++) {
            tbl_sanpham.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        for (int i = 0; i < tbl_thuoctinh.getColumnCount(); i++) {
            tbl_thuoctinh.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        for (int i = 0; i < tbl_sanphamchitiet.getColumnCount(); i++) {
            tbl_sanphamchitiet.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        rdo_mausac.setSelected(true);
        xuLyChonRadioButton();

        //fill cbobox 
        loadComboboxLoaiSanPham();
        loadComboboxLoaiMauSac();
        loadComboboxLoaiKichThuoc();
        loadComboboxLoaiChatlieu();

        lbl_danhsachthuoctinh.setText("DANH SÁCH THUỘC TÍNH MÀU SẮC");
        rdo_sanphamdangban.setSelected(true);

// Thiết lập placeholder khi form mở
        txt_timkiemsanphamtheoten.setText("Nhập tên cần tìm kiếm");
        txt_timkiemsanphamtheoten.setForeground(Color.GRAY);

// Request focus để hiển thị placeholder ngay
        txt_timkiemsanphamtheoten.requestFocusInWindow();

        txt_timkiemsanphamtheoten.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchSanPhamTuongDoi(); // Gọi phương thức tìm kiếm khi người dùng nhập vào
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchSanPhamTuongDoi(); // Gọi phương thức tìm kiếm khi người dùng xóa văn bản
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchSanPhamTuongDoi(); // Gọi phương thức tìm kiếm khi văn bản thay đổi (hiếm khi xảy ra)
            }
        });

        btn_khoiphucsanpham.setVisible(false);
        setupRadioButtons();

        cbo_sanpham.addItem("Tất cả");
        cbo_mausac.addItem("Tất cả");
        cbo_chatlieu.addItem("Tất cả");
        cbo_kichthuoc.addItem("Tất cả");

        cbo_sanpham.setSelectedItem("Tất cả");
        cbo_mausac.setSelectedItem("Tất cả");
        cbo_chatlieu.setSelectedItem("Tất cả");
        cbo_kichthuoc.setSelectedItem("Tất cả");

        cbo_sanpham.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apDungBoLoc();
            }
        });
        cbo_mausac.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apDungBoLoc();
            }
        });
        cbo_chatlieu.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apDungBoLoc();
            }
        });
        cbo_kichthuoc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apDungBoLoc();
            }
        });
        cbo_khoanggia.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apDungBoLoc();
            }
        });

    }

    void fillTable_SanPhamChiTiet(List<SanPham_ChiTiet> allSanPhamChiTiet) {
        DefaultTableModel model = (DefaultTableModel) tbl_sanphamchitiet.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (SanPham_ChiTiet sp : allSanPhamChiTiet) {
            model.addTableModelListener(e -> STT.updateSTT(model));
            model.addRow(new Object[]{
                null,
                sp.getMaSp(),
                sp.getTenSp(),
                sp.getMauSac(),
                sp.getChatLieu(),
                sp.getKichThuoc(),
                sp.getDonGia(),
                sp.getSoLuong(),
                sp.getSoLuong() > 0 ? "Còn hàng" : "Hết hàng"
            });

        }

        tbl_sanphamchitiet.setRowSorter(rowSorter); // Gắn vào bảng
    }

    private void apDungBoLoc() {
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>((DefaultTableModel) tbl_sanphamchitiet.getModel());
        tbl_sanphamchitiet.setRowSorter(rowSorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String sanPham = cbo_sanpham.getSelectedItem().toString();
        if (!sanPham.equals("Tất cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(sanPham), 2)); // Tên sản phẩm (cột 2)
        }

        String mauSac = cbo_mausac.getSelectedItem().toString();
        if (!mauSac.equals("Tất cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(mauSac), 3)); // Màu sắc (cột 3)
        }

        String chatLieu = cbo_chatlieu.getSelectedItem().toString();
        if (!chatLieu.equals("Tất cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(chatLieu), 4)); // Chất liệu (cột 4)
        }

        String kichThuoc = cbo_kichthuoc.getSelectedItem().toString();
        if (!kichThuoc.equals("Tất cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(kichThuoc), 5)); // Kích thước (cột 5)
        }

        rowSorter.setRowFilter(RowFilter.andFilter(filters));

        // Sắp xếp theo đơn giá
        String sapXep = cbo_khoanggia.getSelectedItem().toString();
        if (sapXep.equals("Thấp đến Cao")) {
            rowSorter.setSortKeys(List.of(new RowSorter.SortKey(6, SortOrder.ASCENDING)));
        } else if (sapXep.equals("Cao đến Thấp")) {
            rowSorter.setSortKeys(List.of(new RowSorter.SortKey(6, SortOrder.DESCENDING)));
        } else if (sapXep.equals("Mặc định")) {
            rowSorter.setSortKeys(null);
        }
    }

    void capNhatBangSanPhamChiTiet(List<SanPham_ChiTiet> danhSach) {
        fillTable_SanPhamChiTiet(danhSach);

        DefaultTableModel model = (DefaultTableModel) tbl_sanphamchitiet.getModel();
        rowSorter = new TableRowSorter<>(model);
        tbl_sanphamchitiet.setRowSorter(rowSorter);
    }

    void fillTable_SP(List<Model_SanPham> ds) {
        DefaultTableModel model = (DefaultTableModel) tbl_sanpham.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (Model_SanPham sp : ds) {
            model.addTableModelListener(e -> STT.updateSTT(model));
            model.addRow(new Object[]{
                null,
                sp.getMaSp(),
                sp.getTen(),
                sp.getMoTa(),
                sp.getSoLuong(),
                sp.getSoLuong() > 0 ? "Còn hàng" : "Hết hàng"
            });
        }
    }

    private void fill_SanPhamDauTien(List<Model_SanPham> ds, String maSpMoi) {
        DefaultTableModel model = (DefaultTableModel) tbl_sanpham.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // Đưa sản phẩm mới lên đầu danh sách nếu có
        if (maSpMoi != null) {
            for (int i = 0; i < ds.size(); i++) {
                String maSp = ds.get(i).getMaSp();
                if (maSp != null && maSp.equals(maSpMoi)) {
                    Model_SanPham spMoi = ds.remove(i);
                    ds.add(0, spMoi);
                    break;
                }
            }
        }

        // Thêm dữ liệu
        for (Model_SanPham sp : ds) {
            model.addRow(new Object[]{
                null,
                sp.getMaSp(),
                sp.getTen(),
                sp.getMoTa(),
                sp.getSoLuong(),
                sp.getSoLuong() > 0 ? "Còn hàng" : "Hết hàng"
            });
        }

        // Chỉ thêm listener 1 lần!
        if (model.getTableModelListeners().length == 0) {
            model.addTableModelListener(e -> STT.updateSTT(model));
        }

        // Chọn dòng đầu tiên nếu có mã mới
        if (maSpMoi != null && tbl_sanpham.getRowCount() > 0) {
            tbl_sanpham.setRowSelectionInterval(0, 0);
            tbl_sanpham.scrollRectToVisible(tbl_sanpham.getCellRect(0, 0, true));
        }
    }

    void xuLyChonRadioButton() {
        if (rdo_mausac.isSelected()) {
            capNhatBangThuocTinh("Mau_Sac");
        } else if (rdo_kichthuoc.isSelected()) {
            capNhatBangThuocTinh("Kich_Thuoc");
        } else if (rdo_chatlieu.isSelected()) {
            capNhatBangThuocTinh("Chat_Lieu");
        }
    }

    private String getSelectedTableName() {
        if (rdo_mausac.isSelected()) {
            return "Mau_Sac";
        } else if (rdo_kichthuoc.isSelected()) {
            return "Kich_Thuoc";
        } else if (rdo_chatlieu.isSelected()) {
            return "Chat_Lieu";
        }
        return null;
    }

    private void capNhatBangThuocTinh(String tableName) {
        List<SanPham_ThuocTinh> danhSach = service_spthuoctinh.layDanhSachThuocTinh(tableName);
        fillTable_SPThuocTinh(danhSach);
    }

    private void fillTable_SPThuocTinh(List<SanPham_ThuocTinh> danhSach) {
        DefaultTableModel model = (DefaultTableModel) tbl_thuoctinh.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (SanPham_ThuocTinh sp : danhSach) {
            model.addTableModelListener(e -> STT.updateSTT(model));
            model.addRow(new Object[]{
                null,
                sp.getMa(),
                sp.getTen()});
        }
    }

    private void fillTable_SPThuocTinhDauTien(List<SanPham_ThuocTinh> danhSach, String maMoi) {
        DefaultTableModel model = (DefaultTableModel) tbl_thuoctinh.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // Đưa thuộc tính mới lên đầu danh sách nếu có
        if (maMoi != null) {
            for (int i = 0; i < danhSach.size(); i++) {
                String ma = danhSach.get(i).getMa();
                if (ma != null && ma.equals(maMoi)) {
                    SanPham_ThuocTinh moi = danhSach.remove(i);
                    danhSach.add(0, moi);
                    break;
                }
            }
        }

        // Thêm dữ liệu
        for (SanPham_ThuocTinh sp : danhSach) {
            model.addRow(new Object[]{
                null,
                sp.getMa(),
                sp.getTen()
            });
        }

        // Chỉ thêm listener 1 lần
        if (model.getTableModelListeners().length == 0) {
            model.addTableModelListener(e -> STT.updateSTT(model));
        }

        // Chọn dòng đầu tiên nếu có mã mới
        if (maMoi != null && tbl_thuoctinh.getRowCount() > 0) {
            tbl_thuoctinh.setRowSelectionInterval(0, 0);
            tbl_thuoctinh.scrollRectToVisible(tbl_thuoctinh.getCellRect(0, 0, true));
        }
    }

    private void searchSanPhamTuongDoi() {
        // Lấy từ khóa tìm kiếm, chuyển về chữ thường để tìm kiếm không phân biệt hoa thường
        String keyword = txt_timkiemsanphamtheoten.getText().trim().toLowerCase();

        // Xóa dữ liệu cũ trong bảng
        DefaultTableModel model = (DefaultTableModel) tbl_sanpham.getModel();
        model.setRowCount(0);

        // Lấy trạng thái của radio button
        int status = rdo_sanphamdangban.isSelected() ? 0 : 1;

        // Lấy danh sách sản phẩm từ service (tìm kiếm tương đối)
        List<Model_SanPham> list = service_sp.loadTableTimKiemTuongDoi(keyword, status);

        // Kiểm tra nếu danh sách có sản phẩm
        if (list != null && !list.isEmpty()) {
            for (Model_SanPham sp : list) {
                // So sánh tên sản phẩm với từ khóa tìm kiếm (tìm kiếm không phân biệt chữ hoa chữ thường)
                if (sp.getTen().toLowerCase().contains(keyword)) {
                    // Nếu có khớp, hiển thị vào bảng
                    model.addRow(new Object[]{
                        null,
                        sp.getMaSp(),
                        sp.getTen(),
                        sp.getMoTa(),
                        sp.getSoLuong(),
                        sp.getSoLuong() > 0 ? "Còn hàng" : "Hết hàng"
                    });
                }
            }
        }

        // Không hiển thị thông báo nếu không tìm thấy sản phẩm
    }

    // Thiết lập các radio button
    private void setupRadioButtons() {
        rdo_sanphamdangban.addActionListener(e -> searchSanPhamTuongDoi());
        rdo_sanphamngungban.addActionListener(e -> searchSanPhamTuongDoi());
    }

    void macDinhCboSPCT() {
        cbo_sanpham.setSelectedItem("Tất cả");
        cbo_mausac.setSelectedItem("Tất cả");
        cbo_chatlieu.setSelectedItem("Tất cả");
        cbo_kichthuoc.setSelectedItem("Tất cả");
        cbo_khoanggia.setSelectedItem("Mặc định");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        table1 = new javaapplication8.swing.Table();
        table2 = new javaapplication8.swing.Table();
        table3 = new javaapplication8.swing.Table();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cbo_sanpham = new javax.swing.JComboBox<>();
        cbo_mausac = new javax.swing.JComboBox<>();
        cbo_kichthuoc = new javax.swing.JComboBox<>();
        cbo_chatlieu = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        cbo_khoanggia = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        btn_themmoispct = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_sanphamchitiet = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txt_mathuoctinh = new javax.swing.JTextField();
        txt_tenthuoctinh = new javax.swing.JTextField();
        lbl_thongbaothuoctinh = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        rdo_mausac = new javax.swing.JRadioButton();
        rdo_kichthuoc = new javax.swing.JRadioButton();
        rdo_chatlieu = new javax.swing.JRadioButton();
        btn_themthuoctinh = new javax.swing.JButton();
        btn_capnhatthuoctinh = new javax.swing.JButton();
        btn_xoathuoctinh = new javax.swing.JButton();
        btn_lammoithuoctinh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_thuoctinh = new javax.swing.JTable();
        lbl_danhsachthuoctinh = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        panel_danhsachsanpham = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_masanpham = new javax.swing.JTextField();
        txt_tensanpham = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_motasanpham = new javax.swing.JTextArea();
        btn_themsanpham = new javax.swing.JButton();
        btn_capnhatsanpham = new javax.swing.JButton();
        btn_xoasanpham = new javax.swing.JButton();
        btn_lammoisanpham = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        rdo_sanphamngungban = new javax.swing.JRadioButton();
        rdo_sanphamdangban = new javax.swing.JRadioButton();
        txt_timkiemsanphamtheoten = new org.jdesktop.swingx.JXTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_sanpham = new javaapplication8.swing.Table();
        lbl_danhsachsanpham = new javax.swing.JLabel();
        lbl_thongbaotensp = new javax.swing.JLabel();
        btn_khoiphucsanpham = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1007, 628));

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setText("THÔNG TIN SẢN PHẨM CHI TIẾT");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cbo_sanpham.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_sanpham.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
<<<<<<< HEAD

        cbo_mausac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_mausac.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));

        cbo_kichthuoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_kichthuoc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));

        cbo_chatlieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_chatlieu.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
=======
        cbo_sanpham.setPreferredSize(new java.awt.Dimension(100, 22));

        cbo_mausac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_mausac.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
        cbo_mausac.setPreferredSize(new java.awt.Dimension(100, 22));

        cbo_kichthuoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_kichthuoc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
        cbo_kichthuoc.setMinimumSize(new java.awt.Dimension(100, 22));
        cbo_kichthuoc.setPreferredSize(new java.awt.Dimension(100, 22));

        cbo_chatlieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_chatlieu.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
        cbo_chatlieu.setPreferredSize(new java.awt.Dimension(100, 22));
>>>>>>> origin/master

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Sản phẩm");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Màu sắc");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Kích thước");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Chất liệu");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Khoảng giá");

        jTextField1.setBorder(null);

        jTextField2.setBorder(null);

        cbo_khoanggia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbo_khoanggia.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 153, 255)));
        cbo_khoanggia.setPreferredSize(new java.awt.Dimension(100, 22));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(cbo_sanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(cbo_mausac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(cbo_kichthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(cbo_chatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(cbo_khoanggia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13))
<<<<<<< HEAD
                .addContainerGap(172, Short.MAX_VALUE))
=======
                .addContainerGap(18, Short.MAX_VALUE))
>>>>>>> origin/master
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbo_sanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_mausac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_kichthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_chatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
<<<<<<< HEAD
                    .addComponent(jLabel14)
=======
>>>>>>> origin/master
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_khoanggia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Bộ lọc");

        jButton9.setText("Xuất excel");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        btn_themmoispct.setBackground(new java.awt.Color(204, 255, 255));
        btn_themmoispct.setText("Thêm mới");
        btn_themmoispct.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_themmoispct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themmoispctActionPerformed(evt);
            }
        });

        jButton12.setText("Quét QR");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Tải mã QR");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Làm mới");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        tbl_sanphamchitiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã SPCT", "Sản phẩm", "Màu sắc", "Chất liệu", "Kích thước", "Giá ", "Số lượng", "Trạng thái"
            }
        ));
        jScrollPane4.setViewportView(tbl_sanphamchitiet);
        if (tbl_sanphamchitiet.getColumnModel().getColumnCount() > 0) {
            tbl_sanphamchitiet.getColumnModel().getColumn(0).setMinWidth(50);
            tbl_sanphamchitiet.getColumnModel().getColumn(0).setPreferredWidth(50);
            tbl_sanphamchitiet.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(356, 356, 356))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton9)
                        .addGap(374, 374, 374)
                        .addComponent(btn_themmoispct)
                        .addGap(26, 26, 26)
                        .addComponent(jButton12)
                        .addGap(34, 34, 34)
                        .addComponent(jButton13)
                        .addGap(51, 51, 51)
                        .addComponent(jButton14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 976, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(btn_themmoispct)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addGap(43, 43, 43)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Chi tiết sản phẩm", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setText("Mã thuộc tính");

        jLabel19.setText("Tên thuộc tính");

        txt_mathuoctinh.setEditable(false);
        txt_mathuoctinh.setText("###");
        txt_mathuoctinh.setBorder(null);

        txt_tenthuoctinh.setBorder(null);

        lbl_thongbaothuoctinh.setForeground(new java.awt.Color(255, 51, 51));
        lbl_thongbaothuoctinh.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txt_mathuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                        .addComponent(jLabel18)
                        .addComponent(jLabel19)
                        .addComponent(lbl_thongbaothuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txt_tenthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_mathuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_tenthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_thongbaothuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 255));
        jLabel20.setText("THIẾT LẬP THUỘC TÍNH");

        buttonGroup2.add(rdo_mausac);
        rdo_mausac.setText("Màu Sắc");
        rdo_mausac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_mausacActionPerformed(evt);
            }
        });

        buttonGroup2.add(rdo_kichthuoc);
        rdo_kichthuoc.setText("Kích Thước");
        rdo_kichthuoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_kichthuocActionPerformed(evt);
            }
        });

        buttonGroup2.add(rdo_chatlieu);
        rdo_chatlieu.setText("Chất Liệu");
        rdo_chatlieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_chatlieuActionPerformed(evt);
            }
        });

        btn_themthuoctinh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/material-symbols--add-circle-outline.png"))); // NOI18N
        btn_themthuoctinh.setText("Thêm");
        btn_themthuoctinh.setBorder(null);
        btn_themthuoctinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themthuoctinhActionPerformed(evt);
            }
        });

        btn_capnhatthuoctinh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/capnhat.png"))); // NOI18N
        btn_capnhatthuoctinh.setText("Cập nhật");
        btn_capnhatthuoctinh.setBorder(null);
        btn_capnhatthuoctinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_capnhatthuoctinhActionPerformed(evt);
            }
        });

        btn_xoathuoctinh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/delete.png"))); // NOI18N
        btn_xoathuoctinh.setText("Xóa");
        btn_xoathuoctinh.setBorder(null);
        btn_xoathuoctinh.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btn_xoathuoctinhMouseMoved(evt);
            }
        });
        btn_xoathuoctinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoathuoctinhActionPerformed(evt);
            }
        });

        btn_lammoithuoctinh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/lammoi.png"))); // NOI18N
        btn_lammoithuoctinh.setText("Làm mới");
        btn_lammoithuoctinh.setBorder(null);
        btn_lammoithuoctinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoithuoctinhActionPerformed(evt);
            }
        });

        tbl_thuoctinh.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "STT", "Mã thuộc tính", "Tên thuộc tính"
            }
        ));
        tbl_thuoctinh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_thuoctinhMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_thuoctinh);
        if (tbl_thuoctinh.getColumnModel().getColumnCount() > 0) {
            tbl_thuoctinh.getColumnModel().getColumn(0).setMinWidth(50);
            tbl_thuoctinh.getColumnModel().getColumn(0).setPreferredWidth(50);
            tbl_thuoctinh.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        lbl_danhsachthuoctinh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_danhsachthuoctinh.setForeground(new java.awt.Color(51, 0, 255));
        lbl_danhsachthuoctinh.setText(" ");

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/first.png"))); // NOI18N
        jButton5.setBorder(null);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/before.png"))); // NOI18N
        jButton6.setBorder(null);

        jLabel21.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 204, 255));
        jLabel21.setText("jLabel6");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/next.png"))); // NOI18N
        jButton7.setBorder(null);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/last.png"))); // NOI18N
        jButton8.setBorder(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdo_kichthuoc)
                            .addComponent(rdo_chatlieu))
                        .addGap(137, 137, 137)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btn_xoathuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(104, 104, 104)
                                .addComponent(btn_lammoithuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btn_themthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(104, 104, 104)
                                .addComponent(btn_capnhatthuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(93, 93, 93))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(rdo_mausac)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 946, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_danhsachthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(349, 349, 349)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(393, 393, 393)
                        .addComponent(jLabel20)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(177, 177, 177)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_xoathuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_lammoithuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rdo_mausac)
                                .addGap(41, 41, 41)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdo_kichthuoc)
                                    .addComponent(btn_themthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_capnhatthuoctinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rdo_chatlieu))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(lbl_danhsachthuoctinh, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Thuộc tính", jPanel3);

        panel_danhsachsanpham.setBackground(new java.awt.Color(255, 255, 255));
        panel_danhsachsanpham.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panel_danhsachsanpham.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Mã sản phẩm");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Tên sản phẩm");

        txt_masanpham.setEditable(false);
        txt_masanpham.setBackground(new java.awt.Color(204, 204, 204));
        txt_masanpham.setText("###");
        txt_masanpham.setBorder(null);
        txt_masanpham.setOpaque(true);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        jLabel3.setText("THÔNG TIN SẢN PHẨM");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Mô tả sản phẩm");

        txt_motasanpham.setColumns(20);
        txt_motasanpham.setRows(5);
        jScrollPane1.setViewportView(txt_motasanpham);

        btn_themsanpham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/material-symbols--add-circle-outline.png"))); // NOI18N
        btn_themsanpham.setText("Thêm");
        btn_themsanpham.setBorder(null);
        btn_themsanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themsanphamActionPerformed(evt);
            }
        });

        btn_capnhatsanpham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/capnhat.png"))); // NOI18N
        btn_capnhatsanpham.setText("Cập nhật");
        btn_capnhatsanpham.setBorder(null);
        btn_capnhatsanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_capnhatsanphamActionPerformed(evt);
            }
        });

        btn_xoasanpham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/delete.png"))); // NOI18N
        btn_xoasanpham.setText("Xóa");
        btn_xoasanpham.setBorder(null);
        btn_xoasanpham.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btn_xoasanphamMouseMoved(evt);
            }
        });
        btn_xoasanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoasanphamActionPerformed(evt);
            }
        });

        btn_lammoisanpham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/lammoi.png"))); // NOI18N
        btn_lammoisanpham.setText("Làm mới");
        btn_lammoisanpham.setBorder(null);
        btn_lammoisanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lammoisanphamActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 255));
        jLabel5.setText("Trạng thái");

        buttonGroup1.add(rdo_sanphamngungban);
        rdo_sanphamngungban.setText("Ngừng bán");
        rdo_sanphamngungban.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rdo_sanphamngungbanMouseClicked(evt);
            }
        });
        rdo_sanphamngungban.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_sanphamngungbanActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdo_sanphamdangban);
        rdo_sanphamdangban.setText("Đang bán");
        rdo_sanphamdangban.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rdo_sanphamdangbanMouseClicked(evt);
            }
        });
        rdo_sanphamdangban.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdo_sanphamdangbanActionPerformed(evt);
            }
        });

        txt_timkiemsanphamtheoten.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(51, 102, 255)));
        txt_timkiemsanphamtheoten.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_timkiemsanphamtheotenFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_timkiemsanphamtheotenFocusLost(evt);
            }
        });
        txt_timkiemsanphamtheoten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_timkiemsanphamtheotenActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/first.png"))); // NOI18N
        jButton1.setBorder(null);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/before.png"))); // NOI18N
        jButton2.setBorder(null);

        jLabel6.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 255));
        jLabel6.setText("jLabel6");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/next.png"))); // NOI18N
        jButton3.setBorder(null);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/last.png"))); // NOI18N
        jButton4.setBorder(null);

        jScrollPane3.setBorder(null);
        jScrollPane3.setOpaque(false);

        tbl_sanpham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã sản phẩm", "Tên sản phẩm", "Mô tả", "Số lượng", "Trạng thái"
            }
        ));
        tbl_sanpham.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_sanpham.getTableHeader().setResizingAllowed(false);
        tbl_sanpham.getTableHeader().setReorderingAllowed(false);
        tbl_sanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_sanphamMouseClicked(evt);
            }
        });
        tbl_sanpham.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbl_sanphamPropertyChange(evt);
            }
        });
        jScrollPane3.setViewportView(tbl_sanpham);
        if (tbl_sanpham.getColumnModel().getColumnCount() > 0) {
            tbl_sanpham.getColumnModel().getColumn(0).setMinWidth(40);
            tbl_sanpham.getColumnModel().getColumn(0).setPreferredWidth(40);
            tbl_sanpham.getColumnModel().getColumn(0).setMaxWidth(40);
            tbl_sanpham.getColumnModel().getColumn(1).setMinWidth(100);
            tbl_sanpham.getColumnModel().getColumn(1).setPreferredWidth(100);
            tbl_sanpham.getColumnModel().getColumn(1).setMaxWidth(100);
            tbl_sanpham.getColumnModel().getColumn(2).setMinWidth(150);
            tbl_sanpham.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbl_sanpham.getColumnModel().getColumn(2).setMaxWidth(150);
            tbl_sanpham.getColumnModel().getColumn(4).setMinWidth(70);
            tbl_sanpham.getColumnModel().getColumn(4).setPreferredWidth(70);
            tbl_sanpham.getColumnModel().getColumn(4).setMaxWidth(70);
            tbl_sanpham.getColumnModel().getColumn(5).setMinWidth(120);
            tbl_sanpham.getColumnModel().getColumn(5).setPreferredWidth(120);
            tbl_sanpham.getColumnModel().getColumn(5).setMaxWidth(120);
        }

        lbl_danhsachsanpham.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_danhsachsanpham.setForeground(new java.awt.Color(51, 51, 255));
        lbl_danhsachsanpham.setText("Sản Phẩm Đang Bán");

        lbl_thongbaotensp.setForeground(new java.awt.Color(255, 0, 0));

        btn_khoiphucsanpham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication8/icon/lammoi.png"))); // NOI18N
        btn_khoiphucsanpham.setText("Khôi phục");
        btn_khoiphucsanpham.setBorder(null);
        btn_khoiphucsanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_khoiphucsanphamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_danhsachsanphamLayout = new javax.swing.GroupLayout(panel_danhsachsanpham);
        panel_danhsachsanpham.setLayout(panel_danhsachsanphamLayout);
        panel_danhsachsanphamLayout.setHorizontalGroup(
            panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(jLabel3))
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGap(360, 360, 360)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(100, 100, 100)
                                .addComponent(rdo_sanphamdangban))
                            .addComponent(lbl_danhsachsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_khoiphucsanpham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rdo_sanphamngungban, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_timkiemsanphamtheoten, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_thongbaotensp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_masanpham, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addComponent(txt_tensanpham, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_themsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_xoasanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_capnhatsanpham, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addComponent(btn_lammoisanpham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42))
        );
        panel_danhsachsanphamLayout.setVerticalGroup(
            panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_themsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_capnhatsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_xoasanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_lammoisanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                                .addComponent(txt_masanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tensanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_thongbaotensp, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_danhsachsanphamLayout.createSequentialGroup()
                        .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(rdo_sanphamdangban)
                            .addComponent(rdo_sanphamngungban)
                            .addComponent(txt_timkiemsanphamtheoten, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(lbl_danhsachsanpham))
                    .addComponent(btn_khoiphucsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_danhsachsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sản phẩm", panel_danhsachsanpham);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_xoasanphamMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_xoasanphamMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_xoasanphamMouseMoved

    private void txt_timkiemsanphamtheotenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_timkiemsanphamtheotenFocusGained
        if (txt_timkiemsanphamtheoten.getText().trim().equals("Nhập tên cần tìm kiếm")) {
            txt_timkiemsanphamtheoten.setText("");  // Xóa placeholder khi focus vào
            txt_timkiemsanphamtheoten.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txt_timkiemsanphamtheotenFocusGained

    private void txt_timkiemsanphamtheotenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_timkiemsanphamtheotenFocusLost
        String keyword = txt_timkiemsanphamtheoten.getText().trim();

        // Đặt lại placeholder nếu ô tìm kiếm trống
        if (keyword.isEmpty()) {
            txt_timkiemsanphamtheoten.setText("Nhập tên cần tìm kiếm");
            txt_timkiemsanphamtheoten.setForeground(Color.GRAY);
        } else {
            // Nếu ô tìm kiếm có nội dung, thực hiện tìm kiếm
            searchSanPhamTuongDoi();
        }

    }//GEN-LAST:event_txt_timkiemsanphamtheotenFocusLost

    private void rdo_sanphamngungbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_sanphamngungbanActionPerformed
        List<Model_SanPham> dsNgungBan = service_sp.layDanhSachSanPhamNgungBan();
        fillTable_SP(dsNgungBan);
        lbl_danhsachsanpham.setText("Danh sách sản phẩm ngưng bán");
        lbl_danhsachsanpham.setForeground(Color.red);
        txt_masanpham.setText("###");
        txt_tensanpham.setText("");
        txt_motasanpham.setText("");
        btn_khoiphucsanpham.setVisible(true);
    }//GEN-LAST:event_rdo_sanphamngungbanActionPerformed

    private void rdo_sanphamdangbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_sanphamdangbanActionPerformed
        List<Model_SanPham> dsDangBan = service_sp.layDanhSachSanPhamDangBan();
        fillTable_SP(dsDangBan);
        lbl_danhsachsanpham.setText("Danh sách sản phẩm đang bán");
        txt_masanpham.setText("###");
        txt_tensanpham.setText("");
        txt_motasanpham.setText("");

        btn_khoiphucsanpham.setVisible(false);

    }//GEN-LAST:event_rdo_sanphamdangbanActionPerformed

    private void btn_themsanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themsanphamActionPerformed
        String ma = CodeGeneratorUtil.generateSanPham();
        String ten = txt_tensanpham.getText().trim();
        String moTa = txt_motasanpham.getText().trim();
        int soluong = 0;

        // Kiểm tra tên không được để trống
        if (ValidationUtil.isEmpty(ten)) {
            lbl_thongbaotensp.setText("Tên sản phẩm không được để trống");
            return;
        }

        // Kiểm tra tên thuộc tính đã tồn tại chưa
        if (service_sp.kiemTraTenSanPhamDaTonTai(ten)) {
            lbl_thongbaotensp.setText("Tên sản phẩm đã tồn tại!");
            return;
        }

        // Xác nhận thêm mới
        int luachon = JOptionPane.showConfirmDialog(this, "Bạn có muốn thêm sản phẩm không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (luachon == JOptionPane.YES_OPTION) {
            boolean them = service_sp.addSanPham(ma, ten, moTa);
            if (them) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công.");
                fill_SanPhamDauTien(service_sp.layDanhSachSanPhamDangBan(), ma); // Cập nhật lại bảng sau khi thêm
                capNhatBangSanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
                loadComboboxLoaiSanPham();
                lbl_thongbaotensp.setText("");
                txt_masanpham.setText("###");
                txt_tensanpham.setText("");
                txt_motasanpham.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại.");
            }
        }
    }//GEN-LAST:event_btn_themsanphamActionPerformed

    private void btn_capnhatsanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capnhatsanphamActionPerformed

        int chon = tbl_sanpham.getSelectedRow();

        if (chon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = txt_masanpham.getText().trim();
        String ten = txt_tensanpham.getText().trim();
        String moTa = txt_motasanpham.getText().trim();

        // Lấy dữ liệu cũ từ bảng
        String tenCu = tbl_sanpham.getValueAt(chon, 2).toString().trim(); // cột 2 là tên sản phẩm
        String moTaCu = tbl_sanpham.getValueAt(chon, 3).toString().trim(); // cột 3 là mô tả (sửa nếu khác)

        if (ValidationUtil.isEmpty(ten)) {
            lbl_thongbaotensp.setText("Tên sản phẩm không được để trống!");
            return;
        }

        // Nếu không thay đổi gì
        if (ten.equals(tenCu) && moTa.equals(moTaCu)) {
            JOptionPane.showMessageDialog(this, "Bạn chưa thay đổi gì cả!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Chỉ kiểm tra trùng nếu có thay đổi tên
        if (!ten.equalsIgnoreCase(tenCu)) {
            if (service_sp.kiemTraTenSanPhamDaTonTai(ten)) {
                lbl_thongbaotensp.setText("Tên sản phẩm đã tồn tại!");
                return;
            }
        }

        // Xác nhận cập nhật
        int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật tên sản phẩm?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION) {
            boolean capNhatThanhCong = service_sp.capNhatSanPham(ma, ten, moTa);

            if (capNhatThanhCong) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                fillTable_SP(service_sp.layDanhSachSanPhamDangBan());
                for (int i = 0; i < tbl_sanpham.getRowCount(); i++) {
                    Object maTrongBang = tbl_sanpham.getValueAt(i, 1);
                    if (maTrongBang != null && ma.equals(maTrongBang.toString())) {
                        tbl_sanpham.setRowSelectionInterval(i, i);
                        tbl_sanpham.scrollRectToVisible(tbl_thuoctinh.getCellRect(i, 0, true));
                        break;
                    }
                }
                lbl_thongbaotensp.setText("");

                txt_timkiemsanphamtheoten.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_btn_capnhatsanphamActionPerformed

    private void tbl_sanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_sanphamMouseClicked
        int selectedRow = tbl_sanpham.getSelectedRow(); // Lấy chỉ số hàng được chọn
        if (selectedRow != -1) { // Kiểm tra xem có hàng nào được chọn không
            System.out.println("Giá trị ở dòng " + selectedRow + ": " + tbl_sanpham.getValueAt(selectedRow, 0));

            txt_tensanpham.setText(tbl_sanpham.getValueAt(selectedRow, 2).toString());// Lấy dữ liệu từ cột 
            txt_motasanpham.setText(tbl_sanpham.getValueAt(selectedRow, 3).toString());
            txt_masanpham.setText(tbl_sanpham.getValueAt(selectedRow, 1).toString());
        }
        lbl_thongbaotensp.setText("");


    }//GEN-LAST:event_tbl_sanphamMouseClicked

    private void btn_lammoisanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoisanphamActionPerformed
        txt_masanpham.setText("###");
        txt_tensanpham.setText("");
        txt_motasanpham.setText("");
        lbl_thongbaotensp.setText("");
        rdo_sanphamdangban.setSelected(true);
    }//GEN-LAST:event_btn_lammoisanphamActionPerformed

    private void btn_xoasanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoasanphamActionPerformed

        int chon = tbl_sanpham.getSelectedRow();

        if (chon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy tên sản phẩm từ cột thứ 2 (index = 2)
        String tenSanPham = tbl_sanpham.getValueAt(chon, 2).toString();

        // Tìm ID sản phẩm từ tên (giả sử bạn có Map tên → ID)
        Integer idSanPham = sanPhamDangBanMap.get(tenSanPham);

        // Nếu không có map thì cần gọi service/DAO để lấy ID theo tên:
        // Integer idSanPham = service_sp.getIdSanPhamTheoTen(tenSanPham);
        if (idSanPham == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID sản phẩm tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hỏi xác nhận trước khi xóa
        int luachon = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm '" + tenSanPham + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (luachon == JOptionPane.YES_OPTION) {
            boolean xoaThanhCong = service_sp.updateDaXoaSanPham(idSanPham); // gọi DAO ở trên bạn viết

            if (xoaThanhCong) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                xuLyChonRadioButton(); // Refresh bảng
                fillTable_SP(service_sp.layDanhSachSanPhamDangBan());
                //   fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
                capNhatBangSanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
                lbl_thongbaotensp.setText("");
                txt_masanpham.setText("###");
                txt_tensanpham.setText("");
                txt_motasanpham.setText("");
                txt_timkiemsanphamtheoten.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_xoasanphamActionPerformed

    private void btn_themthuoctinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themthuoctinhActionPerformed
        String ma = "";
        if (rdo_mausac.isSelected()) {
            ma = CodeGeneratorUtil.generateMauSac();
        } else if (rdo_chatlieu.isSelected()) {
            ma = CodeGeneratorUtil.generateChatLieu();
        } else if (rdo_kichthuoc.isSelected()) {
            ma = CodeGeneratorUtil.generateKichThuoc();
        }

        String ten = txt_tenthuoctinh.getText().trim();
        String tableName = getSelectedTableName();

        // Kiểm tra tên không được để trống
        if (ValidationUtil.isEmpty(ten)) {
            lbl_thongbaothuoctinh.setText("Tên thuộc tính không được để trống");
            return;
        }

        // Kiểm tra tên thuộc tính đã tồn tại chưa
        if (service_spthuoctinh.kiemTraTenThuocTinhDaTonTai(tableName, ten)) {
            JOptionPane.showMessageDialog(this, "Tên thuộc tính đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác nhận thêm mới
        int luachon = JOptionPane.showConfirmDialog(this, "Bạn có muốn thêm thuộc tính không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (luachon == JOptionPane.YES_OPTION) {
            boolean them = service_spthuoctinh.addThuocTinh(tableName, ma, ten);
            if (them) {
                loadComboboxLoaiMauSac();
                loadComboboxLoaiKichThuoc();
                loadComboboxLoaiChatlieu();
                JOptionPane.showMessageDialog(this, "Thêm thuộc tính thành công.");
                xuLyChonRadioButton(); // Cập nhật lại bảng sau khi thêm
                fillTable_SPThuocTinhDauTien(service_spthuoctinh.layDanhSachThuocTinh(tableName), ma);
                lbl_thongbaothuoctinh.setText("");
                txt_mathuoctinh.setText("###");
                txt_tenthuoctinh.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại.");
            }
        }
    }//GEN-LAST:event_btn_themthuoctinhActionPerformed

    private void btn_capnhatthuoctinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capnhatthuoctinhActionPerformed
        int chon = tbl_thuoctinh.getSelectedRow();

        if (chon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thuộc tính để cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = tbl_thuoctinh.getValueAt(chon, 1).toString(); // Lấy mã từ bảng
        String tenMoi = txt_tenthuoctinh.getText().trim().toString(); // Lấy tên mới từ text field
        String tenCu = tbl_thuoctinh.getValueAt(chon, 2).toString();
        String tableName = getSelectedTableName(); // Lấy bảng hiện tại

        if (ValidationUtil.isEmpty(tenMoi)) {
            lbl_thongbaothuoctinh.setText("Tên thuộc tính không được để trống!");
            return;
        }

        // Kiểm tra xem có thay đổi tên không
        if (tenMoi.equalsIgnoreCase(tenCu)) {
            lbl_thongbaothuoctinh.setText("Tên mới không có sự thay đổi!");
            return;
        }

        // Kiểm tra xem tên mới đã tồn tại chưa
        if (service_spthuoctinh.kiemTraTenThuocTinhDaTonTai(tableName, tenMoi)) {
            lbl_thongbaothuoctinh.setText("Tên thuộc tính đã tồn tại! Vui lòng nhập tên khác.");
            return;
        }

        // Xác nhận cập nhật
        int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật tên thuộc tính?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION) {
            boolean capNhatThanhCong = service_spthuoctinh.updateThuocTinh(tableName, tenMoi, ma);

            if (capNhatThanhCong) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");

                xuLyChonRadioButton(); // Load lại bảng sau khi cập nhật
                // Chọn lại dòng theo mã vừa cập nhật
                for (int i = 0; i < tbl_thuoctinh.getRowCount(); i++) {
                    Object maTrongBang = tbl_thuoctinh.getValueAt(i, 1);
                    if (maTrongBang != null && ma.equals(maTrongBang.toString())) {
                        tbl_thuoctinh.setRowSelectionInterval(i, i);
                        tbl_thuoctinh.scrollRectToVisible(tbl_thuoctinh.getCellRect(i, 0, true));
                        break;
                    }
                }
                lbl_thongbaothuoctinh.setText("");
                txt_mathuoctinh.setText("###");
                txt_tenthuoctinh.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_capnhatthuoctinhActionPerformed

    private void btn_xoathuoctinhMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_xoathuoctinhMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_xoathuoctinhMouseMoved

    private void btn_xoathuoctinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoathuoctinhActionPerformed
        int chon = tbl_thuoctinh.getSelectedRow();

        if (chon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thuộc tính để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenThuocTinh = tbl_thuoctinh.getValueAt(chon, 2).toString(); // Lấy TÊN thuộc tính
        String tableName = getSelectedTableName(); // Lấy tên bảng tương ứng với radio

        if (tableName == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại thuộc tính cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác định ID dựa trên tên và bảng được chọn
        Integer id = null;
        switch (tableName) {
            case "Mau_Sac":
                id = mauSacMap.get(tenThuocTinh);
                break;
            case "Kich_Thuoc":
                id = kichThuocMap.get(tenThuocTinh);
                break;
            case "Chat_Lieu":
                id = chatLieuMap.get(tenThuocTinh);
                break;
        }

        if (id == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID tương ứng với tên thuộc tính!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int luachon = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa '" + tenThuocTinh + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (luachon == JOptionPane.YES_OPTION) {
            boolean xoaThanhCong = service_spthuoctinh.xoaThuocTinhSanPham(tableName, id); // ← Gọi theo ID

            if (xoaThanhCong) {
                JOptionPane.showMessageDialog(this, "Xóa thuộc tính thành công!");
                xuLyChonRadioButton();  // Cập nhật lại bảng sau khi xóa
                loadComboboxLoaiSanPham();
                loadComboboxLoaiMauSac();
                loadComboboxLoaiKichThuoc();
                loadComboboxLoaiChatlieu();
                fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
                lbl_thongbaothuoctinh.setText("");
                txt_mathuoctinh.setText("###");
                txt_tenthuoctinh.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_xoathuoctinhActionPerformed

    private void btn_lammoithuoctinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lammoithuoctinhActionPerformed
        txt_mathuoctinh.setText("###");
        txt_tenthuoctinh.setText("");
        lbl_thongbaothuoctinh.setText("");
        rdo_mausac.setSelected(true);
    }//GEN-LAST:event_btn_lammoithuoctinhActionPerformed

    private void rdo_mausacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_mausacActionPerformed
        xuLyChonRadioButton();
        lbl_danhsachthuoctinh.setText("DANH SÁCH THUỘC TÍNH MÀU SẮC");
        txt_mathuoctinh.setText("###");
        txt_tenthuoctinh.setText("");
    }//GEN-LAST:event_rdo_mausacActionPerformed

    private void rdo_kichthuocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_kichthuocActionPerformed
        xuLyChonRadioButton();
        lbl_danhsachthuoctinh.setText("DANH SÁCH THUỘC TÍNH KÍCH THƯỚC");
        txt_mathuoctinh.setText("###");
        txt_tenthuoctinh.setText("");
    }//GEN-LAST:event_rdo_kichthuocActionPerformed

    private void rdo_chatlieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdo_chatlieuActionPerformed
        xuLyChonRadioButton();
        lbl_danhsachthuoctinh.setText("DANH SÁCH THUỘC TÍNH CHẤT LIỆU");
        txt_mathuoctinh.setText("###");
        txt_tenthuoctinh.setText("");
    }//GEN-LAST:event_rdo_chatlieuActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void btn_themmoispctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themmoispctActionPerformed
        ThemSanPhamChiTiet themSPCT = new ThemSanPhamChiTiet(this, new Runnable() {
            @Override
            public void run() {
                fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
            }
        });

        themSPCT.setVisible(true); // ✅ Hiển thị đúng đối tượng có callback
    }//GEN-LAST:event_btn_themmoispctActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void tbl_sanphamPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbl_sanphamPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_sanphamPropertyChange

    private void tbl_thuoctinhMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_thuoctinhMouseClicked
        int selectedRow = tbl_thuoctinh.getSelectedRow(); // Lấy chỉ số hàng được chọn
        if (selectedRow != -1) { // Kiểm tra xem có hàng nào được chọn không
            String maThuocTinh = tbl_thuoctinh.getValueAt(selectedRow, 1).toString();
            String tenThuocTinh = tbl_thuoctinh.getValueAt(selectedRow, 2).toString(); // Lấy dữ liệu từ cột 2
            txt_tenthuoctinh.setText(tenThuocTinh); // Hiển thị vào ô nhập liệu
            txt_mathuoctinh.setText(maThuocTinh);
        }
    }//GEN-LAST:event_tbl_thuoctinhMouseClicked

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        fillTable_SP(service_sp.layDanhSachSanPhamDangBan());
        xuLyChonRadioButton();
        //fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
        loadComboboxLoaiSanPham();
        loadComboboxLoaiMauSac();
        loadComboboxLoaiKichThuoc();
        loadComboboxLoaiChatlieu();
        rdo_sanphamdangban.setSelected(true);
        capNhatBangSanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
        macDinhCboSPCT();
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void btn_khoiphucsanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_khoiphucsanphamActionPerformed
        int chon = tbl_sanpham.getSelectedRow();

        if (chon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để khôi phục!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy tên sản phẩm từ cột thứ 2 (index = 2)
        String tenSanPham = tbl_sanpham.getValueAt(chon, 2).toString();

        // Tìm ID sản phẩm từ tên (giả sử bạn có Map tên → ID)
        Integer idSanPham = sanPhamNgungBanMap.get(tenSanPham); // sanPhamMap: Map<String, Integer>

        if (idSanPham == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID sản phẩm tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int luachon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục sản phẩm '" + tenSanPham + "'?",
                "Xác nhận", JOptionPane.YES_NO_OPTION
        );

        if (luachon == JOptionPane.YES_OPTION) {
            boolean thanhCong = service_sp.khoiPhucSanPhamDaXoa(idSanPham);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Khôi phục sản phẩm thành công!");
                xuLyChonRadioButton(); // Ví dụ: lọc lại danh sách đang chọn
                fillTable_SP(service_sp.layDanhSachSanPhamDangBan());
                fillTable_SP(service_sp.layDanhSachSanPhamNgungBan());
                fillTable_SanPhamChiTiet(service_spChiTiet.getAllSanPhamChiTiet());
                txt_timkiemsanphamtheoten.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Khôi phục thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_btn_khoiphucsanphamActionPerformed

    private void txt_timkiemsanphamtheotenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_timkiemsanphamtheotenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_timkiemsanphamtheotenActionPerformed

    private void rdo_sanphamdangbanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdo_sanphamdangbanMouseClicked

    }//GEN-LAST:event_rdo_sanphamdangbanMouseClicked

    private void rdo_sanphamngungbanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdo_sanphamngungbanMouseClicked

    }//GEN-LAST:event_rdo_sanphamngungbanMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capnhatsanpham;
    private javax.swing.JButton btn_capnhatthuoctinh;
    private javax.swing.JButton btn_khoiphucsanpham;
    private javax.swing.JButton btn_lammoisanpham;
    private javax.swing.JButton btn_lammoithuoctinh;
    private javax.swing.JButton btn_themmoispct;
    private javax.swing.JButton btn_themsanpham;
    private javax.swing.JButton btn_themthuoctinh;
    private javax.swing.JButton btn_xoasanpham;
    private javax.swing.JButton btn_xoathuoctinh;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cbo_chatlieu;
    private javax.swing.JComboBox<String> cbo_khoanggia;
    private javax.swing.JComboBox<String> cbo_kichthuoc;
    private javax.swing.JComboBox<String> cbo_mausac;
    private javax.swing.JComboBox<String> cbo_sanpham;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lbl_danhsachsanpham;
    private javax.swing.JLabel lbl_danhsachthuoctinh;
    private javax.swing.JLabel lbl_thongbaotensp;
    private javax.swing.JLabel lbl_thongbaothuoctinh;
    private javax.swing.JPanel panel_danhsachsanpham;
    private javax.swing.JRadioButton rdo_chatlieu;
    private javax.swing.JRadioButton rdo_kichthuoc;
    private javax.swing.JRadioButton rdo_mausac;
    private javax.swing.JRadioButton rdo_sanphamdangban;
    private javax.swing.JRadioButton rdo_sanphamngungban;
    private javaapplication8.swing.Table table1;
    private javaapplication8.swing.Table table2;
    private javaapplication8.swing.Table table3;
    private javaapplication8.swing.Table tbl_sanpham;
    private javax.swing.JTable tbl_sanphamchitiet;
    private javax.swing.JTable tbl_thuoctinh;
    private javax.swing.JTextField txt_masanpham;
    private javax.swing.JTextField txt_mathuoctinh;
    private javax.swing.JTextArea txt_motasanpham;
    private javax.swing.JTextField txt_tensanpham;
    private javax.swing.JTextField txt_tenthuoctinh;
    private org.jdesktop.swingx.JXTextField txt_timkiemsanphamtheoten;
    // End of variables declaration//GEN-END:variables

    private void customizeTabblePane() {
        jTabbedPane1.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x + 5, y + h - 3, w - 10, 3);
                }
            }

        });
    }

    void loadComboboxLoaiSanPham() {

        cbo_sanpham.removeAllItems();
        sanPhamDangBanMap.clear();
        sanPhamNgungBanMap.clear();

        cbo_sanpham.addItem("Tất cả"); // ✅ Thêm dòng mặc định

        List<Model_SanPham> dsSanPhamDangBan = service_sp.layDanhSachSanPhamDangBan();
        List<Model_SanPham> dsSanPhamNgungBan = service_sp.layDanhSachSanPhamNgungBan();

        for (Model_SanPham ms : dsSanPhamDangBan) {
            cbo_sanpham.addItem(ms.getTen());
            sanPhamDangBanMap.put(ms.getTen(), ms.getId());
        }

        for (Model_SanPham ms : dsSanPhamNgungBan) {
            cbo_sanpham.addItem(ms.getTen());
            sanPhamNgungBanMap.put(ms.getTen(), ms.getId());
        }

        cbo_sanpham.setSelectedItem("Tất cả"); // ✅ Chọn mặc định
    }

    void loadComboboxLoaiMauSac() {
        cbo_mausac.removeAllItems();
        mauSacMap.clear();

        cbo_mausac.addItem("Tất cả"); // ✅ Thêm dòng mặc định

        List<SanPham_ThuocTinh> dsMauSac = service_spthuoctinh.getLoaiMauSac();
        for (SanPham_ThuocTinh ms : dsMauSac) {
            cbo_mausac.addItem(ms.getTen());
            mauSacMap.put(ms.getTen(), ms.getId());
        }

        cbo_mausac.setSelectedItem("Tất cả"); // ✅ Chọn mặc định
    }

    void loadComboboxLoaiKichThuoc() {
        cbo_kichthuoc.removeAllItems();
        kichThuocMap.clear();

        cbo_kichthuoc.addItem("Tất cả"); // ✅ Thêm dòng mặc định

        List<SanPham_ThuocTinh> dsKichThuoc = service_spthuoctinh.getLoaiKichThuoc();
        for (SanPham_ThuocTinh ms : dsKichThuoc) {
            cbo_kichthuoc.addItem(ms.getTen());
            kichThuocMap.put(ms.getTen(), ms.getId());
        }

        cbo_kichthuoc.setSelectedItem("Tất cả"); // ✅ Chọn mặc định
    }

    void loadComboboxLoaiChatlieu() {
        cbo_chatlieu.removeAllItems();
        chatLieuMap.clear();

        cbo_chatlieu.addItem("Tất cả"); // ✅ Thêm dòng mặc định

        List<SanPham_ThuocTinh> dsChatLieu = service_spthuoctinh.getLoaiChatLieu();
        for (SanPham_ThuocTinh ms : dsChatLieu) {
            cbo_chatlieu.addItem(ms.getTen());
            chatLieuMap.put(ms.getTen(), ms.getId());
        }

        cbo_chatlieu.setSelectedItem("Tất cả"); // ✅ Chọn mặc định
    }

    public HashMap<String, Integer> getSanPhamDangBanMap() {
        return sanPhamDangBanMap;
    }

    public HashMap<String, Integer> getSanPhamNgungBanMap() {
        return sanPhamNgungBanMap;
    }

    public HashMap<String, Integer> getMauSacMap() {
        return mauSacMap;
    }

    public HashMap<String, Integer> getKichThuocMap() {
        return kichThuocMap;
    }

    public HashMap<String, Integer> getChatLieuMap() {
        return chatLieuMap;
    }

}
