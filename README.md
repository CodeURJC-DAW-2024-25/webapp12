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


