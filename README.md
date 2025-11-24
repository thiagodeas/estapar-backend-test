# Estapar Backend Developer Test

## 📌 Visão Geral
Este projeto implementa a solução para o **Estapar Backend Developer Test (V1.4)**.  
A aplicação backend roda localmente, enquanto o simulador roda em Docker.

Inclui:
- Java 21  
- Spring
- MySQL Local
- Simulador Estapar (`cfontes0estapar/garage-sim:1.0.0`)
- Recebimento e processamento de eventos ENTRY / PARKED / EXIT
- Cálculo de preço com multiplicadores dinâmicos
- Endpoint /revenue funcionando corretamente

---

## 📂 Pré-requisitos

### 🔧 1. Java
Java 21:

```bash
java -version
```

### 🗄️ 2. MySQL rodando localmente
Certifique-se de que o MySQL esteja rodando e acessível:

- Host: `localhost`
- Porta: `3306`
- Usuário e senha conforme configurado no `.env`

### 🐳 3. Docker (somente para rodar o simulador)
Embora a aplicação rode localmente, o simulador **ainda depende de Docker**.

---

## 🔐 Configuração de Variáveis de Ambiente

Crie um arquivo chamado **`.env`** na raiz do projeto com:

```env
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=estapar_db
MYSQL_USER=your_user
MYSQL_PASSWORD=your_password

DB_URL=jdbc:mysql://localhost:3306/${MYSQL_DATABASE}
GARAGE_URL=http://localhost:3000/garage
SERVER_PORT=3003
```

Agora exporte as variáveis no terminal antes de rodar a aplicação:

### Windows (PowerShell)
```powershell
$env:MYSQL_ROOT_PASSWORD="your_root_password"
$env:MYSQL_DATABASE="estapar_db"
$env:MYSQL_USER="your_mysql_user"
$env:MYSQL_PASSWORD="your_mysql_password"

$env:DB_URL="jdbc:mysql://localhost:3306/$env:MYSQL_DATABASE"
$env:GARAGE_URL="http://localhost:3000/garage"
$env:SERVER_PORT="3003"
```

### Linux / MacOS
```bash
export MYSQL_ROOT_PASSWORD=your_root_password
export MYSQL_DATABASE=estapar_db
export MYSQL_USER=your_mysql_user
export MYSQL_PASSWORD=your_mysql_password

export DB_URL="jdbc:mysql://localhost:3306/${MYSQL_DATABASE}"
export GARAGE_URL="http://localhost:3000/garage"
export SERVER_PORT=3003
```

---

## ⚙️ Configuração do `application.properties`

Seu arquivo deve ficar assim:

```properties
spring.application.name=estaparbackendtest

spring.datasource.url=${DB_URL}
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

garage.url=${GARAGE_URL}

server.port=${SERVER_PORT:3003}
```

---

## ▶️ Como rodar o simulador Estapar

O simulador utiliza a porta 3000 para expor o endpoint /garage.

Windows (Docker Desktop)

O modo host network não é suportado, portanto é necessário mapear a porta manualmente:
```bash
docker run -d --name garage-sim -p 3000:3000 --add-host=localhost:host-gateway cfontes0estapar/garage-sim:1.0.0
```

Linux / MacOS

Pode ser usado o modo host network normalmente:
```bash
docker run -d --network=host cfontes0estapar/garage-sim:1.0.0
```


### Ver logs do simulador
```bash
docker logs -f garage-sim
```

### Verificar se está funcionando
```bash
curl http://localhost:3000/garage
```

Deve retornar a configuração da garagem.

---

## ▶️ Rodando o backend localmente

Com tudo configurado:

### Windows (PowerShell)
```powershell
mvn spring-boot:run
```

### Linux / MacOS
```bash
./mvnw spring-boot:run
```

O backend faz:

1. GET /garage

2. Carrega setores e vagas

3. Armazena no MySQL

Ao iniciar, você verá no log:

```
Aplicação iniciada.
Carregando configuração inicial da garagem...
Configuração da garagem carregada com sucesso!
```



Isso significa que:

- Conectou com o MySQL  
- Chamou o endpoint `/garage` do simulador  
- Gravou setores e vagas no banco  

