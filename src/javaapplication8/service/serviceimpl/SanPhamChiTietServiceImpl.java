/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication8.service.serviceimpl;

import java.util.List;
import javaapplication8.dao.SanPhamChiTietDao;
import javaapplication8.model.SanPham_ChiTiet;
import javaapplication8.service.SanPhamChiTietService;

/**
 *
 * @author dungc
 */
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService{
    
    private final SanPhamChiTietDao dao = new SanPhamChiTietDao();

    @Override
    public List<SanPham_ChiTiet> getAllSanPhamChiTiet() {
        return dao.getSanPhamChiTiet();
    }
    
}
