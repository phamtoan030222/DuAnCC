/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication8.service.serviceimpl;

import java.util.List;
import javaapplication8.model.Model_SanPham;
import javaapplication8.service.SanPhamService;
import javaapplication8.dao.SanPhamDao;

/**
 *
 * @author dungc
 */
public class SanPhamServiceImpl implements SanPhamService {
    
    private final SanPhamDao dao = new SanPhamDao();
    
    @Override
    public List<Model_SanPham> layDanhSachSanPhamDangBan() {
        return dao.getSanPhamDangBan();
    }
    
    @Override
    public List<Model_SanPham> layDanhSachSanPhamNgungBan() {
        return dao.getSanPhamNgungBan();
    }
    
    @Override
    public boolean addSanPham(String masp, String tensp, String mota) {
        return dao.addSanPham(masp, tensp, mota);
    }
    
    @Override
    public boolean kiemTraTenSanPhamDaTonTai(String ten) {
        return dao.kiemTraTenSanPhamDaTonTai(ten);
    }
    
    @Override
    public boolean capNhatSanPham(String ma, String ten, String moTa) {
        return dao.capNhatSanPham(ma, ten, moTa);
    }
    
    @Override
    public boolean updateDaXoaSanPham(int id) {
        return dao.updateDaXoaSanPham(id);
    }

    @Override
    public List<Model_SanPham> loadTableTimKiemTuongDoi(String keyword, int daXoa) {
        return dao.loadTableTimKiemTuongDoi(keyword, daXoa);
    }
    
    @Override
    public boolean khoiPhucSanPhamDaXoa(int id) {
        return dao.khoiPhucSanPhamDaXoa(id);
    }
    
}
