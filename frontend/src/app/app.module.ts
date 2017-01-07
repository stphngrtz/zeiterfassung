import { NgModule, ErrorHandler } from '@angular/core';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { Storage } from '@ionic/storage';
import { MyApp } from './app.component';
import { TabsPage } from '../pages/tabs/tabs';
import { ZeiterfassungPage } from '../pages/zeiterfassung/zeiterfassung';
import { ZeiterfassungAddEditPage } from '../pages/zeiterfassung/zeiterfassung-add-edit';
import { AuswertungPage } from '../pages/auswertung/auswertung';
import { ProfilePage } from '../pages/profile/profile';
import { ProfileAddEditPage } from '../pages/profile/profile-add-edit';
import { EinstellungenPage } from '../pages/einstellungen/einstellungen';
import { ZeitenProvider} from '../providers/zeiten-provider';
import { ProfileProvider } from '../providers/profile-provider';

@NgModule({
  declarations: [
    MyApp,
    ZeiterfassungPage,
    ZeiterfassungAddEditPage,
    AuswertungPage,
    ProfilePage,
    ProfileAddEditPage,
    EinstellungenPage,
    TabsPage
  ],
  imports: [
    IonicModule.forRoot(MyApp)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    ZeiterfassungPage,
    ZeiterfassungAddEditPage,
    AuswertungPage,
    ProfilePage,
    ProfileAddEditPage,
    EinstellungenPage,
    TabsPage
  ],
  providers: [{provide: ErrorHandler, useClass: IonicErrorHandler}, Storage, ZeitenProvider, ProfileProvider]
})
export class AppModule {}
