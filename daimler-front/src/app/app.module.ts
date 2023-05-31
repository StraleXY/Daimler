import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MaterialModule } from 'src/core/material.module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RegisterComponent } from './register/register.component';
import { NavMainComponent } from './nav-main/nav-main.component';
import { HomeComponent } from './home/home.component';
import { MapComponent } from './common/map/map.component';
import { GetARideComponent } from './common/get-a-ride/get-a-ride.component';
import { RouteCompactComponent } from './route-compact/route-compact.component';
import { PassengerAccountComponent } from './passenger/passenger-account/passenger-account.component';
import { BasicInfoCardComponent } from './common/basic-info-card/basic-info-card.component';
import { EditBasicInfoCardComponent } from './common/edit-basic-info-card/edit-basic-info-card.component';
import { PickProfilePicComponent } from './passenger/pick-profile-pic/pick-profile-pic.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AccountVerificationComponent } from './account-verification/account-verification.component';
import { AdminAccountComponent } from './admin/admin-account/admin-account.component';
import { AdminBasicInfoCardComponent } from './admin/admin-basic-info-card/admin-basic-info-card.component';
import { AdminEditBasicInfoCardComponent } from './admin/admin-edit-basic-info-card/admin-edit-basic-info-card.component';
import { AdminDriverRegisterComponent } from './admin/admin-driver-register/admin-driver-register.component';
import { SidebarComponent } from './common/sidebar/sidebar.component';
import { LoginComponent } from './login/login.component';
import { AccountRecoveryComponent } from './account-recovery/account-recovery.component';
import { PassengerRideHistoryComponent } from './passenger/passenger-ride-history/passenger-ride-history.component';
import { RouteHistoryCardComponent } from './common/route-history-card/route-history-card.component';
import { DriverAccountComponent } from './driver/driver-account/driver-account.component';
import { DriverUpdateRequestsComponent } from './admin/driver-update-requests/driver-update-requests.component';
import { UrlSentizerPipe } from './pipes/url-sentizer.pipe';
import { DriverRideHistoryComponent } from './driver/driver-ride-history/driver-ride-history.component';
import { UsersPreviewCardComponent } from './common/users-preview-card/users-preview-card.component';
import { DetailedRidePreviewComponent } from './common/detailed-ride-preview/detailed-ride-preview.component';
import { AdminRideHistoryComponent } from './admin/admin-ride-history/admin-ride-history.component';
import { RateRideDialogComponent } from './passenger/rate-ride-dialog/rate-ride-dialog.component';
import { FavoriteRoutesCardComponent } from './passenger/favorite-routes-card/favorite-routes-card.component';
import { PassengerHomeComponent } from './passenger/passenger-home/passenger-home.component';
import { TokenInterceptorService } from './services/token-interceptor.service';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DriverHomeComponent } from './driver/driver-home/driver-home.component';
import { PassengerCountComponent } from './passenger/passenger-count/passenger-count.component';
import { MoreRideSettingsComponent } from './passenger/more-ride-settings/more-ride-settings.component';
import { LinkedRidePersonsComponent } from './passenger/linked-ride-persons/linked-ride-persons.component';
import { DriverComingComponent } from './driver/driver-coming/driver-coming.component';
import { DriverNewRideComponent } from './driver/driver-new-ride/driver-new-ride.component';
import { InvitationResponseComponent } from './invite/invitation-response/invitation-response.component';
import { AdminBlockingComponent } from './admin/admin-blocking/admin-blocking.component';
import { BlockingUsersListComponent } from './admin/blocking-users-list/blocking-users-list.component';
import { UserNotesListComponent } from './admin/user-notes-list/user-notes-list.component';
import { DatePipe } from '@angular/common';
import { StatsPreviewComponent } from './common/stats-preview/stats-preview.component';
import { AdminInboxComponent } from './admin/admin-inbox/admin-inbox.component';
import { InboxComponent } from './common/inbox/inbox.component';
import { ChatComponent } from './common/chat/chat.component';
import { DriverInboxComponent } from './driver/driver-inbox/driver-inbox.component';
import { PassengerInboxComponent } from './passenger/passenger-inbox/passenger-inbox.component';
import { DriverRejectComponent } from './driver/driver-reject/driver-reject.component';
import { RideInProgressPassengerComponent } from './passenger/ride-in-progress-passenger/ride-in-progress-passenger.component';
import { AdminHomeComponent } from './admin/admin-home/admin-home.component';
import { UserPanicNotificationComponent } from './admin/user-panic-notification/user-panic-notification.component';
import { ScheduledRideComponent } from './common/scheduled-ride/scheduled-ride.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    NavMainComponent,
    HomeComponent,
    MapComponent,
    GetARideComponent,
    RouteCompactComponent,
    PassengerAccountComponent,
    BasicInfoCardComponent,
    EditBasicInfoCardComponent,
    PickProfilePicComponent,
    AccountVerificationComponent,
    AdminAccountComponent,
    AdminBasicInfoCardComponent,
    AdminEditBasicInfoCardComponent,
    AdminDriverRegisterComponent,
    SidebarComponent,
    LoginComponent,
    AccountRecoveryComponent,
    PassengerRideHistoryComponent,
    RouteHistoryCardComponent,
    DriverAccountComponent,
    DriverUpdateRequestsComponent,
    UrlSentizerPipe,
    DriverRideHistoryComponent,
    UsersPreviewCardComponent,
    DetailedRidePreviewComponent,
    AdminRideHistoryComponent,
    RateRideDialogComponent,
    FavoriteRoutesCardComponent,
    PassengerHomeComponent,
    ChangePasswordComponent,
    DriverHomeComponent,
    PassengerCountComponent,
    MoreRideSettingsComponent,
    LinkedRidePersonsComponent,
    DriverComingComponent,
    DriverNewRideComponent,
    InvitationResponseComponent,
    AdminBlockingComponent,
    BlockingUsersListComponent,
    UserNotesListComponent,
    StatsPreviewComponent,
    AdminInboxComponent,
    InboxComponent,
    ChatComponent,
    DriverInboxComponent,
    PassengerInboxComponent,
    DriverRejectComponent,
    RideInProgressPassengerComponent,
    AdminHomeComponent,
    UserPanicNotificationComponent,
    ScheduledRideComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptorService,
      multi: true,
    },
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
