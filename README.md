Simple Exam Timer Simulator (HttpSession)

Student Details

| Field         | Details                          |
|---------------|----------------------------------|
| Name          | Suma Biradar                     |
| USN           | 2BL23CS159                       |
| Branch        | Computer Science & Engineering   |
| Semester      | VI Semester                      |
| Subject       | Advanced Java Programming        |
| Problem No.   | 

## Problem Statement

This is a **Simple Exam Timer Simulator** built using Java Servlets and `HttpSession`.
When the user clicks **Start Exam**, the servlet stores the current time
(`System.currentTimeMillis()`) in the session. On every refresh, the servlet
retrieves the start time from the session and displays the elapsed time in
**MM:SS** format. If the elapsed time crosses **60 minutes**, an
*"Exam time over!"* message is shown. The **End Exam** button calls
`session.invalidate()` and shows a final summary of the total time taken.

## Technologies Used

- Java (Servlets â `jakarta.servlet`)
- HTML, CSS (inline)
- Apache Tomcat 10
- Eclipse IDE for Enterprise Java Developers

## How to Run This Project

1. Clone this repository or download the ZIP.
2. Open Eclipse â **File â Import â Existing Projects into Workspace** and import this folder as a **Dynamic Web Project**.
3. Add **Apache Tomcat 10** as the server in Eclipse (Window â Preferences â Server â Runtime Environments).
4. Right-click the project â **Run As â Run on Server**.
5. Open your browser and go to: `http://localhost:8080/ExamTimerServlet/index.html`
6. Click **Start Exam**, refresh the page a few times to see the timer update, then click **End Exam** to view the summary.

## Screenshots

### Input Form
![Input Form](screenshots/screenshot1.png)

### Output / Result Page
![Output Page](screenshots/screenshot2.png)

### Exam Ended Summary
![Exam Ended](screenshots/screenshot3.png)

## Servlet Concept Practiced

**HttpSession** â the servlet uses `request.getSession()` to create and
retrieve a session, stores the exam start time as a session attribute
(`examStart`) using `session.setAttribute(...)`, reads it back on every
refresh with `session.getAttribute(...)`, and clears it with
`session.invalidate()` when the user clicks **End Exam**.

The project also demonstrates:

- `doGet` and `doPost` handling in the same servlet
- Reading a request parameter (`action`) to dispatch behaviour
- Server-side input validation with a friendly error page
- HTML response generation using `PrintWriter`
