package javaapplication8.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javaapplication8.model.HoaDon_Model;
import javaapplication8.until.DBConnect;

public class HoaDonDao {
    
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private List<HoaDon_Model> list;

    public HoaDonDao() {
        conn = DBConnect.getConnection();
        
        list = new ArrayList<>();
    }
    
    //Tạo nhanh hóa đơn:
    
    public boolean taoNhanhHoaDon(String mahd, int idNV, String ngay) {
    String countSql = "SELECT COUNT(*) FROM Hoa_Don WHERE TRANG_THAI = 0";
    String insertSql = "insert into Hoa_Don(MA_HD,ID_NV,NGAY_TAO, TRANG_THAI) values (?,?,?,0);";

    try {
        // Kiểm tra số lượng hóa đơn chưa thanh toán
        ps = conn.prepareStatement(countSql);
        rs = ps.executeQuery();

        if (rs.next()) {
            int count = rs.getInt(1);
            if (count >= 10) {
               return false;
            }
        }

        // Nếu chưa vượt quá 10 -> Insert
        ps = conn.prepareStatement(insertSql);
        ps.setString(1, mahd);
        ps.setInt(2, idNV);
        ps.setString(3, ngay);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return false;
}
    
    public List<HoaDon_Model> danhSachHoaDon () {
    List<HoaDon_Model> list = new ArrayList<>();
    HoaDon_Model model = new HoaDon_Model();
    String sql = """
    SELECT 
        Hoa_Don.ID,
        Hoa_Don.ID_KH,
        Hoa_Don.ID_NV,
        Hoa_Don.ID_PGG,
        Hoa_Don.ID_THANH_TOAN,
        Hoa_Don.TONG_TIEN,
        Hoa_Don.MA_HD,
        Hoa_Don.NGAY_TAO,
        NhanVien.MA_NV AS MA_NV,
        Khach_Hang.TEN_KHACH_HANG AS TEN_KH
    FROM Hoa_Don 
    LEFT JOIN Khach_Hang ON Khach_Hang.id = Hoa_Don.ID_KH
    LEFT JOIN NhanVien ON NhanVien.id = Hoa_Don.ID_NV
    LEFT JOIN Phieu_Giam_Gia ON Phieu_Giam_Gia.id = Hoa_Don.ID_PGG
    LEFT JOIN Hinh_Thuc_Thanh_Toan ON Hinh_Thuc_Thanh_Toan.id = Hoa_Don.ID_THANH_TOAN
    WHERE Hoa_Don.TRANG_THAI = 0
""";


    try {
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {                
            
            model.setId(rs.getInt("ID"));
            model.setIdKH(rs.getInt("ID_KH"));
            model.setMaNV(rs.getString("MA_NV"));
            model.setIdNV(rs.getInt("ID_NV"));
            model.setIdPGG(rs.getInt("ID_PGG"));
            model.setIdTT(rs.getInt("ID_THANH_TOAN"));
            model.setTenKhachHang(rs.getString("TEN_KH"));
            model.setTongTien(rs.getString("TONG_TIEN"));
            list.add(model);
        }
    } catch (Exception e) {
        System.err.println("Lỗi khi truy vấn danh sách hóa đơn:");
        e.printStackTrace();
    }

    return list;
}

    
}
