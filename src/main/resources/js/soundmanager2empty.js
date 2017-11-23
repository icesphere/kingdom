/** @license
 * SoundManager 2: Javascript Sound for the Web
 * --------------------------------------------
 * http://schillmania.com/projects/soundmanager2/
 *
 * Copyright (c) 2007, Scott Schiller. All rights reserved.
 * Code provided under the BSD License:
 * http://schillmania.com/projects/soundmanager2/license.txt
 *
 * V2.97a.20101010
 */

/*jslint white: false, onevar: true, undef: true, nomen: false, eqeqeq: true, plusplus: false, bitwise: true, regexp: true, newcap: true, immed: true, regexp: false */
/*global window, SM2_DEFER, sm2Debugger, alert, console, document, navigator, setTimeout, setInterval, clearInterval, Audio */

(function(window) {

var soundManager = null;

function SoundManager(smURL, smID) {
  this.url = "";
  this.useFlashBlock = false;
  this.onload = false;

  this.createSound = function(oOptions) {
  }

  this.play = function(sID, oOptions) {
  }

} // SoundManager()


soundManager = new SoundManager();

// public interfaces
window.SoundManager = SoundManager; // constructor
window.soundManager = soundManager; // public instance: API, Flash callbacks etc.

}(window));
