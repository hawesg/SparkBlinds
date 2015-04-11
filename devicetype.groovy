/**
* Spark Core Blinds 
*
* Author: Garrett Hawes
* Date: 2014-04-01
* Device type to control blinds by way of a servo driven by a sparkcore. Based on work by Justin Wurth. 
* Open closed states are functioning, need to add poling as well as maybe favorites.
*/

preferences {
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
}
// for the UI
metadata {
  // Automatically generated. Make future change here.
  definition (name: "Spark Core Blinds", author: "Garrett Hawes") {
    capability "Switch Level"
    capability "Switch"
  }
  // tile definitions
  tiles {
    standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
      state "off", label:'closed', action:"switch.on", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"opening"
      state "on", label:'open', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"closing"
      state "opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#ffe71e"
      state "closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#ffe71e"
    }
    standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
      state "level", action:"switch level.setLevel"
    }
    main(["switch"])
    details(["switch", "refresh", "levelSliderControl"])
  }
}

def parse(String description) {
  log.error "This device does not support incoming events"
  return null
}

def on() {
  put'50'
  sendEvent(name: 'switch', value: 'on')
}
def off() {
  put'99'
  sendEvent(name: 'switch', value: 'off')
}
def setLevel(value) {
  def level = Math.min(value as Integer, 99)
  put value
}
private put(led) {
//Spark Core API Call
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/setstate",
    body: [access_token: token, command: led],
  ) {response -> log.debug (response.data)}
}
