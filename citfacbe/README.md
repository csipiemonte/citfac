# BFF Città Facile

Il BFF (Backend For Frontend) di Città Facile è una soluzione software che funge da intermediario tra i client esterni e diverse API di destinazione. Offre una serie di funzionalità aggiuntive oltre alla semplice trasmissione delle richieste verso le API di destinazione.

### Funzionalità Principali

1. **Proxy verso API di Destinazione:** L'applicativo accetta richieste dai client esterni e le inoltra alle API di destinazione specificate.
2. **Ridirezione e Gestione delle Richieste:** In base a determinate condizioni configurabili, l'applicativo può reindirizzare le richieste verso diverse API di destinazione.
3. **Autenticazione e Autorizzazione:** L'applicativo offre un meccanismo per autenticare le richieste provenienti dai client e gestire l'autorizzazione per accedere alle API di destinazione.
4. **Manipolazione delle Richieste e delle Risposte:** Permette di modificare o arricchire le richieste prima di inoltrarle e di manipolare le risposte prima di restituirle ai client esterni.
5. **Gestione degli Errori:** Fornisce una gestione degli errori robusta per affrontare scenari di errore provenienti dalle API di destinazione.
6. **Configurabilità:** L'applicativo è configurabile attraverso file di configurazione e API REST, consentendo una personalizzazione flessibile delle sue funzionalità.

### Tecnologie Utilizzate

- Spring Framework (Spring Boot, Spring MVC, Spring JPA)
- Java
- Gestione delle dipendenze con Maven
- Gestione delle configurazioni con properties files
- Strumenti di logging (Log4j)

### Ambiti di Utilizzo

L'applicativo può essere utilizzato in scenari in cui è necessario gestire e orchestrare le chiamate a diverse API di destinazione, consentendo di implementare logiche di business aggiuntive, come l'elaborazione dei dati o la gestione degli errori, prima di restituire una risposta ai client esterni.

## Configurazione del Progetto

Prima di avviare il progetto, è importante configurare correttamente le proprietà nel file `application.properties`.

### Requisiti di Base

1. **Spring Boot:** Assicurati di avere un ambiente di sviluppo con Spring Boot installato.

2. **Database PostgreSQL:** Assicurati di avere un'istanza di PostgreSQL in esecuzione e di avere le credenziali corrette per l'accesso al database.

### Istruzioni per `application.properties`

Di seguito sono elencate le configurazioni specifiche nel file `application.properties`:

## Configurazioni di Base

- `server.port`: La porta su cui il server è in ascolto.

- `spring.datasource.url`: URL del database PostgreSQL.

- `spring.datasource.username`: Nome utente del database.

- `spring.datasource.password`: Password del database.

- `spring.jpa.hibernate.ddl-auto`: Modalità di generazione dello schema del database.

- `spring.jpa.show-sql`: Mostra le query SQL eseguite.

- `spring.jpa.properties.hibernate.format_sql`: Abilita la formattazione delle query SQL.

- `spring.jpa.properties.hibernate.dialect`: Dialect di Hibernate per PostgreSQL.

## Configurazioni OkHttpClient

Ogni proxy del progetto utilizzerà queste impostazioni

- `ok-http-client.connect-timeout-seconds`: Timeout per la connessione.

- `ok-http-client.read-timeout-seconds`: Timeout per la lettura.

## Configurazioni Token API Manager

Chiavi necessarie per l'accesso alle api di diversi proxy

- `citta-facile-token.consumer-key`

- `citta-facile-token.consumer-secret`

## URL delle API

- `api-manager-url`: URL di base dell' API manager.

Di seguito troviamo gli url relativi delle api che passono tramite API manager, sarà l'implementazione del proxy dei vari servizi a concatenare l'url di base con quello relativo.

- `crm-api-base-url`: URL di base delle API di CRM.

- `mydocs-frontend-base-url`: URL di base del frontend di MyDocs.

- `appsched-api-base-url`: URL di base delle API di AppSched.

- `moon-api-base-url`: URL di base delle API di Moon.

## Profilo Client per Moon

- `moon-client-profile`: Token univoco per il proggetto di Città facile necessario per utilizzare le API di Moon.

## Configurazioni Notificatore

- `notificatore-api-base-url`: URL di base del notificatore.

- `notificatore-api-message-store`: Percorso API per il message store.

- `notificatore-api-message-mb`: Percorso API per il message MB.

- `notificatore-api-preferences`: Percorso API per le preferenze.

## Configurazioni API di Pagamento

- `ppay-api-base-url`: URL di base delle API di pagamento.

## Configurazioni Quarkus

- `quarkus.index-dependency.vladmihalcea.group-id`: ID del gruppo della dipendenza di Quarkus.

- `quarkus.index-dependency.vladmihalcea.artifact-id`: ID dell'artefatto della dipendenza di Quarkus.

## Implementazioni Proxy

Gli URL impostati servono come endpoint per le diverse implementazioni di proxy specifiche per ogni gruppo di URL. Assicurati di configurare correttamente questi valori in base alle esigenze del tuo progetto.
