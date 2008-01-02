// EnrollService.java
// Copyright 2003 Le Ngoc Quoc Khanh.
// Lop implement cua interface Enroll
// Truc tiep thuc thi cac phuong thuc cua dich vu JAX-RPC
package enroll;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Vector;

import sun.net.smtp.SmtpClient;

// Phai implements Enroll
public class EnrollService implements Enroll {
    // Cac hang so cau hinh cua dich vu
    // Ten server
    public static final String SERVER_NAME = "localhost";
    // Cong server
    public static final String SERVER_PORT = "1433";
    // Ten Database User
    public static final String USER = "sa";
    // Password cua Database User
    public static final String PASSWORD = "";
    // Ten cua co so du lieu
    public static final String DATABASE_NAME = "Tuyensinh";
    // Driver de truy xuat truc tiep co so du lieu SQL.
    // Driver cho Java nay do Microsoft cung cap
    public static final String DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    // Chuoi Connection URL de ket noi den co so du lieu SQL Server
    public static final String CONN_URL = "jdbc:microsoft:sqlserver://"
            + SERVER_NAME + ":" + SERVER_PORT + ";DatabaseName="
            + DATABASE_NAME;

    private static String errorString(String message) {
        return "TranslationServiceError: '" + message + "'";
    }

    // Tim ho so thi sinh theo ho, ten, phai, ngaysinh, CMND, ten huyen, ten
    // tinh,
    // ma truong, ma nganh.
    // Cung tra ve tung tap ket qua de bao dam client di dong co kha nang xu ly
    // duoc
    public String[] findRecord(String ho, String ten, int gender,
            String birthday, String id, String district, String province,
            String univ, String branch, int index, int maxCount)
            throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement;
            dbStatement = dbConnection
                    .prepareStatement("EXECUTE findRecord @ho = N'" + ho
                            + "', @ten = N'" + ten + "', @gender = " + gender
                            + ", @birthday = '" + birthday + "', @id = '" + id
                            + "', @district = N'" + district
                            + "', @province = N'" + province + "', @univ = '"
                            + univ + "', @branch = '" + branch + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData && i < index * maxCount; i++) {
                result_rs_hasData = result_rs.next();
            }
            for (int i = 0; i < maxCount && result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("mahoso"));
                list_result.add((String) result_rs.getObject("hoten"));
                list_result.add((String) result_rs.getObject("phai"));

