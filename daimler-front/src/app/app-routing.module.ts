import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountRecoveryComponent } from './account-recovery/account-recovery.component';
import { AccountVerificationComponent } from './account-verification/account-verification.component';
import { AdminAccountComponent } from './admin/admin-account/admin-account.component';
import { AdminBlockingComponent } from './admin/admin-blocking/admin-blocking.component';
import { AdminHomeComponent } from './admin/admin-home/admin-home.component';
import { AdminInboxComponent } from './admin/admin-inbox/admin-inbox.component';
import { AdminRideHistoryComponent } from './admin/admin-ride-history/admin-ride-history.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DriverAccountComponent } from './driver/driver-account/driver-account.component';
import { DriverHomeComponent } from './driver/driver-home/driver-home.component';
import { DriverInboxComponent } from './driver/driver-inbox/driver-inbox.component';
import { DriverRideHistoryComponent } from './driver/driver-ride-history/driver-ride-history.component';
import { HomeComponent } from './home/home.component';
import { InvitationResponseComponent } from './invite/invitation-response/invitation-response.component';
import { LoginComponent } from './login/login.component';
import { PassengerAccountComponent } from './passenger/passenger-account/passenger-account.component';
import { PassengerHomeComponent } from './passenger/passenger-home/passenger-home.component';
import { PassengerInboxComponent } from './passenger/passenger-inbox/passenger-inbox.component';
import { PassengerRideHistoryComponent } from './passenger/passenger-ride-history/passenger-ride-history.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'passenger/account', component: PassengerAccountComponent},
  {path: 'passenger/history', component: PassengerRideHistoryComponent},
  {path: 'passenger/home', component: PassengerHomeComponent},
  {path: 'passenger/inbox', component: PassengerInboxComponent},
  {path: 'driver/home', component: DriverHomeComponent},
  {path: 'driver/account', component: DriverAccountComponent},
  {path: 'driver/history', component: DriverRideHistoryComponent},
  {path: 'driver/inbox', component: DriverInboxComponent},
  {path: 'exit', redirectTo: ''},
  {path: 'verification', component: AccountVerificationComponent},
  {path: 'admin/home', component: AdminHomeComponent},
  {path: 'admin/account', component: AdminAccountComponent},
  {path: 'admin/history', component: AdminRideHistoryComponent},
  {path: 'admin/block', component: AdminBlockingComponent},
  {path: 'admin/inbox', component: AdminInboxComponent},
  {path: 'recovery', component: AccountRecoveryComponent},
  {path: 'changePassword', component: ChangePasswordComponent},
  {path: 'invitation', component: InvitationResponseComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
