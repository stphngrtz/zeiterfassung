export class Profil {
  id: string;
  name: string;
  farbe: string;

  constructor(id: string, name:string, farbe:string) {
    this.id = id;
    this.name = name;
    this.farbe = farbe;
  }

  isValid(f): boolean {
    if (!(this.name && this.name.length > 0)) {
      f("Name fehlt!");
      return false;
    }
    if (!(this.farbe && this.farbe.length > 0)) {
      f("Farbe fehlt!");
      return false;
    }
    return true;
  }
}
