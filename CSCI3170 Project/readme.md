# Steps to Run This Project

This project is a Maven-based Java application. Follow these steps to get it running on your local machine:

1. **Download and Unzip:**
   Download the project folder(.zip archive) and extract its contents to a location of your choice.

2. **Open with an IDE:**
   Open the extracted project folder in your preferred Java IDE.  This project is built using Maven, so IDEs like IntelliJ IDEA and Eclipse are well-suited for this.  The IDE should automatically recognize the project's Maven structure.

3. **Locate the Main Class:**
   The main application entry point is the `Main.java` file. You can find it within the project's source code directory, located at `src/main/java`. 

4. **Run the Application:**
   Once you've located `Main.java`, you can run the application directly from your IDE.  Most IDEs provide a "Run" button or menu option within the code editor. Right-click on the `Main.java` file and select "Run".


# Other Important Notes
1. This project is built based on the integration of PostgreSQL and Oracle only. Other databases will **NOT** work.
2. To test this application on your PostgreSQL or Oracle database, head over to `src/main/java/Application.java` and modify three 
attributes: **DBMSLink**, **username** and **password** to configure your database.
3. The **sample_data** folder should be put in `src/main/java` for proper reading of the source files.
