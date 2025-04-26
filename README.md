# webapp12

## ****Website Name****
Pixel Paradise

## **Integrantes del equipo**
| Name   | Last Name   | University Email         | GitHub          |
|----------|------------|------------------------------|----------------|
| Alba | Velasco Marques | a.velascom.2021@alumnos.urjc.es     | AlbitaVM   |
| Gonzalo | Pérez Roca | g.perezr.2019@alumnos.urjc.es     | gonzaloperz   |
| Alexandra  | Cararus Verdes | a.cararus.2021@alumnos.urjc.es    | alexandraaCS   |
| Paula | Ruiz Rubio | p.ruizr.2021@alumnos.urjc.es   | PaulaRuizRubio   |

## ****Main Aspects of the Application****
### **Entities**
- User
- Activity
- Review
- Place
#### **Relationships**
- A user performs activities and can leave a review for the activities.
- An activity has enrolled users and takes place at a location.
- A review belongs to a user and is associated with an activity.
- A location is associated with activities.
### **DIAGRAM**
![image](https://github.com/user-attachments/assets/ab529818-c485-4fc0-8154-1f52d97d67ff)

### **User Permissions**
- Not registered user: Can view available activities but cannot enroll or leave reviews.
- Registered user: Can enroll in activities and leave reviews.
- Administrator user: Can add, modify, and delete activities and users, and view the chart: A chart that shows the number of activities registered per month by users.
### **Images**
- An activity will have an associated image.
- A user will have an associated image.
### **Charts**
- A chart that shows the number of activities created per month by the administrator.
- A chart to display the activity ratings.
### **Complementary Technology**
- Generate a PDF when the user enrolls in an activity.
### **Algorithm or Advanced Query**
- Recommendation algorithm for similar or interesting activities for the user, based on their past activities and the reviews they have left.

---
## ****Templates****
### **Screenshots**
- Home screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/Inicio.png).
- Login screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/login.png).
- Register screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/registrar.png).
- Activity screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/actividad.png).
- User profile screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/Perfil.png).
- Admin profile screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/Perfil_admin.png).
- Admin statistics screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/admin_estadisticas.png).
- Admin user management screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/admin_gestion_usuarios.png).
- New activity screen
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/nueva_actividad.png).
### **Navegation Diagram**
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/diagrama_navegacion.jpg).

---
# 🛠️ PHASE 1
## ⚙️ EXECUTION INSTRUCTIONS:  Steps to Download, Build, and Run the Application

## 📌 Requirements

1. **Java Version**: Java 11 or higher.
2. **MySQL Version**: MySQL 5.7 or higher.
3. **Maven**: Apache Maven 3.6 or higher.
4. **Gmail SMTP Configuration**: A Gmail account for sending emails via the SMTP server (your app uses `smtp.gmail.com`).
5. **Spring Boot Dependencies**: Ensure that all necessary dependencies are included in the `pom.xml` file for the application.

---

## 📥 Steps to Follow

### 1️⃣  **Download the Code from the Repository**

Open your terminal (or command prompt) and run the following command to clone the repository from GitHub (replace the URL with the actual URL of your repository):

```bash
git clone https://github.com/CodeURJC-DAW-2024-25/webapp12.git
```

Navigate into the project folder:

```bash
cd webapp12
```

### 2️⃣ **Install MySQL**

If you don't have MySQL installed, download and install it from the official website: [MySQL Downloads](https://dev.mysql.com/downloads/).

Start MySQL: Once MySQL is installed, ensure that the MySQL server is running on your machine.

On Linux/Mac:

```bash
sudo service mysql start
```

On Windows: Start MySQL from the MySQL Workbench or command line.

### 3️⃣ **Set Up the Database**

Create the database for your application (`Pixel_Paradise`) in MySQL. You can do this through MySQL Workbench or via the command line.

```sql
CREATE DATABASE Pixel_Paradise;
```

In your `application.properties`, the database connection settings are already set to use the MySQL database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/Pixel_Paradise?useUnicode=true&characterEncoding=utf8&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
```

### 4️⃣ **Build the Application Using Maven**

Make sure Maven is installed. If not, download and install it from: [Maven Downloads](https://maven.apache.org/download.cgi).

Check if Maven is installed:

```bash
mvn -v
```

In your project directory, run the following Maven command to clean, build, and package the application:

```bash
mvn clean install
```

### 5️⃣ **Run the Application**

Once the application is successfully built, you can run it with the following command:

```bash
mvn spring-boot:run
```

This will start the Spring Boot application. By default, it will run on `https://localhost:8443`.

