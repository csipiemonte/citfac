/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { subDays, format } from 'date-fns'

export const ASSETS_MAP = {
  PAYMENTS: "PPAY",
  APPOINTMENTS: "APPSCHED",
  ASSISTANCE: "ASSISTENZA_CRM",
 
}


export const SERVICES_ASSETS_LIST = [ASSETS_MAP.APPOINTMENTS, ASSETS_MAP.PAYMENTS, ASSETS_MAP.ASSISTANCE]



export const FEEDBACK_TYPES_MAP = {
  PAGE: 'page',
  SERVICE: 'service'
}

export const FEEDBACK_ANSWERS_MAP = {
  positivo1: {
    label: 'Le indicazioni erano chiare',
    color: '#29a39b'
  },
  positivo2: {
    label: 'Le indicazioni erano complete',
    color: '#33ccc2'
  },
  positivo3: {
    label: 'Capivo sempre che stavo procedendo correttamente',
    color: '#5cd6ce'
  },
  positivo4: {
    label: 'Non ho avuto problemi tecnici',
    color: '#85e0da'
  },

  negativo1: {
    label: 'A volte le indicazioni non erano chiare',
    color: '#a32931'
  },
  negativo2: {
    label: 'A volte le indicazioni non erano complete',
    color: '#cc333d'
  },
  negativo3: {
    label: 'A volte non capivo se stavo procedendo correttamente',
    color: '#d65c64'
  },
  negativo4: {
    label: 'Ho avuto problemi tecnici',
    color: '#e0858b'
  },
  altro: {
    label: 'Altro',
    color: '#939f9e'
  }
}

export const ORDER_BY_MAP = {
  NAME: 'name',
  VOTE_HIGHT: 'voteHigh',
  VOTE_LOW: 'voteLow'
}

export const DEFAULT_FEEDBACK_PERIOD = {
  startDate: format(subDays(new Date(), 30), 'yyyy-MM-dd'),
  endDate: format(new Date(), 'yyyy-MM-dd')
}

export const FEEDBACK_COMMENTS_LIMIT = 20