---

## 🔄 Testando eventos automáticos

Após chamar uma vez:

```bash
curl http://localhost:3000/garage
```

O simulador começa a enviar eventos para:

```
POST http://localhost:3003/webhook
```

O backend processará automaticamente:

- ENTRY
- PARKED
- EXIT

Você verá logs como:

```
Entry successful for plate: ABC1234
Parked successful for plate...
Exit successful for plate...
```

E no backend:

```
Salvando evento ENTRY...
Atualizando vaga...
Calculando preço...
```

---

## 🧪 Testando manualmente ENTRY / PARKED / EXIT

Caso queira testar manualmente, pause o simulador para facilitar o processo:

```bash
docker pause garage-sim
```

### ➤ ENTRY 
Method: POST

URL: http://localhost:3003/webhook

Body (JSON):
```json
{
  "license_plate": "AAA0001",
  "entry_time": "2025-11-24T17:40:00.000Z",
  "event_type": "ENTRY"
}
```

Resposta (HTTP 200).

#### Consulte no banco:
```bash
SELECT * FROM parking_event WHERE license_plate = 'AAA0001';
```

### Resultado:
```bash

```

### ➤ PARKED 
Method: POST

URL: http://localhost:3003/webhook

Body (JSON):
```json
{
  "license_plate": "AAA0001",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

Resposta (HTTP 200).

#### Consulte no banco:
```bash
SELECT * FROM parking_event WHERE license_plate = 'AAA0001';
```

### Resultado:
status muda para PARKED  
spot_id agora = 1 (ou a vaga correta do setor)
occupied = 1 na tabela spot


### ➤ EXIT – Insomnia
Method: POST

URL: http://localhost:3003/webhook

Body (JSON):
```json
{
  "license_plate": "AAA0001",
  "exit_time": "2025-11-24T18:50:00.000Z",
  "event_type": "EXIT"
}
```

Resposta (HTTP 200).

#### Consulte no banco:
```bash
SELECT * FROM parking_event WHERE license_plate = 'AAA0001';
```

### Resultado:
Calcula o valor
Atualiza o status para EXIT
Libera a vaga (occupied = 0)


## 💰 Testando o endpoint /revenue

O endpoint de receita é POST, recebendo um JSON no corpo da requisição.

Exemplo:
```json
{
  "date": "2025-11-24",
  "sector": "A"
}
```

Resposta:

```json
{
  "amount": 36.45,
  "currency": "BRL",
  "timestamp": "2025-11-24T00:00"
}
```

---

## 🛠️ Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── com/thiagoalves/estaparbackendtest/
            ├── EstaparbackendtestApplication.java
            │
            ├── config/
            │   └── StartupConfig.java
            │
            ├── controllers/
            │   ├── RevenueController.java
            │   └── WebhookController.java
            │
            ├── dtos/
            │   ├── revenue/
            │   │   ├── RevenueRequestDTO.java
            │   │   └── RevenueResponseDTO.java
            │   │
            │   ├── webhook/
            │   │   ├── EntryEventDTO.java
            │   │   ├── ExitEventDTO.java
            │   │   ├── GenericWebhookDTO.java
            │   │   └── ParkedEventDTO.java
            │   │
            │   ├── GarageResponseDTO.java
            │   ├── SectorDTO.java
            │   └── SpotDTO.java
            │
            ├── exceptions/
            │   ├── NoAvailableSectorException.java
            │   ├── SectorNotFoundException.java
            │   ├── SpotAlreadyOccupiedException.java
            │   ├── SpotNotFoundException.java
            │   ├── VehicleAlreadyInsideException.java
            │   └── VehicleNotInsideException.java
            │
            ├── models/
            │   ├── enums/
            │   │   └── ParkingEventStatus.java
            │   │
            │   ├── ParkingEvent.java
            │   ├── Sector.java
            │   └── Spot.java
            │
            ├── repositories/
            │   ├── ParkingEventRepository.java
            │   ├── SectorRepository.java
            │   └── SpotRepository.java
            │
            └── services/
                ├── EntryEventService.java
                ├── ExitEventService.java
                ├── ParkedEventService.java
                ├── RevenueService.java
                └── GarageService.java

```
---
