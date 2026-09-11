package com.studentform.dao;

import com.studentform.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentDAO {

    private static final String INSERT_SQL =
            "INSERT INTO formDetails " +
            "(firstName, lastName, dob, gender, highestqualification, year_of_passing, mobilenumber) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public boolean saveStudent(Student student) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setDate(3, student.getDob());
            ps.setString(4, student.getGender());
            ps.setString(5, student.getHighestQualification());
            ps.setInt(6, student.getYearOfPassing());
            ps.setString(7, student.getMobileNumber());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
