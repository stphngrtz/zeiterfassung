import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';

import 'rxjs/add/operator/map';

import { Storage } from '@ionic/storage';

import { Profil } from '../models/profil';

@Injectable()
export class ProfileProvider {
  data:Profil[];

  constructor(public storage: Storage, public http: Http) {
  }

  load(force: boolean) {
    if (!force && this.data) {
      return Promise.resolve(this.data);
    }
    return this.run((resolve, reject, url, options) => {
      this.http.get(url + '/profile', options)
        .map(res => res.json())
        .map(data => data.map(d => new Profil(d.id, d.name, d.farbe)))
        .subscribe(data => {
          this.data = data;
          this.data.sort((a,b) => a.name.localeCompare(b.name));
          resolve(this.data);
        }, error => {
          reject(error);
        });
    });
  }

  create(profil: Profil) {
    return this.run((resolve, reject, url, options) => {
      this.http.post(url + '/profile', JSON.stringify(profil), options)
        .map(res => res.json())
        .map(data => new Profil(data.id, data.name, data.farbe))
        .subscribe(data => {
          this.data.push(data);
          this.data.sort((a,b) => a.name.localeCompare(b.name));
          resolve(data);
        }, error => {
          reject(error);
        });
    });
  }

  update(profil: Profil) {
    return this.run((resolve, reject, url, options) => {
      this.http.put(url + '/profile/' + profil.id, JSON.stringify(profil), options)
        .subscribe(res => {
          this.data.sort((a,b) => a.name.localeCompare(b.name));
          resolve(profil);
        }, error => {
          reject(error);
        });
    });
  }

  delete(profil: Profil) {
    return this.run((resolve, reject, url, options) => {
      this.http.delete(url + '/profile/' + profil.id, options)
        .subscribe(res => {
          let index = this.data.indexOf(profil);
          if (index >= 0)
            this.data.splice(index, 1);
          resolve(profil);
        }, error => {
          reject(error);
        })
    });
  }

  private run(f) {
    return new Promise((resolve, reject) => {
      this.storage.get("token").then(token => {
        if (!token)
          reject("Es wurde kein Token angegeben!");
        else {
          let headers = new Headers({ 'Token': token });
          let options = new RequestOptions({ headers: headers });

          this.storage.get("url").then(url => {
            if (!url)
              reject("Es wurde keine URL angegeben!");
            else {
              f(resolve, reject, url, options);
            }
          });
        }
      });
    });
  }
}
