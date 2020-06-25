import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { HomeComponent } from './home/home.component';
import { HomeBodyComponent } from './home-body/home-body.component';
import { FooterComponent } from './footer/footer.component';
import { AboutTheTeamComponent } from './about-the-team/about-the-team.component';
import {HardcodedComponent} from './hardcoded/hardcoded.component';
import {LoginComponent} from './login/login.component';


const routes: Routes = [
  {
    path: '', redirectTo: 'home', pathMatch: 'full'
  },
  {
    path: 'home', component: HomeComponent
  },
  {
    path: 'about-the-team', component: AboutTheTeamComponent
  },
  {
    path: 'hardcoded', component: HardcodedComponent
  },
  {
    path: 'login', component: LoginComponent
  },
  {
    path: '**', component: HomeComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
