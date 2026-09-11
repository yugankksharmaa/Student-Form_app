<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Form Application</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="form-container">
        <h1>Student Registration Form</h1>
        <form action="submit" method="post">

            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" required>
            </div>

            <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" required>
            </div>

            <div class="form-group">
                <label for="dob">Date of Birth</label>
                <input type="date" id="dob" name="dob" required>
            </div>

            <div class="form-group">
                <label for="gender">Gender</label>
                <select id="gender" name="gender" required>
                    <option value="">-- Select --</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                </select>
            </div>

            <div class="form-group">
                <label for="qualification">Highest Qualification</label>
                <input type="text" id="qualification" name="qualification" required>
            </div>

            <div class="form-group">
                <label for="yearOfPassing">Year of Passing</label>
                <input type="number" id="yearOfPassing" name="yearOfPassing" min="1950" max="2099" required>
            </div>

            <div class="form-group">
                <label for="mobileNumber">Mobile Number</label>
                <input type="tel" id="mobileNumber" name="mobileNumber" pattern="[0-9]{10}" placeholder="10 digit number" required>
            </div>

            <button type="submit">Submit</button>
        </form>
    </div>
</body>
</html>
