/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import localeIt from "date-fns/locale/it";
import __format from "date-fns/format";
import { LOGIN_URL, LOGOUT_URL } from "./business-logic";

// DATE-FSN WRAPPERS
// @see: https://date-fns.org/v1.29.0/docs/I18n
// ---------------------------------------------------------------------------------------------------------------------
const DEFAULT_DATE_FNS_OPTIONS = { locale: localeIt };

export const format_date = (value, formatStr = "dd/MM/yyyy", options) => {
  if (value) {
    value = new Date(value);
    options = defaults(options, DEFAULT_DATE_FNS_OPTIONS);
    return __format(value, formatStr, options);
  }
};
export const capitalizeFirst = (value) => {
  if (value) {
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  }
};

// @see: https://30secondsofcode.org/object#defaults
export const defaults = (obj, ...defs) =>
  Object.assign({}, obj, ...defs.reverse(), obj);

// @see https://30secondsofcode.org/string#capitalize
export const capitalize = ([first, ...rest], lowerRest = false) => {
  return (
    first.toUpperCase() +
    (lowerRest ? rest.join("").toLowerCase() : rest.join(""))
  );
};

//CONVERSIONE BASE 64
export const toBase64 = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });

//CONYTROLLO VALIDITA CF
export const isValidCF = (cf) => {
  let validi, i, s, set1, set2, setpari, setdisp;
  if (!cf) return false;
  cf = cf.toUpperCase();
  if (cf.length !== 16) return false;
  validi = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  for (i = 0; i < 16; i++) {
    if (validi.indexOf(cf.charAt(i)) === -1) return false;
  }
  set1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  set2 = "ABCDEFGHIJABCDEFGHIJKLMNOPQRSTUVWXYZ";
  setpari = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  setdisp = "BAKPLCQDREVOSFTGUHMINJWZYX";
  s = 0;
  for (i = 1; i <= 13; i += 2)
    s += setpari.indexOf(set2.charAt(set1.indexOf(cf.charAt(i))));
  for (i = 0; i <= 14; i += 2)
    s += setdisp.indexOf(set2.charAt(set1.indexOf(cf.charAt(i))));
  if (s % 26 !== cf.charCodeAt(15) - "A".charCodeAt(0)) return false;

  return true;
};

//CREO UID UNIVOCO
export function createUid() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2);
}

export const getUri = (cfg) => {
  function isArray(val) {
    return toString.call(val) === "[object Array]";
  }

  function isURLSearchParams(val) {
    return (
      typeof URLSearchParams !== "undefined" && val instanceof URLSearchParams
    );
  }

  function isDate(val) {
    return toString.call(val) === "[object Date]";
  }

  function isObject(val) {
    return val !== null && typeof val === "object";
  }

  function forEach(obj, fn) {
    // Don't bother if no value provided
    if (obj === null || typeof obj === "undefined") {
      return;
    }

    // Force an array if not already something iterable
    if (typeof obj !== "object") {
      /*eslint no-param-reassign:0*/
      obj = [obj];
    }

    if (isArray(obj)) {
      // Iterate over array values
      for (var i = 0, l = obj.length; i < l; i++) {
        fn.call(null, obj[i], i, obj);
      }
    } else {
      // Iterate over object keys
      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          fn.call(null, obj[key], key, obj);
        }
      }
    }
  }

  function encode(val) {
    return encodeURIComponent(val)
      .replace(/%40/gi, "@")
      .replace(/%3A/gi, ":")
      .replace(/%24/g, "$")
      .replace(/%2C/gi, ",")
      .replace(/%20/g, "+")
      .replace(/%5B/gi, "[")
      .replace(/%5D/gi, "]");
  }

  function buildURL(url, params, paramsSerializer) {
    /*eslint no-param-reassign:0*/
    if (!params) {
      return url;
    }

    let serializedParams;
    if (paramsSerializer) {
      serializedParams = paramsSerializer(params);
    } else if (isURLSearchParams(params)) {
      serializedParams = params.toString();
    } else {
      var parts = [];

      forEach(params, function serialize(val, key) {
        if (val === null || typeof val === "undefined") {
          return;
        }

        if (isArray(val)) {
          key = key + "[]";
        } else {
          val = [val];
        }

        forEach(val, function parseValue(v) {
          if (isDate(v)) {
            v = v.toISOString();
          } else if (isObject(v)) {
            v = JSON.stringify(v);
          }
          parts.push(encode(key) + "=" + encode(v));
        });
      });

      serializedParams = parts.join("&");
    }

    if (serializedParams) {
      url += (url.indexOf("?") === -1 ? "?" : "&") + serializedParams;
    }

    return url;
  }

  return buildURL(cfg.url, cfg.params, cfg.paramsSerializer);
};

export const keyBy = (arr, fn) => {
  let result = {};
  arr.forEach((el) => {
    let key = fn(el);
    if (result.hasOwnProperty(key))
      console.warn("Overriding", result[key], "with", el);
    result[fn(el)] = el;
  });
  return result;
};

export const hasSelectedKey = (obj, key) => {
  return obj.hasOwnProperty(key);
};

export const isEmpty = (val) =>
  val == null || !(Object.keys(val) || val).length;

export const groupBy = (arr, fn) => {
  let result = {};

  arr.forEach((el) => {
    let key = fn(el);
    result[key] = result[key] || [];
    result[key].push(el);
  });

  return result;
};

// @see https://www.30secondsofcode.org/js/s/order-by
export const orderBy = (arr, props, orders) => {
  return [...arr]?.sort((a, b) =>
    props.reduce((acc, prop, i) => {
      if (acc === 0) {
        const [p1, p2] =
          orders && orders[i] === "desc"
            ? [b[prop], a[prop]]
            : [a[prop], b[prop]];
        acc = p1 > p2 ? 1 : p1 < p2 ? -1 : 0;
      }
      return acc;
    }, 0)
  );
};

export const login = (redirector) => {
  let baseUrl = window.location.origin;
  let loginRedirectUrl = `${baseUrl}/${LOGIN_URL}`;

  if(redirector){
    let url = encodeURIComponent=(redirector)  
    loginRedirectUrl += `?redirector=${url}`
  }

  try {
    location.assign(loginRedirectUrl);
  } catch (e) {
    window.open(loginRedirectUrl, "_self");
  }
};
export const logout = () => {
  let baseUrl = window.location.origin;
  let loginRedirectUrl = `${baseUrl}/${LOGOUT_URL}`;

  try {
    location.assign(loginRedirectUrl);
  } catch (e) {
    window.open(loginRedirectUrl, "_self");
  }
};

export const createSlug = (string) => {
  let slug = string?.toLowerCase() ?? "";
  return slug.replaceAll(" ", "-");
};


