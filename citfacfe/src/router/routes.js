/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

export const ERROR_404 = {
  path: "/:pathMatch(.*)*",
  name: "error404",
  component: () => import("../views/Page404.vue"),
};

export const HOME = {
  path: "/",
  name: "home",
  component: () => import("../views/HomeView.vue"),
};


export const LOGOUT = {
  path: "/logout",
  name: "logout",
  component: () => import("../views/PageLogout.vue"),
};

export const FAQ = {
  path: "",
  name: "faq",
  component: () => import("../views/faq/FaqPage.vue"),
  meta: {
    hideMenu: true, //consente di nascondere il menu dal layout quando richiesto
    title: "FAQ",
  },
};

export const FAQ_DETAIL = {
  path: ":id/dettagli/:metaTitle",
  name: "faq-dettagli-int",
  component: () => import("../views/faq/FaqPageDetail.vue"),
  alias: [":id/dettagli/"],
};

export const FAQ_HOME = {
  path: "/faq",
  name: "faq_home",
  component: () => import("../views/faq/FaqHome.vue"),

  meta: { hideMenu: true, title: "FAQ" },
  children: [FAQ, FAQ_DETAIL],
};


export const NEW_REQUEST = {
  path: "/nuova-richiesta",
  name: "nuova-richiesta",
  component: () => import("../views/assistance/PageRequestNew.vue"),
  meta: { hideMenu: true, title: "Richiesta assistenza" }, //consente di nascondere il menu dal layout quando richiesto
};

export const FEEDBACK = {
  path: "/feedback",
  name: "feedback",
  component: () => import("../views/assistance/FeedbackPage.vue"),
  meta: { hideMenu: true, title: "Richiesta assistenza" },
};

export const DOCUMENTS_LIST = {
  path: "documenti",
  name: "documents-list",
  component: () => import("../views/personal-area/PageDocumentsList.vue"),
  meta: { hideMenu: true, title: "Area Personale - Documenti" },
};

export const ACTIVITIES_LIST = {
  path: "attivita",
  name: "activities-list",
  component: () => import("../views/personal-area/PageActivitiesList.vue"),
  meta: { hideMenu: true, title: "Area Personale - Attività" },
};

export const MESSAGES_LIST = {
  path: "messaggi",
  name: "messages-list",
  component: () => import("../views/personal-area/PageMessagesList.vue"),
  meta: { hideMenu: true, title: "Area Personale - Notifiche" },
};

export const PERSONAL_AREA_DESKTOP = {
  path: "",
  name: "pa-desktop",
  component: () => import("../views/personal-area/PageDesktop.vue"),
  meta: { hideMenu: true, title: "Area Personale" },
};

export const SERVICES_LIST = {
  path: "servizi",
  name: "services-list",
  component: () => import("../views/personal-area/PageServicesList.vue"),
  meta: { hideMenu: true, title: "Area Personale - Servizi" },
};

export const PERSONAL_AREA = {
  path: "/area-personale",
  name: "area-personale",
  component: () => import("../views/personal-area/PagePersonalArea.vue"),
  meta: { hideMenu: true, title: "Area Personale" },
  beforeEnter: (to, from, next) => {
    //Prima verifichiamo se dopo la login dobbiamo reindirizzare l'utente verso un'altra pagina
    let { redirector } = to.query;
    if (redirector) {
      let path = decodeURIComponent(redirector);
      next(path);
    }else{
      next();
    }
  },
  redirect: PERSONAL_AREA_DESKTOP,
  children: [
    SERVICES_LIST,
    DOCUMENTS_LIST,
    ACTIVITIES_LIST,
    MESSAGES_LIST,
    PERSONAL_AREA_DESKTOP,
  ],
};



export const PAYMENT_IUV = {
  path: "/area-personale/pagamento-iuv/:iuv",
  name: "pagamento-iuv",
  component: () => import("../views/payment/PagePaymentWithIuv.vue"),
  meta: { hideMenu: true, title: "Pagamento con IUV" },
};

export const USER_PROFILE = {
  path: "/area-personale/profilo-utente/",
  name: "profilo-utente",
  component: () => import("../views/personal-area/PageProfile.vue"),
  meta: { hideMenu: true, title: "Profilo utente" },
};