---

## ⚙️ Notes on Configuration

### ✉️  **SMTP Configuration**

The application uses Gmail's SMTP server to send emails. You need to replace the `spring.mail.username` and `spring.mail.password` with your Gmail credentials in the `application.properties`.

Example (already set in your file):

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=pixelparadisedaw@gmail.com
spring.mail.password=yourpassword
```

### 🔒 **SSL Configuration**

The app runs on HTTPS using SSL. The following settings are used to enable SSL with a `.jks` keystore. Make sure to replace the `keystore.jks` file path and passwords with your own.

```properties
server.ssl.key-store = classpath:keystore.jks
server.ssl.key-store-password = 123456
server.ssl.key-password = 123456
server.ssl.key-alias = Pixel_Paradise
server.ssl.enabled=true
```

### 🗄️ **Database Initialization**

The application is configured to use Hibernate’s `create-drop` for the database schema. This means that it will automatically create the tables when the app starts and drop them when it stops. If you prefer to keep the data, change it to `update`:

```properties
spring.jpa.hibernate.ddl-auto=update
```

### 🌐 **Access the Application**

Once the application is running, you can access it at `https://localhost:8443`. The login page can be accessed at the `/login` endpoint, and users can register through the `/register` page.

---

## 😵‍💫 Troubleshooting

- **SSL/TLS issues**: If you run into issues with SSL, make sure your keystore file is properly configured and accessible by the Spring Boot application.
- **Database Connection**: If you get an error related to the database connection, ensure that MySQL is running on the specified port (`3307`) and that the credentials match what’s in `application.properties`.
- **Dependencies Missing**: If Maven complains about missing dependencies, check your `pom.xml` file for any missing entries or run `mvn clean` to remove any old artifacts.
---

## 📊 Diagrams

---

### 🔄 **Navegation Diagram**
![image](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Images/diagrama_navegacion.jpg)

---

