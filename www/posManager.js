var DevicePosLoader = function(require, exports, module) {
  var exec = require('cordova/exec');

  var intervalId;
  
  function DevicePos() {}

  DevicePos.prototype.start = function(success, failure, timeOffset) {
    exec(success, failure, 'AndroidPoSManager', 'start', []);
    intervalId = setInterval(function() {
      exec(success, failure, 'AndroidPoSManager', 'getCurrent', []);
    }, timeOffset || 500);
  };

  DevicePos.prototype.stop = function(success, failure) {
    if (intervalId) {
      clearInterval(intervalId);
      intervalId = null;
    }
    exec(success, failure, 'AndroidPoSManager', 'stop', []);
  };
  
  var devicePos = new DevicePos();
  module.exports = devicePos;
};

DevicePosLoader(require, exports, module);

cordova.define("cordova/plugin/DevicePos", DevicePosLoader);
