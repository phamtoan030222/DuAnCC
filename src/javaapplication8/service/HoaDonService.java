package javaapplication8.service;

import java.util.List;
import javaapplication8.model.HoaDon_Model;

public interface HoaDonService {
    
    boolean taoNhanhHoaDonS(String mahd, int idNV, String ngay);
    
    List<HoaDon_Model> danhSachHoaDon();
    
}
