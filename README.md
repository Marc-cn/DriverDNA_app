# DRIVES – Android App for Automotive Customized Services  

## Overview  
**DRIVES (DRIVEr Services)** is an Android application that connects to a vehicle through an **OBD-II Bluetooth dongle** to collect driving data and generate a **Driver DNA** profile. Based on the driver’s style (braking, turning, speeding, RPM), the app provides **personalized services** such as eco-driving rewards, service discounts, and recommendations.  

The app can be installed either on:  
- **Any Android smartphone** (minimum Android 4.0.1 – Ice Cream Sandwich).  
- **In-vehicle infotainment systems** that support Android application installation.  

The solution is generic and works with any vehicle equipped with a standard **OBD-II port** (available on most cars manufactured after 1996).  

---

## Requirements  
- Android device (smartphone or infotainment) with **Android 4.0.1 or later**.  
- **Bluetooth-enabled OBD-II dongle** (ELM327-compatible).  
- Vehicle with an **OBD-II port** (typically located under the dashboard, beneath the steering wheel).  
- Internet connection (for uploading data and requesting services).  

---

## Installation  
1. **Download and install** the APK on your Android device (smartphone or infotainment).  
   - Enable **“Install from Unknown Sources”** in Android settings if needed.  
2. Plug the **OBD-II Bluetooth dongle** into the vehicle’s OBD-II port.  
3. Turn on the vehicle’s ignition (engine on or accessory mode).  
4. Open the **DRIVES app** on your device.  

---

## First-Time Setup  
1. From the main menu, go to **Settings**.  
2. **Pair the OBD-II dongle** via Bluetooth.  
3. Select the **OBD protocol** (usually “Auto” works best).  
4. Set update frequency (default: **1 second**).  
5. *(Optional)* Enable **full logging** to collect data for Driver DNA computation.  

---

## Usage  
- **Live Data**: Displays real-time speed, RPM, engine runtime, and vehicle position on a map.  
- **Driver DNA**: Computes your driving profile based on collected data (braking, turning, speeding, RPM).  
- **Request Services**:  
  - Car-sharing discounts.  
  - Charging/gas station offers.  
  - Restaurant promotions.  
  - Workshop/repair services.  
- **History**: Shows past service requests and personalized results.  

---

## Notes  
- If your vehicle supports **Android app installation on infotainment**, DRIVES can run directly there. Otherwise, use a smartphone.  
- Data collected is anonymized and transmitted securely to external infrastructure for analysis.  
- The app is designed to encourage **eco-friendly and safe driving** while rewarding drivers.  

---

## License  
This project is based on the European project **E-Corridor (H2020, Grant Agreement No. 883135)** and uses libraries provided under **Apache License 2.0**.  
