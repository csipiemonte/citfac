/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import store from '../store/index'


export const onShowNotificationToast = async (toast) => {
  await store.dispatch('addNotificationToast', toast)
}

export const onHideNotificationToast = async (index) => {
  await store.dispatch('clearNotificationToast', index)
}

export const equals = (a, b) => {
  if (a === b) return true
  if (a instanceof Date && b instanceof Date) return a.getTime() === b.getTime()
  if (!a || !b || (typeof a !== 'object' && typeof b !== 'object')) return a === b
  if (a.prototype !== b.prototype) return false
  const keys = Object.keys(a)
  if (keys.length !== Object.keys(b).length) return false
  return keys.every((k) => equals(a[k], b[k]))
}

// @see https://www.30secondsofcode.org/js/s/order-by
export const orderBy = (arr, props, orders) => {
  return [...arr]?.sort((a, b) =>
    props.reduce((acc, prop, i) => {
      if (acc === 0) {
        const [p1, p2] = orders && orders[i] === 'desc' ? [b[prop], a[prop]] : [a[prop], b[prop]]
        acc = p1 > p2 ? 1 : p1 < p2 ? -1 : 0
      }
      return acc
    }, 0)
  )
}

export const createSlug = (string) => {
  let slug = string?.toLowerCase() ?? ''
  return slug.replaceAll(' ', '-')
}

export const slugToTitle = (string) => {
  let slug = string?.replaceAll('-', ' ')
  return capitalizeFirst(slug)
}

export const capitalizeFirst = (value) => {
  if (value) {
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase()
  }
}


export const floatWithDecimals = (num) =>{
  if(!num) return null
  return  parseFloat(num?.toFixed(2))  
}

export const calcPercent = (count, total) => {
  let percent = (count / total) * 100

  return percent.toFixed(2).replace(/[.,]00$/, '')
}


// @see https://www.30secondsofcode.org/js/s/capitalize-every-word/
export const capitalize = (str) => {
 return  str.toLowerCase().split(' ').map(word => word.charAt(0).toUpperCase() + word.substring(1)).join(' ');
};







// by convention, composable function names start with "use"
export const  useDownloadBlob = (blob, filename) =>{
  var URL = window.URL || window.webkitURL;
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.target   = '_blank';
  link.href = url;
  link.setAttribute(    //if you just want to preview pdf and dont want download delete this three lines 
    'download',
    filename, 
  );

  // Append to html link element page
  document.body.appendChild(link);

  // Start download
  link.click();

  // Clean up and remove the link
  link.parentNode.removeChild(link);
  URL.revokeObjectURL(url);
}
