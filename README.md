# zoho-trello-integration

A Spring Boot-based automation tool that connects Zoho CRM and Trello. It creates Trello boards and task lists automatically for new implementation projects in Zoho Deals, streamlining project kickoff and task tracking.


## Features

- Connects Zoho CRM and Trello via APIs
- Creates Trello boards for specific Zoho Deals
- Automatically adds lists and starter cards
- Updates Zoho CRM with the new Trello board ID


## Tech Stack

- Java 21
- Spring Boot
- RestTemplate + Jackson
- Zoho CRM API
- Trello REST API


## Environment Variables

To run this project, you will need to add the following environment variables to your main/resources/application.properties file

`zohoClientId`

`zohoClientSecret`

`redirectUri`

`trello.api.key`

`trello.access.token`

`zoho.access-token`

`zoho.refresh-token`

## Run Locally

Clone the project

```bash
  https://github.com/mostsfa538/zoho-trello-integration.git
```

Go to the project directory

```bash
  cd zoho-trello-integration
```

Install dependencies

```bash
  ./mvnw clean install
```

Start the server

```bash
  ./mvnw 