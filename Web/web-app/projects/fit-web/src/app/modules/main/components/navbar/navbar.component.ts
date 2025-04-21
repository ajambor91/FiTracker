import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../../../users/models/user.model';
import {AuthService} from '../../../../core/services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  public user$!: Observable<User | null>;

  constructor(
    private authService: AuthService,
    private router: Router) {
  }

  public logout(): void {
    this.authService.logoutUser();
    this.router.navigate(['']);
  }

  public ngOnInit(): void {
    this.user$ = this.authService.getUser();
  }
}
