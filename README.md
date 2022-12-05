# Introduction
### **What is Gatling?**
Gatling is a powerful open-source load testing solution.<br/>
Gatling is designed for continuous load testing and integrates with your development pipeline. Gatling includes a web recorder and colorful reports.

# Prerequisites
**Java version:** Gatling supports 64bits OpenJDK LTS (Long Term Support) versions: 8, 11 and 17. Other JVMs such as JDK 12, client JVMs, 32bits systems or OpenJ9 are not supported.<br/>
Gatling Open-Source tool can be downloaded at https://gatling.io/open-source/

# Getting Started
1. Clone the `PerfTest` repo
```js
git clone https://kmddk.visualstudio.com/KMD%20OS2IoT/_git/PerfTest
```
2. Open the project on VScode or navigate to the `bin/` directory in root.
3. Execute the `gatling.bat` to initiate testing.

# Build and Test
The project structure consists of 2 Simulation classes.<br/>
**PublisherSimulationWebhook:** The scenario is built such that a single user can send a single request.<br/>
**PublisherSimulationWebhookMultiLoad:** The scenario is built such that a single user can send multiple requests.<br/>

The requests include:<br/>
**POST - Keycloak Authentication**<br/>
**GET - All Entity Types**<br/>
**GET - Webhook Token**<br/>
**POST - Create New Entity**<br/>
**POST - Create Subscription**<br/>
**POST - Upsert Entity**<br/>
**GET - Webhook Data**<br/>
**DELETE - Delete Entity**<br/>
**DELETE - Delete Subscription**<br/>

> **_NOTE:_** To run tests without any user interaction, run `gatling_scorpio_subscription.bat`

# Further Help
To get more help on Gatling go check out the [Gatling](https://gatling.io/) official page.