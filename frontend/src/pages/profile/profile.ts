import { Component } from '@angular/core';

import { NavController } from 'ionic-angular';
import { AlertController } from 'ionic-angular';

import { ProfileAddEditPage } from './profile-add-edit';
import { Profil } from '../../models/profil';
import { ProfileProvider } from  '../../providers/profile-provider';

@Component({
  selector: 'page-profile',
  templateUrl: 'profile.html'
})
export class ProfilePage {
  profile: Profil[];

  constructor(
    public navCtrl: NavController,
    public alertCtrl: AlertController,
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
  }

  add(event) {
    this.navCtrl.push(ProfileAddEditPage, {
      profil: new Profil("", "", "")
    });
  }

  edit(event, profil) {
    this.navCtrl.push(ProfileAddEditPage, {
      profil: profil
    });
  }
}
