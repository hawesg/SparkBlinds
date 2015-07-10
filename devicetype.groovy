/**
* Spark Core Blinds 
*
* Author: Garrett Hawes
* Date: 2014-04-01
* Device type to control blinds by way of a servo driven by a sparkcore. Based on work by Justin Wurth.
* TODO: GO through and get the data from settings for open, close and closed threshold 
*/
preferences {
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
  input("openThresh", "text", title: "Open Threshold", defaultValue: 10, required: false, displayDuringSetup: true)
  input("open", "text", title: "Open", defaultValue: 99, required: false, displayDuringSetup: true)
  input("closed", "text", title: "Closed", defaultValue: 20, required: false, displayDuringSetup: true)
}
// for the UI
metadata {
  // Automatically generated. Make future change here.
  definition (name: "Spark Core Blinds", author: "Garrett Hawes") {
    capability "Switch Level"
    capability "Switch"
    capability "Refresh"
    capability "Polling"
    
    attribute "openThresh", "string"
    attribute "open", "string"
    attribute "closed", "string"
    
    command "setLevel"
    command "getLevel"
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
  put'20'
  sendEvent(name: 'switch', value: 'on')
}
def off() {
  put'99'
  sendEvent(name: 'switch', value: 'off')
}
def setLevel(val) {
  def level = Math.min(val as Integer, 99)
  if(level>80){
    sendEvent(name: 'switch', value: 'off')
  }
  else{
    sendEvent(name: 'switch', value: 'on')
  }
  put val
}

def getLevel(){ //TODO
}

def poll() { //TODO
}

def refresh() { //TODO
}

private put(level) {
//Spark Core API Call
  sendEvent(name:"level",value:level)
  sendEvent(name:"switch.setLevel",value:level) 
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/setstate",
    body: [access_token: token, command: level],
  ) {response -> log.debug (response.data)}
}
