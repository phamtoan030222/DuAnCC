package javaapplication8.service.serviceimpl;

import java.util.List;
import javaapplication8.dao.HoaDonDao;
import javaapplication8.model.HoaDon_Model;
import javaapplication8.service.HoaDonService;

public class HoaDonServiceImpl implements HoaDonService{
    
    private final HoaDonDao hoaDonDao = new HoaDonDao();

    @Override
    public boolean taoNhanhHoaDonS(String mahd, int idNV, String ngay) {
        return hoaDonDao.taoNhanhHoaDon(mahd,idNV,ngay);
    }

    @Override
    public List<HoaDon_Model> danhSachHoaDon() {
        return hoaDonDao.danhSachHoaDon();
    }


    
}