### 🖥️ **Class and Templates Diagram**
![diagramadaw](https://github.com/user-attachments/assets/8d26b857-5ddc-49d3-a01b-11b3ea944756)

---

### 🗄️ **Diagram with Database Entities**
![diagrama er](https://github.com/user-attachments/assets/7458b90c-80a5-4583-8777-6dc26100cd3d)

---

## 🫂 **Member Participation**

---

### **Alba Velasco Marqués**
-Tasks:
- Make the charts of the number of activities created per month by the administrator and the activity ratings.
- Do the activity finder by location.
- Delete users and activities.
- Create new activity.
- Edit user information.
- Make user and admin profile screen.
- Make all admin screens.

-Commits:

| Commit   | Link   | 
|----------|------------|
| Conseguir arreglar imagenes  | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/70579664cdb0c28fcf0e0977a0676f0517606176 | 
| grafico valoraciones de reviews conseguido | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/cb8a7f386d34cc5b8f8c80f64a7ea0f699e8b8d2 | 
| conseguido grafico actividades por mes  |https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/c9297f1537008d44ec2f18437277b1a85d8782fb |
| conseguido editar perfil | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/4494f49bcea721e6332eda2967512504754b076f |
|conseguido crear nueva actividad |https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/b51a0d67c5fe49c92b358ec99071d83b774476bb |

-Files:

| File   | Link   | 
|----------|------------|
| activityController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/activityController.java | 
| userController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/userController.java | 
| admin_users.html  | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/admin_users.html |
| create_activity.html | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/create_activity.html |
| admin_activities.html |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/admin_activities.html|

---

### **Alexandra Cararus Verdes**
- Recommended algorithm
- Class diagrams and database entity diagrams
- Index with Mustache
- "Load More" button with AJAX on the index page
- Registration form

-Commits:

| Commit   | Link   | 
|----------|------------|
|Recommended algorithm | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/82e6e87af6d2a8a5d603452cea973da60c587dac | 
|Class diagrams and database entity diagrams | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/581d572623a92ded76090299f7e539d2f4b6959c| 
| Index with Mustache |https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/b10335ba828bd6ae6d43de808a4ea00ef0cbb656 |
| Load More on index |https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/3fa334255990566420afde5f1f3a746e621133c1|
|Registration form |https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/6fa73ba0ed1d71b321077da4c3a727c2f480c6cc|

-Files:

| File   | Link   | 
|----------|------------|
| activityController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/activityController.java | 
| app.js| https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/static/js/app.js| 
| index.html | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/index.html |
| moreActivities.html | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/create_activity.html |
| register.html |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/register.html|

---

###  Gonzalo Pérez Roca 

task:
- Generate and manage the email when a new user registers.
- Generate the PDF corresponding to the activity a user has just subscribed to.
- Added the functionalities to edit and modify activities.
- Manage activity reservations.
- Generate data for all activities.

-Commits:

| Commit   | Link   | 
|----------|------------|
|Añadido un mensaje por correo al registrar un nuevo usuario | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/deddead43094495c5bab0f9ad802601da5c5eab2 |
|Generar el pdf v1 | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/c31a777a1ab8dd92fd2d2e9425cee67941e288cf |
|Nuevas actividades añadidas(sin la imagen), y cambios menores en como se muestran | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/bfba779d894ad672f3017d4be79215b978d28bf6 |
|Implementación reservas sin pdf | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/c6cde6983b96caa080f8f74048cb9f4a262f1d8f|
|Intento de reseñas | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/ac754a8894e81052c8bfadbda0a2e1f7a22f5605 |

-Files:

| File   | Link   | 
|----------|------------|
| activityController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/activityController.java | 
| reviewController.java|https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/reviewController.java |
| DatabaseInitializerService.java |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/config/DatabaseInitializerService.java|
| Edit_activity.html |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/Edit_activity.html|
| activity.html |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/templates/activity.html|

---

###  Paula Ruiz Rubio 

Task List

1. **Implement application security** (including login, logout, and user registration).  
2. **Design and configure the database**.  
3. **Connect the database to the application**.  
4. **Develop the functionalities of the login and registration pages**:  
   - Credential validation  
   - Session management  
   - Error handling  
   - User creation  
5. **Make entities pageable (Pageables)**.  
6. **Develop the database initializer service**.  


-Commits:

| Commit   | Link   | 
|----------|------------|
|Creación de la base de datos | [Creación de la base de datos](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/7abe665b256ea7a7400de13ccff25162c92c6b2c) |
|Iniciar sesión |[Iniciar sesión](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/55e74310f905e8b529c141e3e0de2eb7a61c2948) |
|Registrar un nuevo usaurio |[ Registrar un nuevo usaurio](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/e7aaaff72376492a336a9a9e90e2bb13f8671834) |
|Hacer "Pageable" las reviews | [Hacer "Pageable" las reviews](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/358152eb68f416cba31150f3447ac5ed9e0e04e6)|
|Hacer "Pageable" las actividades a las que está incrito un usaurio | [Hacer "Pageable" las actividades a las que está incrito un usaurio](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/eed7fb5df090efd0e5a3ea913c46c65a44508327) |

-Files:

| File   | Link   | 
|----------|------------|
| activityController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/activityController.java | 
| userController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/userController.java | 
| application.properties|[application.properties ](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/resources/application.properties)|
| DatabaseInitializerService.java |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/config/DatabaseInitializerService.java|
| WebSecurityConfig |[WebSecurityConfig](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Security/WebSecurityConfig.java)|
---
# 🛠️ PHASE 2  

---

## ⚙️ Notes on Configuration  

### 🖥️ **Class and Templates Diagram**  
![diagrama part2](https://github.com/user-attachments/assets/a175aada-e7bd-484a-a92e-97aff79f4f9f)

---

## 🛠️ Execution Instructions  

### 🚀 **Deployment Steps**  

1. **Network Connection**  
   - You must be connected to the university network. If accessing from outside, use the Windows or Ubuntu development desktop available in **MyApps**. 

2. **Private Key**  
   - Make sure you have the **private key** downloaded to your local computer.

3. **Conexión SSH**  
   - Open a terminal on your system and run one of the following commands to connect to the virtual machine:
     ```bash
     ssh -i ssh-keys/appWeb12.key vmuser@10.100.139.153
     ```
     o  
     ```bash
     ssh -i ssh-keys/appWeb12.key vmuser@appWeb12.dawgis.etsii.urjc.es
     ```

4. **Clone the repository into the virtual machine**  
   ```bash
   git clone https://github.com/CodeURJC-DAW-2024-25/webapp12.git

5. **Navigate to the docker folder**
   ```bash
   cd webapp12/src/main/backend/docker

6. **If you want to build the Docker images manually**
   ```bash
   chmod +x create_image.sh
   chmod +x publish_image.sh
   
7. **Execute**
   ```bash
    docker login
    ./create_image.sh      # To generate the app image
    ./publish_image.sh     # To publish it into Docker Hub

8. **🌐 Access the application at**
    - VM: https://appWeb12.dawgis.etsii.urjc.es
    - Local: https://localhost:443

9. **To stop the application**
   ```bash
   docker compose down
   
10. **If you also want to remove the database volume:**    
    ```bash
    docker compose down -v

---
### 🖊️**Documentation for API REST**
Api-docs.yaml: [File: api-docs.yaml](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/api-docs/api-docs.yaml)

Api-docs.html: [API DOCS](https://raw.githack.com/CodeURJC-DAW-2024-25/webapp12/main/api-docs/api-docs.html)

---
## 🫂 **Member Participation**

---

### **Alba Velasco Marqués**
Task List
1. Do all user dto files
2. Unify the user service for both controllers
3. Make user Postman requests

-Commits:

| Commit   | Link   | 
|----------|------------|
| Model de userDto y conseguido api de lista de users  | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/dc1c23ed361f826a41906563daa6ade2af44ee42 | 
| conseguido logica unificada pageable user | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/b295c32ec4d76fc9dc9dde2755bbcf8349fc1025#diff-e522951d633aa2432cc00a8743b0753b548ca4ab3b2bc92972ac4b85eba323e8 | 
| editar user e imagen en api | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/8fb508fc939ef3b2817bcfe559ce6d063fe93a3b#diff-6b862065f4c79b8a509ad9e86055c509aba9b96019ce8e30d5e5cf5e69274523 |
| unificado service deleteUser/id | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/fccbea7753e3c838c9f3f27871197c7fc83148d1#diff-bc8f52316f497cf987879097b203f482bc7a6466e8afce44d0b70ebcdf0d0a46  |
| arreglos de peticiones y openAi | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/ece9fa5bbd1c6c558df6bab272714d79d8edc7d6 |

-Files:

| File   | Link   | 
|----------|------------|
| UserService.java  | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Service/UserService.java | 
| UserController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Controllers/UserController.java | 
| UserRestController.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/rest/UserRestController.java |
| WebSecurityConfig.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Security/WebSecurityConfig.java |
| UserDto.java | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/dto/UserDto.java |

---

### **Alexandra Cararus Verdes**
Task List
1. Implement all review methods in the API  
2. Modify security and OpenAPI  
3. Modify the Postman collection 
4. Edit images of the API
5. Api Docs


-Commits:

| Commit   | Link   | 
|----------|------------|
|Review| https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/8bd857ab6814ab612681c8e2a0e89e1c485e9d84 | 
|Segurity| https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/99bff82a2616ced38a40004008c9f197a35d88f0| 
|Postman|https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/883ca8f3faa447901ff9034e2d8a5e4524c255a7 |
|images of the API|https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/bb94e3ffcdf9e35bc7eba135abcbb4b5fb6e3f42|
|Api Docs|https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/6fb43c706e3fa68343ae903bf24e3494dc5a515f|

-Files:

| File   | Link   | 
|----------|------------|
|ReviewRestController | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/8bd857ab6814ab612681c8e2a0e89e1c485e9d84| 
|WebSegurityConfi|https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/99bff82a2616ced38a40004008c9f197a35d88f0| 
|UserRestController| https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/bb94e3ffcdf9e35bc7eba135abcbb4b5fb6e3f42 |
|api-docs.yaml | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/6fb43c706e3fa68343ae903bf24e3494dc5a515f |
|ReviewController| https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/2e793d70d784fb45e9e402932e08d2d06a18664c|

---

###  Gonzalo Pérez Roca 

Task List
1. Generate Dockerfile and Docker-compose.yl 
2. Help with postman requests.
3. set the VM.


-Commits:

| Commit   | Link   | 
|----------|------------|
| Update Pixel Paradise.postman_collection.json | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/f9da433833ffab45f16bb71bc2902fa5bf0e8987 |
| V1 Docker| https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/b8aef881411485b7377df24ab488952b9e9ead94 |
| Generacion .sh pruebas | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/ef9ea4702501dc59e61375248975833e1baeec25 |
|update del copy | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/9cf0c1bfb8fcc0d297d40f5fd87f27a7e5b49396 |
|.sh V2 modificacion de las carpetas | https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/4862bb2a5c742354862f05dfd0b8caed806019b3 |


-Files:

| File   | Link   | 
|----------|------------|
| src/main/backend/docker/Dockerfile | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/docker/Dockerfile | 
| src/main/backend/docker/docker-compose.yml |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/docker/docker-compose.yml|
| src/main/backend/docker/create_image.sh | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/docker/create_image.sh|
| src/main/backend/docker/publish_image.sh | https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/docker/publish_image.sh|
| Pixel Paradise.postman_collection.json |https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/Pixel%20Paradise.postman_collection.json|

---

###  Paula Ruiz Rubio 

I have been responsible for implementing the API security layer, including authentication and authorization mechanisms. I developed comprehensive OpenAPI documentation to ensure clear API specifications. Additionally, I designed and implemented all API endpoints related to Activity and Statistics functionality, including CRUD operations and business logic. This involved creating the data models and implementing the DTO mapping layer for both Activity and Statistics entities to ensure proper data transfer and separation of concerns

-Commits:

| Commit   | Link   | 
|----------|------------|
|crear get recommendActivities y unificar servicio activity | [crear get recommendActivities y unificar servicio activity](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/158dfd9f30b081fba1c6174c63c146405ff1a782)|
|Modificar seguridad para que funcione en web y api | [Modificar seguridad para que funcione en web y api](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/9e522dffc876c25df7a1b17a56f40c79324f7bc3)|
|Upadete api-docs |[Upadete api-docs](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/2ff49cf3c295a777b603c8d9cc995bf64a395f22)|
|security api works | [security api works](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/889f59ebde7364bcffcaf29a30e633b10e5783e6)|
| Devolver error 401 seguridad Api| [Devolver error 401 seguridad Api](https://github.com/CodeURJC-DAW-2024-25/webapp12/commit/bcbf661a4862cbc1e90d1b978a2fea1d8eb133b6) |

-Files:

| File   | Link   | 
|----------|------------|
| Folder: api-docs | [Folder: api-docs](https://github.com/CodeURJC-DAW-2024-25/webapp12/tree/main/api-docs) | 
|ActivityRestController | [ActivityRestController](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/rest/ActivityRestController.java)| 
|StatisticsRestController |[StatisticsRestController](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/rest/StatisticsRestController.java)|
| Security: jwt |[Security: jwt](https://github.com/CodeURJC-DAW-2024-25/webapp12/tree/main/src/main/backend/src/main/java/es/codeurjc/backend/security/jwt)|
| ActivityService|[ActivityService](https://github.com/CodeURJC-DAW-2024-25/webapp12/blob/main/src/main/backend/src/main/java/es/codeurjc/backend/Service/ActivityService.java)|

---
---
# 🛠️ PHASE 3

## ⚙️ Environment setup

To compile and run the SPA, follow these steps to set up your development environment:

### 1. Install Node.js and npm:
   - Download and install Node.js from [its official website](https://nodejs.org/).
   - npm (Node Package Manager) will be installed alongside Node.js.

### 2. Install Angular CLI:
   - Open a terminal or command prompt.
   - Execute the following command to install Angular CLI globally:
     ```bash
     npm install -g @angular/cli
     ```

### 3. Verify the installation:
   - To ensure that Angular CLI was installed correctly, run:
     ```bash
     ng --version
     ```
   - You should see the installed version of Angular CLI.

### 4. Clone this repository:
   ```bash
   git clone https://github.com/CodeURJC-DAW-2023-24/webapp07
   ```

### 5. Docker setup:
   - Install [Docker](https://www.docker.com/get-started/)if you haven't already.
   - Run Docker.

### 6. Launch the application:
   - Open a Git Bash (Windows) or terminal (Linux/Mac) in the cloned repository folder.
   - Change directory:
     ```bash
     cd docker/
     ```
   - Execute the following command
     ```bash
     docker-compose up -d
     ```

### 7. Access the application:
   - Open your browser and navigate to:
     ```bash
     https://localhost:8443/new
     ```

---
## 📊 Diagram
(Pegar imagen diagrama)

---
## 🫂 **Member Participation**

---

### **Alba Velasco Marqués**

Task List
1. 
2. 
3. 

-Commits:

| Commit   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

-Files:

| File   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

---

### **Alexandra Cararus Verdes**

Task List
1. 
2. 
3. 

-Commits:

| Commit   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

-Files:

| File   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

---

###  Gonzalo Pérez Roca 

Task List
1. 
2. 
3. 

-Commits:

| Commit   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

-Files:

| File   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |
---

###  Paula Ruiz Rubio 

Task List
1. 
2. 
3. 

-Commits:

| Commit   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |

-Files:

| File   | Link   | 
|----------|------------|
|   |  | 
|  |  | 
|  |  |
|  |   |
| | |


---
## 🎥 Video
[Video - Pixel Paradise | Youtube]( COPIAR AQUI EL https://ENLACE DEL VIDEO DE YOUTUBE)

---
