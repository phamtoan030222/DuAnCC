package javaapplication8.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javaapplication8.model.SanPham_ChiTiet;
import javaapplication8.until.DBConnect;

public class SanPhamChiTietDao {
    private ArrayList<SanPham_ChiTiet> list;
    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private String sql;

    public SanPhamChiTietDao() {
        conn = DBConnect.getConnection();

        list = new ArrayList<>();
    }

    public ArrayList<SanPham_ChiTiet> getSanPhamChiTiet() {
        ArrayList<SanPham_ChiTiet> list = new ArrayList();

        sql = """
              SELECT San_Pham_Chi_Tiet.ID, San_Pham_Chi_Tiet.MA_SPCT, San_Pham.TEN, Mau_Sac.TEN, Chat_Lieu.TEN, Kich_Thuoc.TEN, FORMAT(San_Pham_Chi_Tiet.Don_Gia, 'N0') + ' VNĐ' , San_Pham_Chi_Tiet.SO_LUONG
              FROM San_Pham_Chi_Tiet 
              JOIN San_Pham ON San_Pham.ID = San_Pham_Chi_Tiet.ID_SAN_PHAM
              JOIN Mau_Sac ON Mau_Sac.ID = San_Pham_Chi_Tiet.ID_MAU_SAC
              JOIN Chat_Lieu ON Chat_Lieu.ID = San_Pham_Chi_Tiet.ID_CHAT_LIEU
              JOIN Kich_Thuoc ON Kich_Thuoc.ID = San_Pham_Chi_Tiet.ID_KICH_THUOC
              WHERE San_Pham_Chi_Tiet.DA_XOA = 0 AND San_Pham.DA_XOA = 0 AND Mau_Sac.DA_XOA = 0 AND Chat_Lieu.DA_XOA = 0 AND Kich_Thuoc.DA_XOA = 0 
              """;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {

                String id;
                String maSpct;
                String sp;
                String mauSac;
                String kichThuoc;
                String chatLieu;
                String donGia;
                int soLuong;

                maSpct = rs.getString(2);
                sp = rs.getString(3);
                kichThuoc = rs.getString(4);
                mauSac = rs.getString(5);
                chatLieu = rs.getString(6);
                donGia = rs.getString(7);
                soLuong = rs.getInt(8);

                list.add(new SanPham_ChiTiet(maSpct, sp,kichThuoc, mauSac, chatLieu, donGia, soLuong));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addSanPhamChiTiet(String ma, int idsp, int idms, int idcl, int idkt, String donGia, int soLuong) {
        sql = """
              insert into San_Pham_Chi_Tiet 
               ( MA_SPCT, ID_SAN_PHAM, ID_MAU_SAC, ID_CHAT_LIEU, ID_KICH_THUOC, DON_GIA, SO_LUONG) 
              values ( ?, ?, ?, ?, ?, ?, ?)
              """;
        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, ma);
            ps.setInt(2, idsp);
            ps.setInt(3, idms);
            ps.setInt(4, idcl);
            ps.setInt(5, idkt);
            ps.setString(6, donGia);
            ps.setInt(7, soLuong);

            return ps.executeUpdate() > 0;//thêm/sửa/xoá:executeUpđate()
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean kiemTraMaSPCTDaTonTai(String ma) {
        String sql = "SELECT COUNT(*) FROM San_Pham_Chi_Tiet WHERE MA_SPCT = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setNString(1, ma);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Tên đã tồn tại
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Tên chưa tồn tại
    }

    public boolean updateSanPham(String ma, String ten, int idsp, int idkt, int idms, int idcl, String donGia, int soLuong) {
        sql = "UPDATE San_Pham_Chi_Tiet set  TEN_SP = ? , ID_SAN_PHAM = ? , ID_KICH_THUOC = ? , ID_MAU_SAC = ? , ID_CHAT_LIEU = ? , DON_GIA = ? , SO_LUONG = ?  where MA_SPCT = ? ";
        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, ten);
            ps.setInt(2, idsp);
            ps.setInt(3, idkt);
            ps.setInt(4, idms);
            ps.setInt(5, idcl);
            ps.setString(6, donGia);
            ps.setInt(7, soLuong);
            ps.setString(8, ma);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean xoaSPCT(String ma) {
        sql = """
              update San_Pham_Chi_Tiet 
              set DA_XOA =1 
              where MA_SPCT = ?
              """;

        try {
            ps = conn.prepareStatement(sql);

            ps.setNString(1, ma);

            return ps.executeUpdate() > 0;//thêm/sửa/xoá:executeUpđate()

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<SanPham_ChiTiet> getSanPhamChiTietDaXoa() {
        ArrayList<SanPham_ChiTiet> list = new ArrayList();

        sql = """
              select San_Pham_Chi_Tiet.MA_SPCT, San_Pham_Chi_Tiet.TEN_SP, San_Pham.Ten,
                  Kich_Thuoc.Ten, Mau_Sac.Ten, Chat_Lieu.Ten, FORMAT(San_Pham_Chi_Tiet.Don_Gia, 'N0') + ' VNĐ' , San_Pham_Chi_Tiet.So_Luong
              from San_Pham_Chi_Tiet
              join San_Pham on San_Pham_Chi_Tiet.ID_SAN_PHAM = San_Pham.ID
              join Kich_Thuoc on San_Pham_Chi_Tiet.ID_Kich_Thuoc = Kich_Thuoc.ID
              join Mau_Sac on San_Pham_Chi_Tiet.ID_Mau_Sac = Mau_Sac.ID
              join Chat_Lieu on San_Pham_Chi_Tiet.ID_Chat_Lieu = Chat_Lieu.ID
              
               WHERE San_Pham_Chi_Tiet.DA_XOA = 1 OR (San_Pham.DA_XOA = 1 OR Kich_Thuoc.DA_XOA = 1 OR Mau_Sac.DA_XOA = 1 OR Chat_Lieu.DA_XOA = 1 )
               
              """;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {

                String maSp;
                String tenSp;
                String loaiSp;
                String kichThuoc;
                String mauSac;
                String chatLieu;
                String donGia;
                int soLuong;

                maSp = rs.getString(1);
                tenSp = rs.getString(2);
                loaiSp = rs.getString(3);
                kichThuoc = rs.getString(4);
                mauSac = rs.getString(5);
                chatLieu = rs.getString(6);
                donGia = rs.getString(7);
                soLuong = rs.getInt(8);

//                list.add(new SanPham_ChiTiet(maSp, tenSp, loaiSp, kichThuoc, mauSac, chatLieu, donGia, soLuong));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean khoiPhucDaXoaSPCT(String ma, int idsp, int idms, int idkt, int idcl) {
        String sql1 = "UPDATE San_Pham SET DA_XOA = 0 WHERE San_Pham.ID = ?";
        String sql2 = "UPDATE Mau_Sac SET DA_XOA = 0 WHERE Mau_Sac.ID = ?";
        String sql3 = "UPDATE Kich_Thuoc SET DA_XOA = 0 WHERE Kich_Thuoc.ID = ?";
        String sql4 = "UPDATE Chat_Lieu SET DA_XOA = 0 WHERE Chat_Lieu.ID = ?";
        String sql5 = "UPDATE San_Pham_Chi_Tiet SET DA_XOA = 0 WHERE MA_SPCT = ?";

        try {
            conn.setAutoCommit(false); // Tắt auto commit để kiểm soát transaction

            // Thực hiện các câu lệnh update
            try (PreparedStatement ps = conn.prepareStatement(sql5)) {
                ps.setString(1, ma);
                System.out.println(ma);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, idsp);
                System.out.println(idsp);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, idms);
                System.out.println(idms);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setInt(1, idkt);
                System.out.println(idkt);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sql4)) {
                ps.setInt(1, idcl);
                System.out.println(idcl);
                ps.executeUpdate();
            }

            conn.commit(); // Commit nếu tất cả đều thành công
            return true; // Thành công

        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<SanPham_ChiTiet> loadTableTimKiemTuongDoi(String keyword) {
        ArrayList<SanPham_ChiTiet> list = new ArrayList<SanPham_ChiTiet>();

        String sql = """
                  select San_Pham_Chi_Tiet.MA_SPCT, San_Pham_Chi_Tiet.TEN_SP, San_Pham.Ten,
                                   Kich_Thuoc.Ten, Mau_Sac.Ten, Chat_Lieu.Ten, FORMAT(San_Pham_Chi_Tiet.Don_Gia, 'N0') + ' VNĐ' , San_Pham_Chi_Tiet.So_Luong
                               from San_Pham_Chi_Tiet
                               join San_Pham on San_Pham_Chi_Tiet.ID_SAN_PHAM = San_Pham.ID
                               join Kich_Thuoc on San_Pham_Chi_Tiet.ID_Kich_Thuoc = Kich_Thuoc.ID
                               join Mau_Sac on San_Pham_Chi_Tiet.ID_Mau_Sac = Mau_Sac.ID
                               join Chat_Lieu on San_Pham_Chi_Tiet.ID_Chat_Lieu = Chat_Lieu.ID
                                WHERE San_Pham_Chi_Tiet.DA_XOA = 0 AND San_Pham.DA_XOA = 0 AND Kich_Thuoc.DA_XOA = 0 AND Mau_Sac.DA_XOA = 0 AND Chat_Lieu.DA_XOA = 0 AND San_Pham_Chi_Tiet.TEN_SP LIKE ?
                               
                 """;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%"); // LIKE tìm kiếm
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String maSp;
                String tenSp;
                String loaiSp;
                String kichThuoc;
                String mauSac;
                String chatLieu;
                String donGia;
                int soLuong;

                maSp = rs.getString(1);
                tenSp = rs.getString(2);
                loaiSp = rs.getString(3);
                kichThuoc = rs.getString(4);
                mauSac = rs.getString(5);
                chatLieu = rs.getString(6);
                donGia = rs.getString(7);
                soLuong = rs.getInt(8);

//                list.add(new SanPham_ChiTiet(maSp, tenSp, loaiSp, kichThuoc, mauSac, chatLieu, donGia, soLuong));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
