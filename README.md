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
### **Class and temples diagram**
![diagramadaw](https://github.com/user-attachments/assets/8d26b857-5ddc-49d3-a01b-11b3ea944756)
## **Member Participation**
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


