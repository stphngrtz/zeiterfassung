import { Component } from '@angular/core';

import { NavController } from 'ionic-angular';
import { AlertController } from 'ionic-angular';

import { ZeiterfassungAddEditPage } from './zeiterfassung-add-edit';
import { Zeit } from '../../models/zeit';
import { Profil } from '../../models/profil';
import { ZeitenProvider } from  '../../providers/zeiten-provider';
import { ProfileProvider } from  '../../providers/profile-provider';

@Component({
  selector: 'page-zeiterfassung',
  templateUrl: 'zeiterfassung.html'
})
export class ZeiterfassungPage {
  zeiten: Zeit[];
  profile: Profil[];

  constructor(
    public navCtrl: NavController,
    public alertCtrl: AlertController,
    public zeitenProvider: ZeitenProvider,
    public profileProvider: ProfileProvider
  ) {
  }

  ionViewDidLoad() {
    this.reload(null, false);
  }

  reload(event, force: boolean) {
    this.profileProvider.load(force).then(profile => {
      this.profile = profile;
    }, error => {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: error,
        buttons: ['OK']
      }).present();
    });
    this.zeitenProvider.load(force).then(zeiten => {
      this.zeiten = zeiten;
    }, error => {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: error,
        buttons: ['OK']
      }).present();
    });
  }

  add(event) {
    if (!this.profile || this.profile.length == 0) {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: "Keine Profile vorhanden!",
        buttons: ['OK']
      }).present();
    }
    else {
      this.navCtrl.push(ZeiterfassungAddEditPage, {
        zeit: new Zeit("", new Date(), new Date(), "ARBEIT", "", this.profile.length == 1 ? this.profile[0].id : "")
      });
    }
  }

  edit(event, zeit) {
    this.navCtrl.push(ZeiterfassungAddEditPage, {
      zeit: zeit
    });
  }

  getProfil(zeit: Zeit) {
    for (let profil of this.profile) {
      if (profil.id == zeit.profilId)
        return profil;
    }
    return new Profil("", "n/a", "red");
  }
}
