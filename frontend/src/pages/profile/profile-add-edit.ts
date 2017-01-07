import { Component } from '@angular/core';

import { NavController, NavParams } from 'ionic-angular';
import { AlertController } from 'ionic-angular';

import { Profil } from '../../models/profil';
import { ProfileProvider } from  '../../providers/profile-provider';

@Component({
  selector: 'page-profile-add-edit',
  templateUrl: 'profile-add-edit.html'
})
export class ProfileAddEditPage {

  profil:Profil;

  constructor(
    public navCtrl: NavController,
    public navParams: NavParams,
    public alertCtrl: AlertController,
    public profileProvider: ProfileProvider
  ) {
    this.profil = navParams.get('profil');
  }

  save(event) {
    if (this.profil.isValid(error => this.alertCtrl.create({
      title: 'Oops!',
      subTitle: error,
      buttons: ['OK']
    }).present())) {
      if (this.profil.id)
        this.profileProvider.update(this.profil).then(data => this.navCtrl.pop(), error => {
          this.alertCtrl.create({
            title: 'Oops!',
            subTitle: error,
            buttons: ['OK']
          }).present();
        });
      else
        this.profileProvider.create(this.profil).then(data => this.navCtrl.pop(), error => {
          this.alertCtrl.create({
            title: 'Oops!',
            subTitle: error,
            buttons: ['OK']
          }).present();
        });
    }
  }

  delete(event) {
    this.profileProvider.delete(this.profil).then(data => this.navCtrl.pop(), error => {
      this.alertCtrl.create({
        title: 'Oops!',
        subTitle: error,
        buttons: ['OK']
      }).present();
    });
  }
}
