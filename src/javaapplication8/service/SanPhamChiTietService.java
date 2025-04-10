/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaapplication8.service;

import java.util.List;
import javaapplication8.model.SanPham_ChiTiet;
import javaapplication8.model.SanPham_ThuocTinh;

/**
 *
 * @author dungc
 */
public interface SanPhamChiTietService {
    List<SanPham_ChiTiet> getAllSanPhamChiTiet();
    
    boolean addSanPhamChiTiet(String ma, int idsp, int idms, int idcl, int idkt, String donGia, int soLuong);
    
}
