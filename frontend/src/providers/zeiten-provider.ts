import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';

import 'rxjs/add/operator/map';

import { Storage } from '@ionic/storage';

import { Zeit } from '../models/zeit';

@Injectable()
export class ZeitenProvider {
  data:Zeit[];

  constructor(public storage: Storage, public http: Http) {
  }

  load(force: boolean) {
    if (!force && this.data) {
      return Promise.resolve(this.data);
    }
    return this.run((resolve, reject, url, options) => {
      this.http.get(url + '/zeiten', options)
        .map(res => res.json())
        .map(data => data.map(d => new Zeit(d.id, new Date(d.von), new Date(d.bis), d.bemerkung, d.profilId)))
        .subscribe(data => {
          this.data = data;
          this.data.sort((a,b) => b.von.valueOf() - a.von.valueOf());
          resolve(this.data);
        }, error => {
          reject(error);
        });
    });
  }

  create(zeit: Zeit) {
    return this.run((resolve, reject, url, options) => {
      this.http.post(url + '/zeiten', JSON.stringify(zeit), options)
        .map(res => res.json())
        .map(data => new Zeit(data.id, new Date(data.von), new Date(data.bis), data.bemerkung, data.profilId))
        .subscribe(data => {
          this.data.push(data);
          this.data.sort((a,b) => b.von.valueOf() - a.von.valueOf());
          resolve(data);
        }, error => {
          reject(error);
        });
    });
  }

  update(zeit: Zeit) {
    return this.run((resolve, reject, url, options) => {
      this.http.put(url + '/zeiten/' + zeit.id, JSON.stringify(zeit), options)
        .subscribe(res => {
          this.data.sort((a,b) => b.von.valueOf() - a.von.valueOf());
          resolve(zeit);
        }, error => {
          reject(error);
        });
    });
  }

  delete(zeit: Zeit) {
    return this.run((resolve, reject, url, options) => {
      this.http.delete(url + '/zeiten/' + zeit.id, options)
        .subscribe(res => {
          let index = this.data.indexOf(zeit);
          if (index >= 0)
            this.data.splice(index, 1);
          resolve(zeit);
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