                Object birth_obj = result_rs.getObject("ngaysinh");
                if (birth_obj != null) {
                    Calendar birthDate = Calendar.getInstance();
                    birthDate.setTime((java.util.Date) birth_obj);
                    StringBuffer birthDateSB = new StringBuffer();
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.DATE)));
                    birthDateSB.append("/");
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.MONTH) + 1));
                    birthDateSB.append("/");
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.YEAR)));
                    list_result.add(birthDateSB.toString());
                } else
                    list_result.add("null");

                list_result.add((String) result_rs.getObject("CMND"));
                list_result.add((String) result_rs.getObject("sbd"));
                list_result.add((String) result_rs.getObject("tenhuyen"));
                list_result.add((String) result_rs.getObject("tentinh"));
                list_result.add((String) result_rs.getObject("diachi"));
                list_result.add((String) result_rs.getObject("doituong"));
                list_result.add((String) result_rs.getObject("kvut"));

                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Tim truong dai hoc theo cac du kien:
    // ma truong, ten truong, khoi thi, nganh tuyen
    // Tuong tu nhu phuong thuc tren, cung tra ve theo tap ket qua
    public String[] findUniv(String univCode, String univName, String block,
            String branch, int index, int maxCount) throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement;
            dbStatement = dbConnection
                    .prepareStatement("EXECUTE findUniv @univCode = '"
                            + univCode + "', @univName = N'" + univName
                            + "', @block = '" + block + "', @branch = N'"
                            + branch + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            // So luong ket qua tong cong
            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData && i < index * maxCount; i++) {
                result_rs_hasData = result_rs.next();
            }
            // Tap ket qua can lay
            for (int i = 0; i < maxCount && result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("truong"));
                list_result.add((String) result_rs.getObject("tentruong"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Tim KVUT ung voi ten xa, ten huyen va ten tinh
    public String[] findZone(String communeName, String districtName,
            String provinceName) throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE findZone @communeName = N'"
                            + communeName + "', @districtName = N'"
                            + districtName + "', @provinceName = N'"
                            + provinceName + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            if (!result_rs_hasData) {
                result_rs.close();
                dbStatement = null;
                dbStatement = dbConnection
                        .prepareStatement("EXECUTE findZoneZero @districtName = N'"
                                + districtName
                                + "', @provinceName = N'"
                                + provinceName + "'");
                result_rs = dbStatement.executeQuery();
                result_rs_hasData = result_rs.next();
            }

            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("kvut"));
                list_result.add((String) result_rs.getObject("tenxa"));
                list_result.add((String) result_rs.getObject("xa"));
                list_result.add((String) result_rs.getObject("tenhuyen"));
                list_result.add((String) result_rs.getObject("huyen"));
                list_result.add((String) result_rs.getObject("tentinh"));
                list_result.add((String) result_rs.getObject("tinh"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay danh sach tat ca cac khoi thi
    public String[] getBlockList() throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getBlockList");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("khoi"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin chi tiet cua nganh tuyen
    // Du kien cung cayp la ma truong, khoi, va ma nganh
    public String[] getBranchDetail(String univCode, String blockCode,
            String branchCode) throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getBranchDetail @univCode = '"
                            + univCode + "', @blockCode = '" + blockCode
                            + "', @branchCode = '" + branchCode + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            if (result_rs_hasData) {
                list_result.add((String) result_rs.getObject("nganh"));
                list_result.add((String) result_rs.getObject("tennganh"));
                list_result.add((String) result_rs.getObject("khoi"));
                list_result.add((String) result_rs.getObject("monthi1"));
                list_result.add(String.valueOf(result_rs.getObject("heso1")));
                list_result.add((String) result_rs.getObject("monthi2"));
                list_result.add(String.valueOf(result_rs.getObject("heso2")));
                list_result.add((String) result_rs.getObject("monthi3"));
                list_result.add(String.valueOf(result_rs.getObject("heso3")));
                list_result.add((String) result_rs.getObject("monthi4"));
                list_result.add(String.valueOf(result_rs.getObject("heso4")));
                list_result.add(String.valueOf(result_rs.getObject("chitieu")));
                list_result.add((String) result_rs.getObject("ghichu"));
            }

        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay danh sach cac nganh tuyen cua mot truong dai hoc, voi du kien la ma
    // truong
    // Cung tra ve theo tap ket qua
    public String[] getBranchList(String univCode, int index, int maxCount)
            throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getBranchList @univCode = '"
                            + univCode + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData && i < index * maxCount; i++) {
                result_rs_hasData = result_rs.next();
            }
            for (int i = 0; i < maxCount && result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("tennganh"));
                list_result.add((String) result_rs.getObject("khoi"));
                list_result.add((String) result_rs.getObject("nganh"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Xem ket qua tuyen sinh cua thi sinh theo ma ho so, so bao danh, ho, ten,
    // phai, ngay sinh,
    // CMND, ten huyen, ten tinh, ma truong, ma nganh
    // Cung tra ve tung tap ket qua de thi sinh chon lua
    public String[] getMark(String mahoso, String sbd, String ho, String ten,
            int gender, String birthday, String id, String district,
            String province, String univ, String branch, int index, int maxCount)
            throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement;
            dbStatement = dbConnection
                    .prepareStatement("EXECUTE getMark @mahoso = '" + mahoso
                            + "', @sbd = '" + sbd + "', @ho = N'" + ho
                            + "', @ten = N'" + ten + "', @gender = " + gender
                            + ", @birthday = '" + birthday + "', @id = '" + id
                            + "', @district = N'" + district
                            + "', @province = N'" + province + "', @univ = '"
                            + univ + "', @branch = '" + branch + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData && i < index * maxCount; i++) {
                result_rs_hasData = result_rs.next();
            }
            for (int i = 0; i < maxCount && result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("sbd"));
                list_result.add((String) result_rs.getObject("hoten"));
                list_result.add((String) result_rs.getObject("phai"));

                Object birth_obj = result_rs.getObject("ngaysinh");
                if (birth_obj != null) {
                    Calendar birthDate = Calendar.getInstance();
                    birthDate.setTime((java.util.Date) birth_obj);
                    StringBuffer birthDateSB = new StringBuffer();
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.DATE)));
                    birthDateSB.append("/");
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.MONTH) + 1));
                    birthDateSB.append("/");
                    birthDateSB.append(String.valueOf(birthDate
                            .get(Calendar.YEAR)));
                    list_result.add(birthDateSB.toString());
                } else
                    list_result.add("null");

                list_result.add((String) result_rs.getObject("tenhuyen"));
                list_result.add((String) result_rs.getObject("tentinh"));
                list_result.add((String) result_rs.getObject("diachi"));
                list_result.add((String) result_rs.getObject("doituong"));
                list_result.add((String) result_rs.getObject("kvut"));
                list_result.add((String) result_rs.getObject("tentruong"));
                list_result.add((String) result_rs.getObject("tennganh"));
                list_result.add(String.valueOf(result_rs.getObject("dm1")));
                list_result.add(String.valueOf(result_rs.getObject("dm2")));
                list_result.add(String.valueOf(result_rs.getObject("dm3")));
                list_result.add(String.valueOf(result_rs.getObject("dm4")));
                list_result.add(String.valueOf(result_rs
                        .getObject("diemthuong")));
                list_result.add(String.valueOf(result_rs.getObject("dtc")));

                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay danh sach cac nguyen vong cua thi sinh theo ma ho so
    public String[] getNguyenvong(String record) throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getNguyenvong @record = '"
                            + record + "'");

            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add(String.valueOf(result_rs
                        .getObject("nguyenvong")));
                list_result.add((String) result_rs.getObject("tentruong"));
                list_result.add((String) result_rs.getObject("truong"));
                list_result.add((String) result_rs.getObject("tennganh"));
                list_result.add((String) result_rs.getObject("nganh"));
                list_result.add((String) result_rs.getObject("khoi"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin tat ca cac loai doi tuong tuyen sinh
    // Tat ca cac phuong thuc cua JAX-RPC bat buoc phai throws RemoteException
    public String[] getObjectInfo() throws RemoteException {
        // Vector ket qua se tra ve
        Vector list_result = new Vector();
        try {
            // Thiet lap ket noi
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            // Thuc thi cau lenh SQL, tra ve result set
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getObjectInfo");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            // Duyet result set, ghi ket qua vao Vector list_result
            for (int i = 0; result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("doituong"));
                list_result.add((String) result_rs.getObject("tendoituong"));
                list_result.add((String) result_rs.getObject("nhomuutien"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            // Neu loi xay ra, nem ra ngoai le RemoteException
            throw new RemoteException(e.getMessage());
        }
        // Tra ve ket qua la mang String[]
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin cac dot thi
    // Chi tiet thuc hien tuong tu nhu tren
    public String[] getStageInfo() throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getStageInfo");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add(String.valueOf(result_rs.getObject("dot")));

                // Lay ngaybatdau la mot doi tuong Date
                // Chuyen doi doi tuong Date cua Java thanh chuoi ngay/thang/nam
                Object start_obj = result_rs.getObject("ngaybatdau");
                if (start_obj != null) {
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime((java.util.Date) start_obj);
                    StringBuffer startDateSB = new StringBuffer();
                    startDateSB.append(String.valueOf(startDate
                            .get(Calendar.DATE)));
                    startDateSB.append("/");
                    startDateSB.append(String.valueOf(startDate
                            .get(Calendar.MONTH) + 1));
                    startDateSB.append("/");
                    startDateSB.append(String.valueOf(startDate
                            .get(Calendar.YEAR)));
                    list_result.add(startDateSB.toString());
                } else
                    list_result.add("null");

                // Lay chuoi ngay/thang/nam cua ngayketthuc
                Object end_obj = result_rs.getObject("ngayketthuc");
                if (end_obj != null) {
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime((java.util.Date) end_obj);
                    StringBuffer endDateSB = new StringBuffer();
                    endDateSB
                            .append(String.valueOf(endDate.get(Calendar.DATE)));
                    endDateSB.append("/");
                    endDateSB.append(String
                            .valueOf(endDate.get(Calendar.MONTH) + 1));
                    endDateSB.append("/");
                    endDateSB
                            .append(String.valueOf(endDate.get(Calendar.YEAR)));
                    list_result.add(endDateSB.toString());
                } else
                    list_result.add("null");

                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin chi tiet cua truong theo ma truong
    public String[] getUnivDetail(String univCode) throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getUnivDetail @univCode = '"
                            + univCode + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            if (result_rs_hasData) {
                list_result.add((String) result_rs.getObject("truong"));
                list_result.add((String) result_rs.getObject("tentruong"));
                list_result.add((String) result_rs.getObject("diachi"));
                list_result.add((String) result_rs.getObject("dienthoai"));
                list_result.add(String.valueOf(result_rs
                        .getObject("tongchitieu")));
                list_result.add((String) result_rs.getObject("vungtuyen"));
                list_result.add((String) result_rs.getObject("ghichu"));
            }

        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay danh sach tat ca cac truong dai hoc
    // Tra ve mot tap ket qua co so luong toi da la maxCount truong,
    // voi STT cua tap ket qua la index.
    // Do client di dong khong the xu ly ket qua voi so luong lon
    // va bang thong khong cho phep truyen du lieu nhieu
    public String[] getUnivList(int index, int maxCount) throws RemoteException {
        Vector list_result = new Vector();
        int result_rs_size = 0;
        try {
            // Context ctx = new InitialContext();
            // ConnectionPoolDataSource ds =
            // (ConnectionPoolDataSource)ctx.lookup("jdbc/EnrollDatabase");
            // PooledConnection pcon = ds.getPooledConnection("lnqk",
            // "javalnqk");
            // Connection dbConnection = pcon.getConnection();
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getUnivList");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            // Dau tien la lay so luong cua toan bo danh sach
            // Ghi vao phan tu dau tien cua Vector
            // de client biet duoc co tong cong bao nhieu ket qua
            for (result_rs_size = 0; result_rs_hasData; result_rs_size++) {
                result_rs_hasData = result_rs.next();
            }

            list_result.add(String.valueOf(result_rs_size));

            result_rs.close();
            result_rs = dbStatement.executeQuery();
            result_rs_hasData = result_rs.next();

            // Bo qua cac phan tu cua cac tap ket qua truoc index
            for (int i = 0; result_rs_hasData && i < index * maxCount; i++) {
                result_rs_hasData = result_rs.next();
            }
            // Lay tap ket qua co STT la index, ghi vao vector ket qua
            for (int i = 0; i < maxCount && result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("truong"));
                list_result.add((String) result_rs.getObject("tentruong"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin thi cu, phong thi, dia diem thi, dia chi
    // cua truong ma thi sinh dang ky thi
    public String[] getXepthisinh(String mahoso, String nguyenvong)
            throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getXepthisinh @mahoso = '"
                            + mahoso + "', @nguyenvong = '" + nguyenvong + "'");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("sbd"));
                list_result.add((String) result_rs.getObject("phongthi"));
                list_result.add((String) result_rs.getObject("tendiadiem"));
                list_result.add((String) result_rs.getObject("diachi"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Lay thong tin cac loai KVUT
    public String[] getZoneInfo() throws RemoteException {
        Vector list_result = new Vector();
        try {
            Driver dbDriver = (Driver) Class.forName(EnrollService.DRIVER)
                    .newInstance();
            Connection dbConnection = DriverManager.getConnection(
                    EnrollService.CONN_URL, EnrollService.USER,
                    EnrollService.PASSWORD);
            PreparedStatement dbStatement = dbConnection
                    .prepareStatement("EXECUTE getZoneInfo");
            ResultSet result_rs = dbStatement.executeQuery();
            boolean result_rs_hasData;
            result_rs_hasData = result_rs.next();

            for (int i = 0; result_rs_hasData; i++) {
                list_result.add((String) result_rs.getObject("kvut"));
                list_result.add((String) result_rs.getObject("khuvuc"));
                list_result.add((String) result_rs.getObject("loai"));
                list_result.add((String) result_rs.getObject("diengiai"));
                result_rs_hasData = result_rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        String[] result = new String[list_result.size()];
        result = (String[]) list_result.toArray(result);
        return (result);
    }

    // Phuong thuc thuc hien goi mail khi client yeu cau
    // Voi du kien do client cung cap
    public String sendMail(String name, String from, String to,
            String mailHost, String phone, String content)
            throws RemoteException {
        String responseStr = new String();
        try {
            // Ket noi den mailhost
            SmtpClient mailer = new SmtpClient(mailHost);
            mailer.from(from);
            mailer.to(to);
            // Lay luong Stream de goi mail
            java.io.PrintStream ps = mailer.startMessage();
            ps.println("From: " + from);
            ps.println("To: " + to);
            ps.println("Subject: %Lien he tuyen sinh from " + name + "%");
            ps.println("Dien thoai: " + phone + "\n" + content);
            // Goi mail hoan thanh, dong ket noi, tra ve chuoi bao thanh cong
            mailer.closeServer();
            responseStr = "G\u1edfi mail th\u00e0nh c\u00f4ng";
        } catch (Exception e) {
            // Neu co loi thi tra ve chuoi thong bao loi
            throw new RemoteException("G\u1edfi mail b\u1ecb l\u1ed7i. "
                    + e.getMessage());
        }
        return (responseStr);
    }
}