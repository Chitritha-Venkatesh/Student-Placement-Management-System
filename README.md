# Student Placement Management System

A Java-based student placement management system with a desktop dashboard for managing student profiles, checking placement eligibility, calculating placement scores, and recommending companies.

## Features

- Add and manage student profiles
- View all student records
- Search students by USN
- Check placement eligibility
- Calculate placement readiness score
- Recommend companies based on placement score
- Save and load student data using CSV
- Interactive Java Swing dashboard
- Placement analysis view
- Student profile details
- Console-based Java application

## Placement Logic

The system calculates a placement score using CGPA, certifications, and internships.

### Eligibility

Students with a **CGPA of 7.5 or higher** are considered placement eligible.

### Company Recommendations

- **Score above 80:** Google, Microsoft, Amazon
- **Score above 60:** Infosys, Accenture, TCS Digital
- **Score 60 or below:** Skill Improvement Required

## Technologies

- Java
- Java Swing
- Object-Oriented Programming
- CSV File Handling
- Exception Handling
- Collections (`ArrayList`)
- Interfaces and Inheritance

## Project Structure

```text
Student-Placement-Management-System/
│
├── DashboardApp.java
├── Main.java
├── Student.java
├── Person.java
├── PlacementManager.java
├── PlacementOperations.java
├── InvalidStudentDataException.java
├── StudentNotFoundException.java
│
└── data/
    └── students.csv
