# Section 1
The medical management app is to be made to aid doctor and staff to manage paitient appoinments, there timings and date, treatment they are provided with and much more for both the paitient and the doctors ussage

# architecture layer
The architecture is divided into 3 layers 
i) presentation layer - thymeleaf templated later where user interacts and restapi handle control
ii) application layer - spring layer that handle the backend and server
iii) data layer- mysql and mongodb layer handling the databser as entites 

# section 2
step 1) user interacts with the application
step 2 ) user enters data that application made up using the thymleaf templaters 
step 3) control is handed over to rest-api end points
step 4) the rest api sents data the backend
step 5) this data is then carried and used for proccesses by the spring architecture
step 6) data is interpreted 
step 7) data is transfered to the mysql and mongodb no-sqls for dynamic storage
