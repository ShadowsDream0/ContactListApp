# ContactListApp
#### Description
ContactListApp is a command line application that allows user to manage his/her contacts:

#### Features
- add a new contact with information about: first name, last name, gender, date of birth, city, email and phone numbers
- view general and detailed information about contacts
- update contact's information
- import contacts from one or several files located in folder pointed by user
- export contacts from contact list to a file
- receive exported contacts by email as a text or as an attached file
- activity logging

#### Usage
Preconditions for running the application:

- latest JRE
- installed postgresql server with user "postgres" and created database. Name of the database is passed as argument to the application
- email sending feature is supported only if the app can find the file (path to the file is hard coded in EmailSenderServiceImpl) with credentials for smtp-server. Login and password must be on first and second line accordingly.

#### Planned Features
- removing hard coded smtp-server credentials
- transforming into a web app
- multithreaded execution of folder scanning

#### Run example
```
java -jar cl-app.jar <database_name>
```
##### Development tools info
- Apache Maven was used to build the project
- Project supports JaCoCo reporting and build failing on insufficient coverage
- Tests: Junit 5 and Mockito
- Logging: slf4j
- RDBMS: PostgreSQL
- IDE: Intellij Idea