# Library
FBLA Project

TO RUN THE APPLICATION AS AN EXECUTABLE ON WINDOWS: 
========================================================================================================
1. Open URL www.github.com/nimitadeshpande
2. Click on the "Library" Repository
3. Click on the "Library-1.0.exe" link - this opens a new page
4. Click on the Download button
5. Double Click on the downloaded file on local machine. 
6. Click on the Install button  [Click Run Anyway if Windows Defender blocks the install]
7. This opens the application. 
8. Enter admin for username and admin for password and hit Submit to open the Main page of the application.

NOTE: when run in this manner, any data changes such as new books/students, checkout/returns are NOT saved to the database files
If you want to save data updates, please run the application using the Libary.jar files as outlined below.

TO RUN THE APPLICATION USING LIBRARY.JAR ON WINDOWS: 
========================================================================================================
1. Ensure Java is installed on local machine and JAVA_HOME is set to point to the Java install directory 
2. Open URL www.github.com/nimitadeshpande
3. Click on the "Library" Repository
4. Click on the green "Clone or Download" button
5. On the prompt window, Click on "Download Zip" button. This downloads the application as Library-master.zip.
6. On the local machine, extract the Library-master.zip files to a local folder, say C:\test; files are extracted to C:\test\Library-master folder
7. Open a Command Prompt window and type cd C:\test\Library-master to change directory 
8. Type in runlibrary.bat at the prompt OR the command : java -jar Library.jar Book.txt Student.txt Teacher.txt Admin.txt
9. This opens the application. 
10. Enter admin for username and admin for password and hit Submit to open the Main page of the application.
