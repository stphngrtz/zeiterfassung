import { Component } from '@angular/core';

import { NavController, NavParams } from 'ionic-angular';
import { AlertController } from 'ionic-angular';

import { Zeit } from '../../models/zeit';
import { Profil } from '../../models/profil';
import { ZeitenProvider } from  '../../providers/zeiten-provider';
import { ProfileProvider } from  '../../providers/profile-provider';

@Component({
  selector: 'page-zeiterfassung-add-edit',
  templateUrl: 'zeiterfassung-add-edit.html'
})
export class ZeiterfassungAddEditPage {

  zeit: Zeit;
  profile: Profil[];

  datum: string;
  von: string;
  bis: string;

  constructor(
    public navCtrl: NavController,
    public navParams: NavParams,
    public alertCtrl: AlertController,
    public zeitenProvider: ZeitenProvider,
    public profileProvider: ProfileProvider
  ) {
    this.zeit = navParams.get('zeit');
    this.datum = this.zeit.von.toLocalISODateString();
    this.von = this.zeit.von.toLocalISOTimeString();
    this.bis = this.zeit.bis.toLocalISOTimeString();
  }

  ionViewDidLoad() {
    this.profileProvider.load(false).then(data => {
      this.profile = data;
    }, error => {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: error,
        buttons: ['OK']
      }).present();
    });
  }

  save(event) {
    this.zeit.von = new Date(this.datum + " " + this.von);
    this.zeit.bis = new Date(this.datum + " " + this.bis);

    if (this.zeit.isValid(error => this.alertCtrl.create({
      title: 'Oops!',
      subTitle: error,
      buttons: ['OK']
    }).present())) {
      if (this.zeit.id)
        this.zeitenProvider.update(this.zeit).then(data => this.navCtrl.pop(), error => {
          this.alertCtrl.create({
            title: 'Oops!',
            subTitle: error,
            buttons: ['OK']
          }).present();
        });
      else
        this.zeitenProvider.create(this.zeit).then(data => this.navCtrl.pop(), error => {
          this.alertCtrl.create({
            title: 'Oops!',
            subTitle: error,
            buttons: ['OK']
          }).present();
        });
    }
  }

  delete(event) {
    this.zeitenProvider.delete(this.zeit).then(data => this.navCtrl.pop(), error => {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: error,
        buttons: ['OK']
      }).present();
    });
  }
}
