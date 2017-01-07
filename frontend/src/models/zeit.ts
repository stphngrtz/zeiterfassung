export class Zeit {
  id: string;
  von: Date;
  bis: Date;
  bemerkung: string;
  profilId: string;

  constructor(id: string, von:Date, bis:Date, bemerkung:string, profilId:string) {
    this.id = id;
    this.von = von;
    this.bis = bis;
    this.bemerkung = bemerkung;
    this.profilId = profilId;
  }

  isValid(f): boolean {
    if (!(this.profilId && this.profilId.length > 0)) {
      f("Profil fehlt!");
      return false;
    }
    if (!(this.von instanceof Date && !isNaN(this.von.valueOf()))) {
      f("Ungültiges Datum!");
      return false;
    }
    if (!(this.bis instanceof Date && !isNaN(this.bis.valueOf()))) {
      f("Ungültiges Datum!");
      return false;
    }
    if(this.von >= this.bis) {
      f("Ungültiges Zeitintervall!");
      return false;
    }
    return true;
  }

  getStunden() {
    return this.bis.getHours() - this.von.getHours() + this.getMinutenAlsStundenanteil(this.bis.getMinutes()) - this.getMinutenAlsStundenanteil(this.von.getMinutes());
  }

  private getMinutenAlsStundenanteil(minuten: number) {
    if (minuten < 15)
      return 0;
    if (minuten < 30)
      return 0.25;
    if (minuten < 45)
      return 0.5;

    return 0.75;
  }
}
