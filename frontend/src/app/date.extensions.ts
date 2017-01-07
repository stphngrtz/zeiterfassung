export {}

declare global {
   interface Date {
      toLocalISOString(): string;
      toLocalISODateString(): string;
      toLocalISOTimeString(): string;
   }
}

Date.prototype.toLocalISOString = function (): string {
  // If not called on a Date instance, or timevalue is NaN, return undefined
  if (isNaN(this) || Object.prototype.toString.call(this) != '[object Date]') return;

  // Copy date so don't modify original
  var d = new Date(+this);
  var offset = d.getTimezoneOffset();
  var offSign = offset > 0? '-' : '+';
  offset = Math.abs(offset);
  var tz = offSign + ('0' + (offset/60|0)).slice(-2) + ':' + ('0' + offset%60).slice(-2)
  return new Date(d.setMinutes(d.getMinutes() - d.getTimezoneOffset())).toISOString().slice(0,-1) + tz;
};

Date.prototype.toLocalISODateString = function (): string {
  // If not called on a Date instance, or timevalue is NaN, return undefined
  if (isNaN(this) || Object.prototype.toString.call(this) != '[object Date]') return;

  return this.toLocalISOString().slice(0,10);
};

Date.prototype.toLocalISOTimeString = function (): string {
  // If not called on a Date instance, or timevalue is NaN, return undefined
  if (isNaN(this) || Object.prototype.toString.call(this) != '[object Date]') return;

  return this.toLocalISOString().slice(11,19);
};

Date.prototype.toJSON = function (): string {
  // If not called on a Date instance, or timevalue is NaN, return undefined
  if (isNaN(this) || Object.prototype.toString.call(this) != '[object Date]') return;

  return this.toLocalISOString();
};
