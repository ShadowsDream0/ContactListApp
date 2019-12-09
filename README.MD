# ContactListApp
#### Description
ContactListApp is a command line application that allows user to manage his/her contacts:
#### Features
- add new contact with information about first name, last name, gender, date of birth, city, email and phone numbers
- view general and detailed information about contacts-update contact's information
- import contacts from one file or from several files in folder pointed by user
- export contacts from contact list to file-receive exported contacts by email as a text or an attached file
#### Usage
Preconditions for running the application:
- JRE 11
- installed postgresql server with created database. Name of the database is passed as argument to the application
- email sending feature is supported only if the app can find the file (path to the file is hard coded) with credentials for smtp-server. Login and password must be on first and second line accordingly.
#### Planned Features
- transforming to a web app
- multithreaded execution of folder scanning
#### Run example
```
java -jar cl-app-with-dependencies.jar <database_name>
```