export const USER_PROFILE_NEW = {
  path: "/area-personale/profilo-utente/componi/",
  name: "profilo-utente-nuovo",
  component: () => import("../views/personal-area/PageProfileNew.vue"),
  meta: { hideMenu: true, title: "Nuovo profilo utente" },
};

export const USER_PROFILE_NEW_SUCCESS = {
  path: "/area-personale/profilo-utente/componi/successo",
  name: "profilo-utente-nuovo-feedback",
  component: () => import("../views/personal-area/PageProfileNewSuccess.vue"),
  meta: { hideMenu: true, title: "Nuovo profilo utente" },
};



export const SERVICE_APPOINTMENT_BOOKING = {
  path: "/servizi/prenotazione-appuntamento",
  name: "servizio-prenotazione-appuntamento",
  component: () => import("../views/appointment/PageAppointmentBooking.vue"),
  meta: { title: "Prenotazione appuntamento" },
};
export const SERVICE_APPOINTMENT_BOOKING_SUCCESS = {
  path: "/servizi/prenotazione-appuntamento/successo/:id",
  name: "servizio-prenotazione-appuntamento-successo",
  component: () =>
    import("../views/appointment/PageAppointmentBookingSuccess.vue"),
  meta: { title: "Prenotazione appuntamento" },
};

export const SELF_PAYMENT_HOME = {
  path: "",
  name: "servizio-pagamento-spontaneo-home",
  component: () => import("../views/payment/PagePaymentHome.vue"),
};

export const SELF_PAYMENT_ONLINE = {
  path: "online",
  name: "servizio-pagamento-spontaneo-online",
  component: () => import("../views/payment/PagePaymentOnline.vue"),
  meta: { title: "Pagamento online tramite PAGOPA" },
};
export const SELF_PAYMENT_NOTICE = {
  path: "avviso-pagamento",
  name: "servizio-pagamento-spontaneo-avviso-pagamento",
  component: () => import("../views/payment/PagePaymentNoticeGeneration.vue"),
  meta: { title: "Avviso di pagamento" },
};

export const SELF_PAYMENT_NOTICE_FEEDBACK = {
  path: "/avviso-pagamento/esito",
  name: "servizio-pagamento-spontaneo-avviso-pagamento-esito",
  component: () =>
    import("../views/payment/PagePaymentNoticeGenerationFeedback.vue"),
  meta: { title: "Avviso di pagamento" },
};

export const SERVICE_SELF_PAYMENT = {
  path: "/servizi/pagamento-spontaneo",
  name: "servizio-pagamento-spontaneo",
  component: () => import("../views/payment/PagePaymentSpontaneous.vue"),
  redirect: SELF_PAYMENT_HOME,
  children: [SELF_PAYMENT_HOME, SELF_PAYMENT_ONLINE, SELF_PAYMENT_NOTICE],
  meta: { title: "Pagamento spontaneo" },
};

export const SERVICE_PAYMENT_FEEDBACK = {
  path: "/servizi/pagamento-esito",
  name: "servizio-pagamento-esito",
  component: () => import("../views/payment/PagePaymentFeedback.vue"),
  meta: { title: "Pagamento spontaneo" },
};

export const SERVICE_MOON = {
  path: "/servizi/:id",
  name: "servizio-moon",
  component: () => import("../views/services/PageServicesMoon.vue"),
};


const routes = [
  HOME,
  LOGOUT,
  FAQ_HOME,
  NEW_REQUEST,
  FEEDBACK,
  USER_PROFILE,
  USER_PROFILE_NEW,
  USER_PROFILE_NEW_SUCCESS,
  SERVICE_APPOINTMENT_BOOKING,
  SERVICE_APPOINTMENT_BOOKING_SUCCESS,
  SERVICE_SELF_PAYMENT,
  SERVICE_PAYMENT_FEEDBACK,
  SELF_PAYMENT_NOTICE_FEEDBACK,
  PERSONAL_AREA,
  PAYMENT_IUV,
  SERVICE_MOON,
  ERROR_404,
];

export default routes;
