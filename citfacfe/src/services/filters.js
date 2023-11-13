/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

const globalFilters = {
    stripHTML(value) {
        if(value) {
            return value.replace(/<\/?[^>]+>/ig, " ");
        }
        return ""
    },
    pad(value){
        let newValue = value.toString();
        while (newValue.length < 2) newValue = "0" + newValue;
        return newValue;
    }
}


export default  globalFilters
