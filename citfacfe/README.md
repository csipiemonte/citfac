<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

# Città facile (citfac)

Front-end (PWA) di Città facile

## Info sviluppo

Vite + Vuejs 3 + VitePWA

Versione node: 16

Bootstrap Italia


## IDE Setup consigliato

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur) + [TypeScript Vue Plugin (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.vscode-typescript-vue-plugin).


## Istalalzione progetto

```sh
npm install
```

### Avvia il progetto in modalità sviluppo (hot-code reloading, error reporting, etc.)

```sh
npm run dev
```

### Builda l'app per test

```sh
npm run test
```


### Builda l'app per prod

```sh
npm run prod
```


### Distinzione tra ambienti

Sono stati configurati 3 ambienti

1. Sviluppo
2. Test
3. Produzione

E' possibile customizzare il comportamento di ogni ambiente tramite le variabili d'ambiente presenti nei files `.env.*`.

### Routes 

La lista delle routes si trova sul file `src/router/routes.js`

I meta title vengono creati dinamicamente dal file `src/router/index` per consentire la lettura dei dati dalle api (utile per il componente di feedback)

### API predefinite

Il client HTTP (axios) è già preconfigurato nel file `src/boot/axios` mentre le chiamate alle API necessarie sono
presenti nel file `src/services/api`


### Notifiche push

I file di service worker e manifest saranno esterni al progetto.
La registrazione sel service worker avviene tramite il file `registerServiceWorker.js` dichiarato nel `main.js`
La configurazione di firebase e registrazione token fcm è presente nel componente `src\components\TheGuardFcm.vue`




