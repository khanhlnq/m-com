// Eroll.java
// Copyright 2003 Le Ngoc Quoc Khanh.
// Giao dien cho dich vu Web ho tro tuyen sinh
// Dinh nghia cac phuong thuc ma dich vu se ho tro
package enroll;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Bat buoc phai extends Remote
public interface Enroll extends Remote {
    public String[] findRecord(String ho, String ten, int gender,
            String birthday, String id, String district, String province,
            String univ, String branch, int index, int maxCount)
            throws RemoteException;

    public String[] findUniv(String univCode, String univName, String block,
            String branch, int index, int maxCount) throws RemoteException;

    public String[] findZone(String communeName, String districtName,
            String provinceName) throws RemoteException;

    public String[] getBlockList() throws RemoteException;

    public String[] getBranchDetail(String univCode, String blockCode,
            String branchCode) throws RemoteException;

    public String[] getBranchList(String univCode, int index, int maxCount)
            throws RemoteException;

    public String[] getMark(String mahoso, String sbd, String ho, String ten,
            int gender, String birthday, String id, String district,
            String province, String univ, String branch, int index, int maxCount)
            throws RemoteException;

    public String[] getNguyenvong(String record) throws RemoteException;

    // Cac phuong thuc cua dich vu, chi tiet se mo ta trong phan implement
    public String[] getObjectInfo() throws RemoteException;

    public String[] getStageInfo() throws RemoteException;

    public String[] getUnivDetail(String univCode) throws RemoteException;

    public String[] getUnivList(int index, int maxCount) throws RemoteException;

    public String[] getXepthisinh(String mahoso, String nguyenvong)
            throws RemoteException;

    public String[] getZoneInfo() throws RemoteException;

    public String sendMail(String name, String from, String to,
            String mailHost, String phone, String content)
            throws RemoteException;
}