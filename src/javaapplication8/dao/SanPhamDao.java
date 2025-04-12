/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication8.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javaapplication8.model.Model_SanPham;
import javaapplication8.until.DBConnect;

/**
 *
 * @author dungc
 */
public class SanPhamDao {

    private ArrayList<Model_SanPham> list;
    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private String sql;

    public SanPhamDao() {
        conn = DBConnect.getConnection();

        list = new ArrayList<>();
    }

    public ArrayList<Model_SanPham> getSanPhamDangBan() {
        ArrayList<Model_SanPham> list = new ArrayList<>();
        sql = """
            SELECT 
                      sp.ID, 
                      MA, 
                      TEN, 
                      MO_TA, 
                      SUM(ISNULL(ct.SO_LUONG, 0)) AS TongSoLuong, 
                      sp.DA_XOA
                  FROM San_Pham sp
                  LEFT JOIN San_Pham_Chi_Tiet ct ON sp.ID = ct.ID_SAN_PHAM AND ct.DA_XOA = 0
                  WHERE sp.DA_XOA = 0
                  GROUP BY 
                      sp.ID, 
                      MA, 
                      TEN, 
                      MO_TA, 
                      sp.DA_XOA
              """;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String maSP = rs.getString("MA");
                String ten = rs.getString("TEN");
                String moTa = rs.getString("MO_TA");
                int SoLuong = rs.getInt("TongSoLuong");
                boolean daXoa = rs.getBoolean("DA_XOA");
                list.add(new Model_SanPham(id, maSP, ten, moTa, SoLuong, daXoa));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Model_SanPham> getSanPhamNgungBan() {
        ArrayList<Model_SanPham> list = new ArrayList<>();
        sql = """
           SELECT 
                      San_Pham.ID, 
                      MA, 
                      TEN, 
                      MO_TA, 
                      SUM(ISNULL(San_Pham_Chi_Tiet.SO_LUONG, 0)) AS TongSoLuong, 
                      San_Pham.DA_XOA
                  FROM San_Pham
                  LEFT JOIN San_Pham_Chi_Tiet 
                      ON San_Pham.ID = San_Pham_Chi_Tiet.ID_SAN_PHAM
                  WHERE San_Pham.DA_XOA = 1
                  GROUP BY 
                      San_Pham.ID, 
                      MA, 
                      TEN, 
                      MO_TA, 
                      San_Pham.DA_XOA
              """;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String maSP = rs.getString("MA");
                String ten = rs.getString("TEN");
                String moTa = rs.getString("MO_TA");
                int SoLuong = rs.getInt("TongSoLuong");
                boolean daXoa = rs.getBoolean("DA_XOA");
                list.add(new Model_SanPham(id, maSP, ten, moTa, SoLuong, daXoa));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addSanPham(String masp, String tensp, String mota) {
        sql = "insert into San_Pham (MA, TEN, MO_TA) values (?,?,?)";
        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, masp);
            ps.setString(2, tensp);
            ps.setString(3, mota);
            return ps.executeUpdate() > 0;//thêm/sửa/xoá:executeUpđate()
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean kiemTraTenSanPhamDaTonTai(String ten) {
        String sql = "select count(*) from San_Pham where TEN = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setNString(1, ten);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Tên đã tồn tại
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Tên chưa tồn tại
    }

    public boolean capNhatSanPham(String ma, String ten, String moTa) {
        sql = "UPDATE San_Pham SET TEN = ?, MO_TA = ? WHERE MA = ?";

        try {
            ps = conn.prepareStatement(sql);

            ps.setNString(1, ten);
            ps.setNString(2, moTa);
            ps.setNString(3, ma);

            return ps.executeUpdate() > 0;//thêm/sửa/xoá:executeUpđate()

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateDaXoaSanPham(int id) {

        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {

            conn.setAutoCommit(false); // Tắt auto commit để kiểm soát transaction

            // Câu lệnh UPDATE đầu tiên: Cập nhật DA_XOA trong tableName
            String sql1 = """
                          update San_Pham_Chi_Tiet 
                          set DA_XOA = 1
                          where ID_SAN_PHAM = ?
                          """;
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, id);
            int rows1 = ps1.executeUpdate(); // Thực thi lệnh 1

            // Câu lệnh UPDATE thứ hai: Cập nhật DA_XOA trong bảng San_Pham_Chi_Tiet
            String sql2 = """
                          update San_Pham
                          set DA_XOA = 1
                          where ID = ?
                          """;
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, id);
            int rows2 = ps2.executeUpdate(); // Thực thi lệnh 2

            conn.commit(); // Nếu cả hai UPDATE thành công, commit transaction
            return rows1 > 0 || rows2 > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi, rollback để không bị cập nhật lỗi một phần
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Model_SanPham> loadTableTimKiemTuongDoi(String keyword, int daXoa) {
        ArrayList<Model_SanPham> list = new ArrayList<Model_SanPham>();

        String sql = """
                        SELECT 
                                              sp.ID, 
                                              MA, 
                                              TEN, 
                                              MO_TA, 
                                              SUM(ISNULL(ct.SO_LUONG, 0)) AS TongSoLuong, 
                                              sp.DA_XOA
                                          FROM San_Pham sp
                                          LEFT JOIN San_Pham_Chi_Tiet ct ON sp.ID = ct.ID_SAN_PHAM AND ct.DA_XOA = ?
                                          WHERE sp.DA_XOA = ? AND sp.TEN LIKE ?
                                          GROUP BY 
                                              sp.ID, 
                                              MA, 
                                              TEN, 
                                              MO_TA, 
                                              sp.DA_XOA 
                 """;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(3, "%" + keyword + "%"); // LIKE tìm kiếm
            ps.setInt(2, daXoa);
             ps.setInt(1, daXoa);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("ID");
                String maSP = rs.getString("MA");
                String ten = rs.getString("TEN");
                String moTa = rs.getString("MO_TA");
                int SoLuong = rs.getInt("TongSoLuong");
                boolean daXoa1 = rs.getBoolean("DA_XOA");
                list.add(new Model_SanPham(id, maSP, ten, moTa, SoLuong, daXoa1));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean khoiPhucSanPhamDaXoa(int id) {

        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {

            conn.setAutoCommit(false); // Tắt auto commit để kiểm soát transaction

            // Câu lệnh UPDATE đầu tiên: Cập nhật DA_XOA trong tableName
            String sql1 = """
                           update San_Pham
                           set DA_XOA = 0
                           where ID = ?
                          
                          """;
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, id);
            int rows1 = ps1.executeUpdate(); // Thực thi lệnh 1

            // Câu lệnh UPDATE thứ hai: Cập nhật DA_XOA trong bảng San_Pham_Chi_Tiet
            String sql2 = """
                         update San_Pham_Chi_Tiet 
                         set DA_XOA = 0
                         where ID_SAN_PHAM = ?
                          """;
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, id);
            int rows2 = ps2.executeUpdate(); // Thực thi lệnh 2

            conn.commit(); // Nếu cả hai UPDATE thành công, commit transaction
            return rows1 > 0 || rows2 > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi, rollback để không bị cập nhật lỗi một phần
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return false;
    }

}
