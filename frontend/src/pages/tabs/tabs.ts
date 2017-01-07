import { Component } from '@angular/core';

import { ZeiterfassungPage } from '../zeiterfassung/zeiterfassung';
import { AuswertungPage } from '../auswertung/auswertung';
import { ProfilePage } from '../profile/profile';
import { EinstellungenPage } from '../einstellungen/einstellungen';

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root: any = ZeiterfassungPage;
  tab2Root: any = AuswertungPage;
  tab3Root: any = ProfilePage;
  tab4Root: any = EinstellungenPage;

  constructor() {
  }
}
