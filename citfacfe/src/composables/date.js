/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */


import { ref, onMounted, onUnmounted } from 'vue'

// by convention, composable function names start with "use"
export function useDate(date) {

    
  let dateFormatted = new Intl.DateTimeFormat('it-IT', { dateStyle: 'short' }).format(new Date(date));
  return dateFormatted 
}