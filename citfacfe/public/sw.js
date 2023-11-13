/*
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */


importScripts(
  "https://storage.googleapis.com/workbox-cdn/releases/6.4.1/workbox-sw.js"
);

// Importiamo le librerie di firebase che ci servono.
// NOTA: solo FCM funziona nei service workers
importScripts("https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js");
importScripts(
  "https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js"
);



self.__precacheManifest = [].concat(self.__precacheManifest || []);
workbox.precaching.precacheAndRoute(self.__precacheManifest, {});

const firebaseConfig = {
 
};


// FCM CODE
// ---------------------------------------------------------------------------------------------------------------------
// Inizializiamo Firebase
// Diciamo qual è il sender che vogliamo ascoltare tramite il "messagingSenderId"
// disponibile nella configurazione del client di firebase
firebase.initializeApp(firebaseConfig);

// Otteniamo l'istanza di FCM (Firebase Cloud Messaging) per gestire i messaggi in background
const messaging = firebase.messaging();

// Questo handler viene chiamato quando il messaggio push inviato non contiene la proprietà "notification".
// @NOTE: se il messaggio push inviato contiene la proprietà "notification"
//        => firebase crea un wrapper per generare
//        una notifica veloce, che ha molte restrizioni ed è meno controllabile
messaging.onBackgroundMessage((payload) => {
  console.debug("Received background message ", payload);

  // Customize notification here

  let notification = payload.notification || payload.data

  let notificationTitle = notification.title;
  let notificationOptions = {
    body: `Inviato al topic: ${data.topic}`,
    actions: notification.actions,
  };

  // Mostriamo la notifica all'utente
  return self.registration.showNotification(
    notificationTitle,
    notificationOptions
  );
});


self.skipWaiting();
self.clients.claim();