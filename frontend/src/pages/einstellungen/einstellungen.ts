import { Component } from '@angular/core';

import { NavController } from 'ionic-angular';
import { Storage } from '@ionic/storage';

@Component({
  selector: 'page-einstellungen',
  templateUrl: 'einstellungen.html'
})
export class EinstellungenPage {

  url: string;
  token: string;
  debug: boolean;

  constructor(
    public navCtrl: NavController,
    public storage: Storage
  ) {
    storage.get('url').then(val => {
      this.url = val;
    });
    storage.get('token').then(val => {
      this.token = val;
    });
    storage.get('debug').then(val => {
      this.debug = val;
    });
  }

  ionViewWillLeave() {
    this.storage.set('url', this.url);
    this.storage.set('token', this.token);
    this.storage.set('debug', this.debug);
  }
}
