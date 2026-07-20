import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from './pages/feed/feed.component';
import { HomeComponent } from './pages/home/home.component';
import { ArticleComponent } from './pages/article/article.component';
import { ArticleNewComponent } from './pages/article-new/article-new.component';
import { LoginComponent } from './pages/login/login.component';
import { MeComponent } from './pages/me/me.component';
import { RegisterComponent } from './pages/register/register.component';
import { ThemesComponent } from './pages/themes/themes.component';

// consider a guard combined with canLoad / canActivate route option
// to manage unauthenticated user to access private routes
const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'feed', component: FeedComponent },
  { path: 'article/nouveau', component: ArticleNewComponent },
  { path: 'article/:id_article', component: ArticleComponent },
  { path: 'login', component: LoginComponent },
  { path: 'me', component: MeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'themes', component: ThemesComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
