import {Routes} from '@angular/router';
import {UserLayoutComponent} from './module/user/user-layout/user-layout.component';
import {AdminLayoutComponent} from './module/admin/admin-layout/admin-layout.component';
import {AuthGuard} from './share/guards/auth.guard';
import {profileResolver} from './share/service/user.service';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('./module/user/home/home.component')
          .then((mod) => mod.HomeComponent),
      },
      {
        path: 'login-register',
        loadComponent: () => import('./module/user/login-signup/login-signup.component')
          .then((mod) => mod.LoginSignupComponent),
      },
      {
        path: 'fixture',
        loadComponent: () => import('./module/user/fixture/fixture.component')
          .then((mod) => mod.FixtureComponent),
      },
      {
        path: 'leagues',
        loadComponent: () => import('./module/user/fixture/leagues/leagues.component')
          .then((mod) => mod.LeaguesComponent),
      },
      {
        path: 'team',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/user/team/team-list/team-list.component')
              .then((mod) => mod.TeamListComponent),
          },
          {
            path: 'detail',
            loadComponent: () => import('./module/user/team/team-detail/team-detail.component')
              .then((mod) => mod.TeamDetailComponent),
          }
        ]
      },
      {
        path: 'fixture-detail',
        loadComponent: () => import('./module/user/fixture-detail/fixture-detail.component')
          .then((mod) => mod.FixtureDetailComponent),
      },
      {
        path: 'thank-you',
        loadComponent: () => import('./module/user/thank-you/thank-you.component')
          .then((mod) => mod.ThankYouComponent),
      },
      {
        path: 'vn-pay',
        loadComponent: () => import('./module/user/thank-you/thank-you.component')
          .then((mod) => mod.ThankYouComponent),
      },
      {
        path: 'history',
        loadComponent: () => import('./module/user/history/history.component')
          .then((mod) => mod.HistoryComponent),
      }
    ],
  },
  {
    path: 'admin',
    canActivate: [AuthGuard],
    component: AdminLayoutComponent,
    resolve: {
      profile: profileResolver
    },
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('./module/admin/home-admin/home-admin.component')
          .then((mod) => mod.HomeAdminComponent),
      },
      {
        path: 'user',
        loadComponent: () => import('./module/admin/user-management/user-list/user-list.component')
          .then((mod) => mod.UserListComponent),
      },
      {
        path: 'league',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/admin/league-management/league-list/league-list.component')
              .then((mod) => mod.LeagueListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('./module/admin/league-management/league-upsert/league-upsert.component')
              .then((mod) => mod.LeagueUpsertComponent),
          }
        ]
      },
      {
        path: 'match',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/admin/match-management/match-list/match-list.component')
              .then((mod) => mod.MatchListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('./module/admin/match-management/match-upsert/match-upsert.component')
              .then((mod) => mod.MatchUpsertComponent),
          }
        ]
      },
      {
        path: 'team',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/admin/team/team-list/team-list.component')
              .then((mod) => mod.TeamListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('./module/admin/team/team-upsert/team-upsert.component')
              .then((mod) => mod.TeamUpsertComponent),
          }
        ]
      },
      {
        path: 'order',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/admin/order-management/order-list/order-list.component')
              .then((mod) => mod.OrderListComponent),
          }
        ]
      },
      {
        path: 'stadium',
        children: [
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          },
          {
            path: 'list',
            loadComponent: () => import('./module/admin/stadium/stadium.component')
              .then((mod) => mod.StadiumComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('./module/admin/stadium/stadium-upsert/stadium-upsert.component')
              .then((mod) => mod.StadiumUpsertComponent),
          }
        ]
      }
    ]
  },
  {
    path: '**',
    redirectTo: '404',
    pathMatch: 'full'
  }
];
