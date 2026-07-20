import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private readonly router: Router) {}

  get showNavbar(): boolean {
    const url = this.router.url.split('?')[0].split('#')[0];
    if (url === '/') return false;
    if (url === '') return false;
    if (url === '/login') return false;
    if (url === '/register') return false;
    return true;
  }
}
