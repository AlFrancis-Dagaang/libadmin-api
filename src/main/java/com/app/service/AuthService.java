package com.app.service;

import com.app.dao.AuthDAO;
import com.app.exception.ResourceNotFound;
import com.app.model.Admin;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    // Validates the admin login credentials.
    public void loginAsAdmin(Admin admin) {
        Admin tempAdmin = this.authDAO.getAdminByUsername(admin.getUsername());

        if (tempAdmin == null) {
            throw new ResourceNotFound ("No account found");
        }else if (!admin.getPassword().equals(tempAdmin.getPassword())){
            throw new IllegalArgumentException("Username or password is incorrect");
        }
    }

}
