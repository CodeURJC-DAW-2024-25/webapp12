import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { IndexComponent } from './index/index.component';
import { LoginComponent } from './login/login.component';
import { AdminStatisticsComponent } from './admin-statistics/admin-statistics.component';
import { ProfileComponent } from './profile/profile.component';
import { AdminActivitiesComponent } from './admin-activities/admin-activities.component';
import { ActivityComponent } from './activity/activity.component';
import { EditUserProfileComponent } from './edit-user-profile/edit-user-profile.component';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { RegisterComponent } from './register/register.component';
import { ErrorComponent } from './error/error.component';
import { CreateActivityComponent } from './create-activity/create-activity.component';
import { EditActivityComponent } from './edit-activity/edit-activity.component';


const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'login', component: LoginComponent },
  { path: 'statistics', component: AdminStatisticsComponent},
  { path: 'profile', component: ProfileComponent},
  { path: 'adminActivities', component: AdminActivitiesComponent},
  { path : 'activity/:id', component: ActivityComponent},
  { path : 'editUserProfile/:id', component: EditUserProfileComponent},
  { path : 'adminUsers', component: AdminUsersComponent},
  { path: 'register', component: RegisterComponent},
  { path: 'error', component: ErrorComponent},
  { path: 'newActivity', component: CreateActivityComponent },
  { path: 'activity/edit/:id', component: EditActivityComponent },


  { path: '**', component: ErrorComponent } //redirige si no existe la ruta a la pantalla de error.




];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
