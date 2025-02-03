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
- Un usuario realiza actividades y pode poner una review a las actividades.
- Una actividad tiene a usuarios inscritos y se desarrolla en un lugar.
- Una reseña pertenece a un usuario y está asociada a una actividad.
- Un lugar está asociado a una actividades.
#### **Diagram**

### **User Permissions**
- Usarios invitado: Podrá ver las actividades disponibles pero no incribirse ni poner reseñas
- Usuario registrado: Podrá apuntarse a las actividades y poner reseñas
- Usuario administrador:Poderá añadir, modificar y elimianr actividades y usuario. Y ver el gráfico: Gráfico que muestra el número de actividades registradas por mes por los usuarios
### **Images**
- Actividad tendrá una imagen asociada
- Lugar tendrá una imagen asociada
### **Charts**
- Gráfico que muestra el número de actividades creadas por mes por el administrador
- Grafico para mostrar las valoraciones de la actividad.

### **Complementary Technology**
Gerenar pdf cuando el usuario se inscriba a una actividad

### **Algorithm or Advanced Query**
Algoritmo de recomendación de actividades similares o de interés para el usuario, basado en sus actividades anteriores y las reseñas que ha dejado.
