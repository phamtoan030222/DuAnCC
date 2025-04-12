/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaapplication8.service;

import java.util.List;
import javaapplication8.model.Model_SanPham;

/**
 *
 * @author dungc
 */
public interface SanPhamService {
    List<Model_SanPham> layDanhSachSanPhamDangBan();
    
    List<Model_SanPham> layDanhSachSanPhamNgungBan();
    
    boolean addSanPham(String masp, String tensp, String mota);
    
    boolean kiemTraTenSanPhamDaTonTai(String ten);
    
    boolean capNhatSanPham(String ma, String ten, String moTa);
    
    boolean updateDaXoaSanPham(int id);

    List<Model_SanPham> loadTableTimKiemTuongDoi(String keyword, int daXoa);
    
    boolean khoiPhucSanPhamDaXoa(int id);
}
