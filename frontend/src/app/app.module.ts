import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { ProfileComponent } from './profile/profile.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AdminStatisticsComponent  } from './admin-statistics/admin-statistics.component';
import { AdminActivitiesComponent } from './admin-activities/admin-activities.component';


@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    HeaderComponent,
    FooterComponent,
    ProfileComponent,
    LoginComponent,
    RegisterComponent,
    AdminStatisticsComponent,
    AdminActivitiesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
