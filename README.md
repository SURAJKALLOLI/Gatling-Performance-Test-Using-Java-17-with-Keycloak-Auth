# Introduction
### **What is Gatling?**
Gatling is a powerful open-source load testing solution.<br/>
Gatling is designed for continuous load testing and integrates with your development pipeline. Gatling includes a web recorder and colorful reports.

# Prerequisites
**Java version:** Gatling supports 64bits OpenJDK LTS (Long Term Support) versions: 8, 11 and 17. Other JVMs such as JDK 12, client JVMs, 32bits systems or OpenJ9 are not supported.<br/>
Gatling Open-Source tool can be downloaded at https://gatling.io/open-source/

# Getting Started
1. Start
2. Open the project on VScode or navigate to the `bin/` directory in root.
3. Execute the `gatling.bat` to initiate testing.
4. `results/` folder consists of the test report generated.

# Build and Test
The project structure consists of the following Simulation scenarios mentioned below.<br/>
**PublisherSimulationWebhook:** The scenario is built such that a single user can send a single request.<br/>
**PublisherSimulationWebhookMultiLoad:** The scenario is built such that a single user can send multiple requests.<br/>
**KeycloakMultiRealm:** In this scenario, we authenticate as master keycloak user and create multiple realms in a single keycloak instance.<br/>
**KeycloakUser:** In this scenario, we create a new realm and under that realm, we create a single client. The realm will have multiple users created, and each user will be assigned with unique ids. We also fetch the ids of users and delete all the users along with the realm.<br/>

Scorpio requests include:<br/>
**POST - Keycloak Authentication**<br/>
**GET - All Entity Types**<br/>
**GET - Webhook Token**<br/>
**POST - Create New Entity**<br/>
**POST - Create Subscription**<br/>
**POST - Upsert Entity**<br/>
**GET - Webhook Data**<br/>
**DELETE - Delete Entity**<br/>
**DELETE - Delete Subscription**<br/>

Keycloak requests include:<br/>
**POST - Request Master Access Token**<br/>
**POST - Create Realm**<br/>
**POST - Create Client**<br/>
**POST - Create User**<br/>
**GET - List Users**<br/>
**DELETE - Delete User**<br/>
**DELETE - Delete Realm**<br/>

> **_NOTE:_** To run scorpio tests without any user interaction, run `gatling_scorpio_subscription.bat`

> **_NOTE:_** To run keycloak tests without any user interaction, run `gatling_keycloak.bat`

# Further Help
To get more help on Gatling go check out the [Gatling](https://gatling.io/) official page.
