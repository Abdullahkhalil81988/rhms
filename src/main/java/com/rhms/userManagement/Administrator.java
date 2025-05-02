package com.rhms.userManagement;

import java.io.Serializable;
import java.util.ArrayList;

public class Administrator extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ArrayList<Doctor> managedDoctors;
    
    public Administrator(String name, String email, String password, String phone, String address, int userID) {
        super(name, email, password, phone, address, userID);
        this.managedDoctors = new ArrayList<>();
    }
    
    public void addDoctor(Doctor doctor) {
        if (!managedDoctors.contains(doctor)) {
            managedDoctors.add(doctor);
        }
    }
    
    public void removeDoctor(Doctor doctor) {
        managedDoctors.remove(doctor);
    }
    
    public ArrayList<Doctor> getManagedDoctors() {
        return managedDoctors;
    }
}
