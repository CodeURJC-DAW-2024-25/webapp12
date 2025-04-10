import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { IndexComponent } from './index/index.component';
import { LoginComponent } from './login/login.component';
import { AdminStatisticsComponent } from './admin-statistics/admin-statistics.component';
import { ProfileComponent } from './profile/profile.component';
import { AdminActivitiesComponent } from './admin-activities/admin-activities.component';
import { ActivityComponent } from './activity/activity.component';


const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'login', component: LoginComponent },
  { path: 'statistics', component: AdminStatisticsComponent},
  { path: 'profile', component: ProfileComponent},
  { path: 'adminActivities', component: AdminActivitiesComponent},
  {path : 'activity/:id', component: ActivityComponent},



  { path: '**', redirectTo: '' }




];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
