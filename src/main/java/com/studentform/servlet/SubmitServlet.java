package com.studentform.servlet;

import com.studentform.dao.StudentDAO;
import com.studentform.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/submit")
public class SubmitServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String firstName = trim(request.getParameter("firstName"));
        String lastName = trim(request.getParameter("lastName"));
        String dobStr = trim(request.getParameter("dob"));
        String gender = trim(request.getParameter("gender"));
        String qualification = trim(request.getParameter("qualification"));
        String yearStr = trim(request.getParameter("yearOfPassing"));
        String mobile = trim(request.getParameter("mobileNumber"));

        // ---- Server-side validation ----
        StringBuilder errors = new StringBuilder();

        if (isEmpty(firstName)) errors.append("First name is required. ");
        if (isEmpty(lastName)) errors.append("Last name is required. ");
        if (isEmpty(dobStr)) errors.append("Date of birth is required. ");
        if (isEmpty(gender)) errors.append("Gender is required. ");
        if (isEmpty(qualification)) errors.append("Highest qualification is required. ");
        if (isEmpty(yearStr) || !yearStr.matches("\\d{4}")) errors.append("Valid year of passing is required. ");
        if (isEmpty(mobile) || !mobile.matches("\\d{10}")) errors.append("Valid 10-digit mobile number is required. ");

        if (errors.length() > 0) {
            request.setAttribute("message", errors.toString());
            request.getRequestDispatcher("failure.jsp").forward(request, response);
            return;
        }

        try {
            Student student = new Student();
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setDob(Date.valueOf(dobStr)); // expects yyyy-MM-dd from <input type="date">
            student.setGender(gender);
            student.setHighestQualification(qualification);
            student.setYearOfPassing(Integer.parseInt(yearStr));
            student.setMobileNumber(mobile);

            StudentDAO dao = new StudentDAO();
            boolean saved = dao.saveStudent(student);

            if (saved) {
                request.getRequestDispatcher("success.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "Could not save data. Please try again.");
                request.getRequestDispatcher("failure.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Server error: " + e.getMessage());
            request.getRequestDispatcher("failure.jsp").forward(request, response);
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
