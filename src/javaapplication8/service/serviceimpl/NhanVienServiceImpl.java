package javaapplication8.service.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import javaapplication8.dao.NhanVienDao;
import javaapplication8.model.Model_NhanVien;
import javaapplication8.service.NhanVienService;

public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienDao nvDao = new NhanVienDao();

    @Override
    public Model_NhanVien kiemTraDangNhap(String tenDN, String mk) {
        return nvDao.kiemTraDangNhap(tenDN, mk);
    }

    @Override
    public Model_NhanVien kiemTraEmail(String email) {
        return nvDao.kiemTraEmail(email);
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        return nvDao.capNhatMatKhau(email, newPassword);
    }

    @Override
    public List<Model_NhanVien> getPage(int offset, int limit) throws SQLException {
        return nvDao.getPage(offset, limit);
    }

    @Override
    public int countAll() throws SQLException {
        return nvDao.countAll();
    }

}
